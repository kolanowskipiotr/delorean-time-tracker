package pko.delorean.time.tracker.ui.work.day.dto

data class WorkDaysPeriodStatisticsDto(
    val statistics: Map<String, Long>? = null,
    val duration: Long? = null
)
