package pko.delorean.time.tracker.ui.work.day.form

data class WorkLogForm(
    val workDayId: Long? = null,
    val workLogId: Long? = null,
    val jiraIssueId: String? = null,
    val jiraIssueType: String? = null,
    val jiraIssueName: String? = null,
    val jiraIssueComment: String? = null,
    val started: String? = null, //FIXME: This should be Instant but i can`t force bootstrap to send time with date
    val ended: String? = null //FIXME: This should be Instant but i can`t force bootstrap to send time with date
)
