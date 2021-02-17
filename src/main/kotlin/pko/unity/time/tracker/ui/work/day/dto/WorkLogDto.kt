package pko.unity.time.tracker.ui.work.day.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.Instant
import java.time.LocalDate

data class WorkLogDto(
    val id: Long? = null,
    val jiraIssiueId: String,
    val started: String? = null, //FIXME: This should be Instant but i can`t force bootstrap to send time with date
    val ended: String? = null, //FIXME: This should be Instant but i can`t force bootstrap to send time with date
    val took: Long? = null,
    val jiraIssiueName: String? = null,
    val jiraIssiueComment: String? = null,
    val status: String? = null
)