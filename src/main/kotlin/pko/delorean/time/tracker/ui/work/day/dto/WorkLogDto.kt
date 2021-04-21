package pko.delorean.time.tracker.ui.work.day.dto
import org.apache.commons.lang3.StringUtils.capitalize
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogTypeDto.WORK_LOG
import java.net.URI

data class WorkLogDto(
    val id: Long? = null,
    val type: WorkLogTypeDto? = WORK_LOG,
    val jiraIssiueId: String,
    val jiraIssueType: JiraIssueTypeDto,
    val started: String? = null, //FIXME: This should be Instant but i can`t force bootstrap to send time with date
    val ended: String? = null, //FIXME: This should be Instant but i can`t force bootstrap to send time with date
    val breakTime: Long? = null,
    val duration: Long? = null,
    val jiraIssiueName: String? = null,
    val jiraIssiueComment: String? = null,
    val status: String? = null
) {
    companion object {
        fun breakDto(breakType: WorkLogTypeDto, started: String): WorkLogDto {
            val capitalizedName = capitalize(breakType.name.toLowerCase().replace("_", " "))
            return WorkLogDto(
                type = breakType,
                jiraIssiueId = capitalizedName,
                jiraIssueType = JiraIssueTypeDto(breakType.name),
                started = started,
                jiraIssiueName = capitalizedName
            )
        }
    }
}

enum class WorkLogTypeDto {
    WORK_LOG,//👷
    BREAK,//🏖
    WORK_ORGANIZATION,//🗄
    PRIVATE_WORK_LOG//🏡
}

data class JiraIssueTypeDto @JvmOverloads constructor(
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