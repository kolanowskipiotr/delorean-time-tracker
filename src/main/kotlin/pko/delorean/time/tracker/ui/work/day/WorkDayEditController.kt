package pko.delorean.time.tracker.ui.work.day

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pko.delorean.time.tracker.application.WorkDayService
import pko.delorean.time.tracker.domain.JiraIssueType
import pko.delorean.time.tracker.infrastructure.JiraService
import pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogDto
import pko.delorean.time.tracker.ui.work.day.form.WorkLogForm
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.*

@Controller
@RequestMapping("/work-day")
class WorkDayEditController(
    private val workDayService: WorkDayService,
    private val jiraService: JiraService
) {

    companion object {
        private const val WORK_DAY_ID_PARAM = "workDayId"

        const val URL = "/work-day/edit"

        fun editUrl(workDayId: Long) =
            "$URL?$WORK_DAY_ID_PARAM=$workDayId"
    }

    @GetMapping("/edit")
    fun showWorkDay(
        @RequestParam(name = WORK_DAY_ID_PARAM) workDayId: Long,
        @RequestParam(name = "searchedWorkLogId", required = false) searchedWorkLogId: Long?,
        @RequestParam(name = "searchedJiraIssueId", required = false) searchedJiraIssueId: String?,
        @RequestParam(name = "searchedJiraIssueType", required = false) searchedJiraIssueType: String?,
        @RequestParam(name = "searchedJiraIssueName", required = false) searchedJiraIssueName: String?,
        @RequestParam(name = "searchedJiraIssueComment", required = false) searchedJiraIssueComment: String?,
        @RequestParam(name = "searchedWorkLogStart", required = false) searchedWorkLogStart: String?,
        @RequestParam(name = "searchedWorkLogEnd", required = false) searchedWorkLogEnd: String?,
        @RequestParam(name = "searchedWorkLogStatus", required = false) searchedWorkLogStatus: String?,
        @RequestParam(name = "success", required = false) success: Boolean?,
        @RequestParam(name = "message", required = false) message: String?,
        model: Model
    ): String {
        model.addAttribute("random", Random())
        model.addAttribute("workDay", workDayService.getWorkDay(workDayId))
        model.addAttribute("workDayBefore", workDayService.findWorkDayBefore(workDayId))

        model.addAttribute("jiraUrl", jiraService.credentials()?.jiraUrl)
        model.addAttribute("searchedWorkLogId", searchedWorkLogId)
        model.addAttribute("searchedJiraIssueId", searchedJiraIssueId)
        model.addAttribute("searchedJiraIssueType", searchedJiraIssueType)
        model.addAttribute("searchedJiraIssueName", searchedJiraIssueName)
        model.addAttribute("searchedJiraIssueComment", searchedJiraIssueComment)
        model.addAttribute("searchedWorkLogStart", searchedWorkLogStart)
        model.addAttribute("searchedWorkLogEnd", searchedWorkLogEnd)
        model.addAttribute("searchedWorkLogStatus", searchedWorkLogStatus)

        model.addAttribute("workLogIdsInConflict", workDayService.workLogInConflictIds(workDayId))

        if(success != null) {
            model.addAttribute("connectionResult",
                if (success) JiraService.ConnectionResult.success(message)
                else JiraService.ConnectionResult.error(message)
            )
        }
        return URL
    }

    @PostMapping("/edit")
    fun updateWorkDay(
        @RequestParam(name = WORK_DAY_ID_PARAM) workDayId: Long,
        @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): String {
        workDayService.updateWorkDay(workDayId, date)
        return  "redirect:${editUrl(workDayId)}"
    }

    @GetMapping("/stop")//FIXME: This should be Patch
    fun stopWorkDay(@RequestParam(name = WORK_DAY_ID_PARAM) workDayId: Long): String {
        workDayService.stopWorklog(workDayId)
        return  "redirect:${editUrl(workDayId)}"
    }

    @GetMapping("/export")//FIXME: This should be Patch
    fun exportWorkDay(@RequestParam(name = WORK_DAY_ID_PARAM) workDayId: Long): String {
        val exportStatus = workDayService.exportWorkDay(workDayId)
        val urlEncodedMessage = URLEncoder.encode(exportStatus.message?:"Error", StandardCharsets.UTF_8.toString())
        return "redirect:$URL?workDayId=$workDayId&success=${exportStatus.success}&message=$urlEncodedMessage"
    }

    @PostMapping("/work-log")
    fun addWorkLog(
        workLogform: WorkLogForm
    ): String {
        workDayService.addWorkLog(workLogform.workDayId!!, workLogform.toDto())
        return  "redirect:${editUrl(workLogform.workDayId)}"
    }

    @PostMapping("/work-log/edit")
    fun editWorkLog(
        workLogform: WorkLogForm
    ): String {
        workDayService.editWorkLog(workLogform.workDayId!!, workLogform.toDto())
        return  "redirect:${editUrl(workLogform.workDayId)}"
    }

    @PostMapping("/work-log/delete")
    fun deleteReceiver(
        @RequestParam(name = WORK_DAY_ID_PARAM) workDayId: Long,
        @RequestParam(name = "workLogId") workLogId: Long
    ): String {
        workDayService.removeWorkLog(workDayId, workLogId)
        return  "redirect:${editUrl(workDayId)}"
    }

    @GetMapping("/work-log/start")//FIXME: This should be Patch
    fun startWorkLog(
        @RequestParam(name = WORK_DAY_ID_PARAM) workDayId: Long,
        @RequestParam(name = "workLogId") workLogId: Long
    ): String {
        workDayService.startWorkLog(workDayId, workLogId)
        return  "redirect:${editUrl(workDayId)}"
    }

    @GetMapping("/work-log/continue")//FIXME: This should be Patch
    fun continueWorkLog(
        @RequestParam(name = WORK_DAY_ID_PARAM) workDayId: Long,
        @RequestParam(name = "workLogId") workLogId: Long
    ): String {
        workDayService.continueWorkLog(workDayId, workLogId)
        return  "redirect:${editUrl(workDayId)}"
    }

    @GetMapping("/work-log/export/toggle")//FIXME: This should be Patch
    fun toggleExportWorkLog(
        @RequestParam(name = WORK_DAY_ID_PARAM) workDayId: Long,
        @RequestParam(name = "workLogId") workLogId: Long
    ): String {
        workDayService.toggleExport(workDayId, workLogId)
        return "redirect:${editUrl(workDayId)}"
    }

    private fun WorkLogForm.toDto() =
        WorkLogDto(workLogId, jiraIssueId!!, JiraIssueTypeDto(jiraIssueType!!), started, ended, jiraIssiueName = jiraIssueName, jiraIssiueComment = jiraIssueComment )
}
