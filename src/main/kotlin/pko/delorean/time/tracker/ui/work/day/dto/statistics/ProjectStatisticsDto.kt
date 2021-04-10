package pko.delorean.time.tracker.ui.work.day.dto.statistics

import pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto

data class ProjectStatisticsDto(
    val projectKey: String,
    val duration: Long,
    val issuesStatistics: List<IssueStatisticsDto>
)

data class IssueStatisticsDto(
    val issueKey: String,
    val duration: Long,
    val jiraIssues: List<JiraIssueDto>
) {
    fun distinctJiraIssuesByName(): List<JiraIssueDto> =
        jiraIssues.distinctBy { it.jiraName }
}

data class JiraIssueDto(
    val jiraName: String,
    val jiraIssueType: JiraIssueTypeDto
)
