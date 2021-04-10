package pko.delorean.time.tracker.ui.jira.dto

import org.apache.commons.lang3.StringUtils
import java.net.URI

data class JiraIssueDto(
    val jiraId: String,
    val jiraName: String,
    val type: JiraIssueTypeDto,
    val comment: String? = null
)

data class JiraIssueTypeDto(
    val name: String,
    val self: URI? = null,
    val id: Long? = null,
    val iconUri: URI? = null,
    val description: String? = null,
    val subtask: Boolean? = null
)