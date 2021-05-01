package pko.delorean.time.tracker.ui.work.day.dto.statistics

data class WorkDaysPeriodStatisticsDto(
    val duration: Long? = null,
    val projectsStatistics: List<ProjectStatisticsDto>? = null,
    val privateTime: IssueStatisticsDto? = null
) {
    fun getFullDuration() =
        (duration?:0) + (privateTime?.duration?:0)
}
