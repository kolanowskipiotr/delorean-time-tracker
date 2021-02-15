package pko.unity.time.tracker.infrastructure

import org.springframework.stereotype.Repository
import org.springframework.web.context.annotation.ApplicationScope
import pko.unity.time.tracker.ui.jira.dto.JiraCredentialsDto
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.RestClientException
import com.atlassian.jira.rest.client.api.SearchRestClient
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.ServerInfo
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import pko.unity.time.tracker.ui.jira.dto.JiraIssueDto
import java.lang.Exception
import java.net.URI

@Repository
@ApplicationScope
class JiraRepository {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.javaClass)
    private var jiraCredentials: JiraCredentialsDto? = null

    fun updateCredential(jiraCredentials: JiraCredentialsDto) {
        this.jiraCredentials = jiraCredentials
    }

    fun credentialsAreValid(): Boolean =
        findJiraServerInfo().success

    fun findJiraServerInfo(): ConnectionResult {
        var serverInfo: ServerInfo?
        try {
            val restClient: JiraRestClient = buildJiraClient()
            serverInfo = restClient.metadataClient.serverInfo.get()
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            return ConnectionResult.error(e.message)
        }
        return ConnectionResult.succes(serverInfo.toString())
    }

    private fun buildJiraClient(): JiraRestClient {
        val factory = AsynchronousJiraRestClientFactory()
        val restClient: JiraRestClient =
            factory.createWithBasicHttpAuthentication(
                URI(jiraCredentials!!.jiraUrl),//FIXME: Handle sytuation without !!
                jiraCredentials!!.jiraUserName,
                jiraCredentials!!.jiraUserPassword
            )
        return restClient
    }

    data class ConnectionResult(val success: Boolean, val message: String?) {
        companion object {
            fun error(message: String?) =
                ConnectionResult(false, message)

            fun succes(message: String?) =
                ConnectionResult(true, message)
        }
    }

    fun credentials(): JiraCredentialsDto? =
        this.jiraCredentials

    fun findJiraIssues(userQuery: String): List<JiraIssueDto> {
        try {
            val jiraClient = buildJiraClient()
            val jiraQuery = buildJiraQuery(jiraClient, userQuery)
            val searchClient = jiraClient.searchClient
            return searchByIssueKey(searchClient, userQuery)
                .plus(searchClient.searchJql(jiraQuery).claim().issues)
                .map { JiraIssueDto(it.key, it.summary) }
        } catch (e: Exception) { //FIXME: Catch only important exceptions
            logger.error(e.message, e)
        }
        return listOf()
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
        }
        else if ( issiueTypes.contains(word.toUpperCase())) {
            return "issuetype = " + word.toLowerCase()
        }

        return "(assignee = '$word' OR text ~ '$word*')"
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

    private fun searchByIssueKey(searchClient: SearchRestClient, query: String): MutableList<Issue> {
        val keyQuery = "issuekey = \"${query.toUpperCase()}\""
        try {
            return searchClient.searchJql(keyQuery, 1, null, null).claim().issues.toMutableList()
        } catch (e: RestClientException) {
            logger.error(e.message, e)
        }
        return mutableListOf()
    }
}