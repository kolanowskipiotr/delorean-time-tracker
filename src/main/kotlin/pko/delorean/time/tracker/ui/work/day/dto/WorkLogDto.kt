package pko.delorean.time.tracker.ui.work.day.dto
import java.net.URI

data class WorkLogDto(
    val id: Long? = null,
    val jiraIssiueId: String,
    val jiraIssueType: JiraIssueTypeDto,
    val started: String? = null, //FIXME: This should be Instant but i can`t force bootstrap to send time with date
    val ended: String? = null, //FIXME: This should be Instant but i can`t force bootstrap to send time with date
    val duration: Long? = null,
    val jiraIssiueName: String? = null,
    val jiraIssiueComment: String? = null,
    val status: String? = null
)

data class JiraIssueTypeDto(
    val name: String,
    val self: URI? = null,
    val id: Long? = null,
    val iconUri: URI? = null,
    val description: String? = null,
    val subtask: Boolean? = null
) {
    companion object{
        fun buildJava(name: String) = JiraIssueTypeDto(name)
    }
}