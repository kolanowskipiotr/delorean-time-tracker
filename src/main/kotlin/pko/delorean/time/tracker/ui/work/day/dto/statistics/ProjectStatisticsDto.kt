package pko.delorean.time.tracker.ui.work.day.dto.statistics

import pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogTypeDto

data class ProjectStatisticsDto(
    val projectKey: String,
    val duration: Long,
    val issuesStatistics: List<IssueStatisticsDto>
) {
    companion object {
        fun fromMultipleStatistics(projectKey: String, projectStatistics: List<ProjectStatisticsDto>): ProjectStatisticsDto =
            ProjectStatisticsDto(
                projectKey,
                projectStatistics.map { it.duration }.sum(),
                projectStatistics.flatMap { it.issuesStatistics }
                    .groupBy { it.issueKey }
                    .map {
                        IssueStatisticsDto(
                            it.key,
                            it.value.map { issueStats -> issueStats.duration }.sum(),
                            it.value.flatMap { issueStats -> issueStats.jiraIssues })}
                    .sortedByDescending { it.duration }
            )
    }
}

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
    val jiraIssueType: JiraIssueTypeDto,
    val workLogType: WorkLogTypeDto
)
