package pko.delorean.time.tracker.ui.work.day.dto

data class ProjectStatisticsDto(
    val projectKey: String,
    val duration: Long,
    val issuesStatistics: List<IssueStatisticsDto>
)

data class IssueStatisticsDto(
    val issueKey: String,
    val duration: Long,
    val jiraNames: Set<String>
)
