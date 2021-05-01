package pko.delorean.time.tracker.ui.work.day.dto

import org.springframework.format.annotation.DateTimeFormat
import pko.delorean.time.tracker.ui.work.day.dto.statistics.IssueStatisticsDto
import pko.delorean.time.tracker.ui.work.day.dto.statistics.ProjectStatisticsDto
import pko.delorean.time.tracker.ui.work.day.dto.summary.IssueSummaryDto
import java.time.LocalDate

data class WorkDayDto(
    val id: Long? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val date: LocalDate,
    val state: String? = null,
    val duration: Long? = null,
    val statistics: WorkDayStatisticsDto? = null,
    val summary: List<IssueSummaryDto>? = null,
    val workLogs: List<WorkLogDto>? = null
)

data class WorkDayStatisticsDto(
    val duration: Long,
    val projectsStatistics: List<ProjectStatisticsDto>,
    val privateTime: IssueStatisticsDto
) {
    fun getFullDuration() =
        duration + privateTime.duration
}