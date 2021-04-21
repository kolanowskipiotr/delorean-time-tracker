package pko.delorean.time.tracker.infrastructure

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.RestClientException
import com.atlassian.jira.rest.client.api.SearchRestClient
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.IssueType
import com.atlassian.jira.rest.client.api.domain.ServerInfo
import com.atlassian.jira.rest.client.api.domain.input.WorklogInputBuilder
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.defaultString
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.web.context.annotation.ApplicationScope
import pko.delorean.time.tracker.domain.dto.ExportableWorkLog
import pko.delorean.time.tracker.infrastructure.model.IssiueTypes
import pko.delorean.time.tracker.kernel.Utils.Companion.buildDateTimeInstant
import pko.delorean.time.tracker.ui.jira.dto.JiraCredentialsDto
import pko.delorean.time.tracker.ui.jira.dto.JiraIssueDto
import pko.delorean.time.tracker.ui.jira.dto.JiraIssueTypeDto
import java.net.URI
import java.time.Instant.now
import java.time.LocalDate
import java.time.temporal.ChronoUnit.MINUTES
import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import jdk.internal.joptsimple.internal.Strings.EMPTY

@Repository
@ApplicationScope
class JiraService {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.javaClass)
    private var jiraCredentials: JiraCredentialsDto? = null

    fun updateCredential(jiraCredentials: JiraCredentialsDto) {
        this.jiraCredentials = jiraCredentials
    }

    fun credentialsAreValid(): Boolean =
        findJiraServerInfo().success

    fun findJiraServerInfo(): ConnectionResult<String> {
        val serverInfo: ServerInfo?
        try {
            val restClient: JiraRestClient = buildJiraClient()
            serverInfo = restClient.metadataClient.serverInfo.get()
            restClient.close()
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            logger.error(e.message, e)
            return ConnectionResult.error(e.toString() + defaultString(e.message, e.javaClass.name))
        }
        return ConnectionResult.success(serverInfo.toString())
    }

    fun exportWorkDay(exportableWorkLogs: Set<ExportableWorkLog>): ConnectionResult<List<Long>> {
        val exportedWorkLogIds = mutableListOf<Long>()
        val unexportedWorkLOgs = mutableListOf<ExportableWorkLog>()
        try {
            val restClient: JiraRestClient = buildJiraClient()
            exportableWorkLogs.forEach { exportableWorkLog: ExportableWorkLog ->
                val workLogExportResult = exportWorkLog(restClient, exportableWorkLog)
                when(workLogExportResult.success) {
                    true -> exportedWorkLogIds.add(workLogExportResult.value!!)
                    false -> unexportedWorkLOgs.add(exportableWorkLog)
                }
            }
            restClient.close()
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            logger.error(e.message, e)
            return ConnectionResult.error(exportedWorkLogIds, e.toString() + defaultString(e.message, e.javaClass.name))
        }
        return ConnectionResult.success(
            exportedWorkLogIds,
            buildUnexportedWorklogsMessage(unexportedWorkLOgs) + "<br>" +
                    buildExportedWorkLogsMessage(exportableWorkLogs, exportedWorkLogIds)
        )
    }

    fun credentials(): JiraCredentialsDto? =
        this.jiraCredentials

    fun findJiraIssues(userQuery: String): ConnectionResult<List<JiraIssueDto>> {
        val foundJiraIssues: List<JiraIssueDto>
        try {
            val jiraClient = buildJiraClient()
            val jiraQuery = buildJiraQuery(userQuery)
            val searchClient = jiraClient.searchClient
            foundJiraIssues = searchByIssueKey(searchClient, userQuery)
                .plus(searchClient.searchJql(jiraQuery).claim().issues)
                .map { JiraIssueDto(it.key, it.summary,  it.issueType.toDto()) }
            jiraClient.close()
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            logger.error(e.message, e)
            return ConnectionResult.error(listOf(), e.toString() + defaultString(e.message, e.javaClass.name))
        }
        return ConnectionResult.success(foundJiraIssues)
    }

    fun getIssueTypesCached(): IssiueTypes =
        getIssueTypesCache.get("getIssueTypes")

    private var getIssueTypesCache: LoadingCache<String, IssiueTypes> =
        CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build(object : CacheLoader<String, IssiueTypes>() {
                @Throws(java.lang.Exception::class)
                override fun load(key: String): IssiueTypes {
                    val jiraClient = buildJiraClient()
                    return getIssueTypes(jiraClient)
                }
            })
    private fun getIssueTypes(jiraClient: JiraRestClient): IssiueTypes =
        IssiueTypes(jiraClient.metadataClient.issueTypes.claim().map { it.toDto() })

    fun getIssueType(jiraIssueType: String): JiraIssueTypeDto {
        try {
            return getIssueTypesCached().getType(jiraIssueType)
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            logger.error(e.message, e)
        }
        return IssiueTypes().getType(jiraIssueType)
    }

    fun getAllProjectNamesCached():  List<String> =
        allProjectNamesCache.get("getAllProjectNames")
    private var allProjectNamesCache: LoadingCache<String,  List<String>> =
        CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build(object : CacheLoader<String,  List<String>>() {
                @Throws(java.lang.Exception::class)
                override fun load(key: String): List<String> {
                    val jiraClient = buildJiraClient()
                    return getAllProjectNames(jiraClient)
                }
            })
    private fun getAllProjectNames(jiraClient: JiraRestClient) =
        jiraClient.projectClient.allProjects.claim()
            .map { it.key }
            .map { it.toUpperCase() }

    private fun exportWorkLog(restClient: JiraRestClient, exportableWorkLog: ExportableWorkLog): ConnectionResult<Long> {
        try {
            val workLog = exportableWorkLog.worklog
            val issueClient = restClient.issueClient
            val issue = issueClient.getIssue(workLog.jiraId).claim()

            val defaultWorkLogEnd = buildDateTimeInstant(exportableWorkLog.date, now().truncatedTo(MINUTES))
            val worklogInput = WorklogInputBuilder(issue.self)
                .setStartDate(exportableWorkLog.date.toDateTime())
                .setComment(exportableWorkLog.comment)
                .setMinutesSpent(
                    (exportableWorkLog.worklog.getDuration(defaultWorkLogEnd) + exportableWorkLog.breakDurationInMinutes).toInt()
                )
                .build()
            val result = issueClient.addWorklog(issue.worklogUri, worklogInput)
            result.claim()
            return ConnectionResult.success(workLog.id)
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            logger.error(e.message, e)
            return ConnectionResult.error()
        }
    }

    private fun buildExportedWorkLogsMessage(exportableWorkLogs: Set<ExportableWorkLog>, exportedWorkLogIds: MutableList<Long>) =
        if(exportedWorkLogIds.isNotEmpty())
            "Exported: " + exportableWorkLogs
                .filter { exportableWorkLog -> exportedWorkLogIds.contains(exportableWorkLog.worklog.id) }
                .map { it.worklog.jiraId }
                .joinToString(", ")
        else
            EMPTY

    private fun buildUnexportedWorklogsMessage(unexportedWorkLOgs: MutableList<ExportableWorkLog>) =
        if(unexportedWorkLOgs.isNotEmpty())
            "Export manually or try again: " + unexportedWorkLOgs.map { it.unexportedMessage() }.joinToString { ", " }
        else
            EMPTY

    private fun ExportableWorkLog.unexportedMessage(): String {
        val defaultWorkLogEnd = buildDateTimeInstant(this.date, now().truncatedTo(MINUTES))
        return this.worklog.jiraId + " " + (this.worklog.getDuration(defaultWorkLogEnd) + this.breakDurationInMinutes).toInt() + "m"
    }

    private fun LocalDate.toDateTime(): DateTime? {
        return DateTime(DateTimeZone.UTC).withDate(
            this.year, this.monthValue, this.dayOfMonth
        ).withTime(0, 0, 0, 0)
    }

    private fun buildJiraQuery(
        userQuery: String
    ): String {
        val projectNames = getAllProjectNamesCached()
        val issueTypes = getIssueTypesCached()
        val jiraQuery = userQuery.split(" ")
            .map { it.trim() }
            .joinToString(
                separator = " AND ",
                transform = { buildQueryPart(it, projectNames, issueTypes) }) +
                " AND resolution = Unresolved order by updated desc, priority desc"
        return jiraQuery
    }

    private fun buildQueryPart(word: String, projectNames: List<String>, issueTypes: IssiueTypes): String {
        if (projectNames.contains(word.toUpperCase())) {
            return "project = " + word.toUpperCase()
        } else if (issueTypes.contains(word)) {
            return "issuetype = " + word.toLowerCase()
        }

        return "(assignee = '$word' OR text ~ '$word*')"
    }

    private fun searchByIssueKey(searchClient: SearchRestClient, query: String): MutableList<Issue> {
        val keyQuery = "issuekey = \"${query.toUpperCase()}\""
        try {
            return searchClient.searchJql(keyQuery, 1, null, null).claim().issues.toMutableList()
        } catch (e: RestClientException) {
            logger.error(e.message, e)
        }
        return mutableListOf()
    }

    private fun buildJiraClient(): JiraRestClient {
        val factory = AsynchronousJiraRestClientFactory()
        val restClient: JiraRestClient =
            factory.createWithBasicHttpAuthentication(
                URI(jiraCredentials!!.jiraUrl!!),//FIXME: Handle situation without !!
                jiraCredentials!!.jiraUserName,
                jiraCredentials!!.jiraUserPassword
            )
        return restClient
    }

    data class ConnectionResult<V>(
        val success: Boolean,
        val value: V? = null,
        val message: String? = null
    ) {
        companion object {
            fun <V> error(value: V? = null, message: String? = null) =
                ConnectionResult(false, value, StringUtils.abbreviate(message, 1380))

            fun <V> success(value: V?, message: String? = null) =
                ConnectionResult(true, value, StringUtils.abbreviate(message, 1380))
        }
    }

    private fun IssueType.toDto() =
        JiraIssueTypeDto(name, self, id, iconUri, description, isSubtask)

}
