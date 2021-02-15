package pko.unity.time.tracker.ui.work.day.dto

import java.time.Instant

data class WorkLogDto(
    val id: Long? = null,
    val jiraIssiueId: String,
    val started: Instant? = null,
    val ended: Instant? = null,
    val took: Long? = null,
    val jiraIssiueName: String? = null,
    val jiraIssiueComment: String? = null
)