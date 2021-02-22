package pko.delorean.time.tracker.infrastructure

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.RestClientException
import com.atlassian.jira.rest.client.api.SearchRestClient
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.ServerInfo
import com.atlassian.jira.rest.client.api.domain.input.WorklogInputBuilder
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.defaultString
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import org.springframework.web.context.annotation.ApplicationScope
import pko.delorean.time.tracker.domain.dto.ExportableWorkLog
import pko.delorean.time.tracker.kernel.Utils.Companion.buildDateTimeInstant
import pko.delorean.time.tracker.ui.jira.dto.JiraCredentialsDto
import pko.delorean.time.tracker.ui.jira.dto.JiraIssueDto
import java.net.URI
import java.time.Instant.now
import java.time.LocalDate
import java.time.temporal.ChronoUnit.MINUTES


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
        var serverInfo: ServerInfo?
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
        try {
            val restClient: JiraRestClient = buildJiraClient()
            exportableWorkLogs.forEach { exportableWorkLog ->
                exportedWorkLogIds.add(exportWorkLog(restClient, exportableWorkLog))
            }
            restClient.close()
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            logger.error(e.message, e)
            return ConnectionResult.error(exportedWorkLogIds, e.toString() + defaultString(e.message, e.javaClass.name))
        }
        return ConnectionResult.success(
            exportedWorkLogIds,
            exportableWorkLogs
                .filter { exportableWorkLog -> exportedWorkLogIds.contains(exportableWorkLog.worklog.id) }
                .map { "${it.worklog.jiraId} - ${it.comment}" }
                .joinToString("<br>" )
        )
    }

    fun credentials(): JiraCredentialsDto? =
        this.jiraCredentials

    fun findJiraIssues(userQuery: String): ConnectionResult<List<JiraIssueDto>> {
        var foundJiraIssues = listOf<JiraIssueDto>()
        try {
            val jiraClient = buildJiraClient()
            val jiraQuery = buildJiraQuery(jiraClient, userQuery)
            val searchClient = jiraClient.searchClient
            foundJiraIssues = searchByIssueKey(searchClient, userQuery)
                .plus(searchClient.searchJql(jiraQuery).claim().issues)
                .map { JiraIssueDto(it.key, it.summary) }
            jiraClient.close()
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            logger.error(e.message, e)
            return ConnectionResult.error(listOf(), e.toString() + defaultString(e.message, e.javaClass.name))
        }
        return ConnectionResult.success(foundJiraIssues)
    }

    @Cacheable("getAllIssiueTypes")
    fun getAllIssiueTypes(jiraClient: JiraRestClient): List<String> =
        jiraClient.metadataClient.issueTypes.claim()
            .map { it.name }
            .map { it.toUpperCase() }

    @Cacheable("getAllProjectNames")
    fun getAllProjectNames(jiraClient: JiraRestClient) =
        jiraClient.projectClient.allProjects.claim()
            .map { it.key }
            .map { it.toUpperCase() }

    private fun exportWorkLog(restClient: JiraRestClient, exportableWorkLog: ExportableWorkLog): Long {
        val workLog = exportableWorkLog.worklog
        val issueClient = restClient.issueClient
        val issue = issueClient.getIssue(workLog.jiraId).claim()

        val worklogInput = WorklogInputBuilder(issue.self)
            .setStartDate(exportableWorkLog.date.toDateTime())
            .setComment(exportableWorkLog.comment)
            .setMinutesSpent(
                exportableWorkLog.worklog.getDuration(
                    buildDateTimeInstant(exportableWorkLog.date, now().truncatedTo(MINUTES))
                ).toInt()
            )
            .build()
        val result = issueClient.addWorklog(issue.worklogUri, worklogInput)
        result.claim()

        return workLog.id
    }

    private fun LocalDate.toDateTime(): DateTime? {
        return DateTime(DateTimeZone.UTC).withDate(
            this.year, this.monthValue, this.dayOfMonth
        ).withTime(0, 0, 0, 0)
    }

    private fun buildJiraQuery(
        jiraClient: JiraRestClient,
        userQuery: String
    ): String {
        val projectNames: List<String> = getAllProjectNames(jiraClient)
        val issueTypes: List<String> = getAllIssiueTypes(jiraClient)
        val jiraQuery = userQuery.split(" ")
            .map { it.trim() }
            .joinToString(
                separator = " AND ",
                transform = { buildQueryPart(it, projectNames, issueTypes) }) +
                " AND resolution = Unresolved order by updated desc, priority desc"
        return jiraQuery
    }

    private fun buildQueryPart(word: String, projectNames: List<String>, issiueTypes: List<String>): String {
        if (projectNames.contains(word.toUpperCase())) {
            return "project = " + word.toUpperCase()
        } else if (issiueTypes.contains(word.toUpperCase())) {
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
                URI(jiraCredentials!!.jiraUrl),//FIXME: Handle situation without !!
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
            fun <V> error(value: V?, message: String? = null) =
                ConnectionResult(false, value, StringUtils.abbreviate(message, 200))

            fun <V> success(value: V?, message: String? = null) =
                ConnectionResult(true, value, StringUtils.abbreviate(message, 200))
        }
    }
}