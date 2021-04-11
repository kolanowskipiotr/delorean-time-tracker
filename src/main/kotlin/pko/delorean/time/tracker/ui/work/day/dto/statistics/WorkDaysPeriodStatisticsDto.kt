package pko.delorean.time.tracker.ui.work.day.dto.statistics

data class WorkDaysPeriodStatisticsDto(
    val statistics: List<ProjectStatisticsDto>? = null,
    val duration: Long? = null
)
