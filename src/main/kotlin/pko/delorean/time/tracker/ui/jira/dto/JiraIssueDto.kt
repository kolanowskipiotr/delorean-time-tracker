package pko.delorean.time.tracker.ui.jira.dto

data class JiraIssueDto(
    val jiraId: String,
    val jiraName: String,
    val comment: String? = null
)