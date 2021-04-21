package pko.delorean.time.tracker.ui.work.day.dto.summary

import pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogTypeDto

data class IssueSummaryDto(
    val jiraId: String,
    val jiraIssues: List<JiraIssueDto>
 ) {
    fun distinctJiraIssuesByName(): List<JiraIssueDto> =
        jiraIssues.distinctBy { it.jiraName }

    fun distinctJiraIssuesByComment(): List<JiraIssueDto> =
        jiraIssues.distinctBy { it.jiraComment }
}

data class JiraIssueDto(
    val jiraName: String,
    val jiraIssueType: JiraIssueTypeDto,
    val workLogType: WorkLogTypeDto,
    val jiraComment: String? = null
)