package pko.unity.time.tracker.ui.work.day.dto

import org.springframework.format.annotation.DateTimeFormat
import pko.unity.time.tracker.domain.WorkLog
import java.time.LocalDate

data class WorkDayDto(
    val id: Long? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val date: LocalDate,
    val state: String? = null,
    val duration: Long? = null,
    val statistics: Map<String, Long>? = null,
    val workLogs: List<WorkLogDto>? = null
)