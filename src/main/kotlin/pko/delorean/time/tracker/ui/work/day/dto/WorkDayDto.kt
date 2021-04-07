package pko.delorean.time.tracker.ui.work.day.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class WorkDayDto(
    val id: Long? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val date: LocalDate,
    val state: String? = null,
    val duration: Long? = null,
    val statistics: Map<String, Long>? = null,
    val summary: List<IssueSummaryDto>? = null,
    val workLogs: List<WorkLogDto>? = null
)