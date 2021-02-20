package pko.delorean.time.tracker.ui.work.day

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pko.delorean.time.tracker.application.WorkDayService
import pko.delorean.time.tracker.infrastructure.JiraService
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogDto
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

@Controller
@RequestMapping("/work-day")
class WorkDayEditController(
    private val workDayService: WorkDayService,
    private val jiraService: JiraService
) {

    @GetMapping("/edit")
    fun showWorkDay(
        @RequestParam(name = "workDayId") workDayId: Long,
        @RequestParam(name = "searchedWorkLogId", required = false) searchedWorkLogId: Long?,
        @RequestParam(name = "searchedJiraIssueId", required = false) searchedJiraIssueId: String?,
        @RequestParam(name = "searchedJiraIssueName", required = false) searchedJiraIssueName: String?,
        @RequestParam(name = "searchedJiraIssueComment", required = false) searchedJiraIssueComment: String?,
        @RequestParam(name = "searchedWorkLogStart", required = false) searchedWorkLogStart: String?,
        @RequestParam(name = "searchedWorkLogEnd", required = false) searchedWorkLogEnd: String?,
        @RequestParam(name = "success", required = false) success: Boolean?,
        @RequestParam(name = "message", required = false) message: String?,
        model: Model
    ): String {
        model.addAttribute("workDay", workDayService.getWorkDay(workDayId))

        model.addAttribute("jiraUrl", jiraService.credentials()?.jiraUrl)
        model.addAttribute("searchedWorkLogId", searchedWorkLogId)
        model.addAttribute("searchedJiraIssueId", searchedJiraIssueId)
        model.addAttribute("searchedJiraIssueName", searchedJiraIssueName)
        model.addAttribute("searchedJiraIssueComment", searchedJiraIssueComment)
        model.addAttribute("searchedWorkLogStart", searchedWorkLogStart)
        model.addAttribute("searchedWorkLogEnd", searchedWorkLogEnd)

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
        @RequestParam(name = "workDayId") workDayId: Long,
        @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): String {
        workDayService.updateWorkDay(workDayId, date)
        return "redirect:$URL?workDayId=$workDayId"
    }

    @GetMapping("/stop")//FIXME: This should be Patch
    fun stopWorkDay(@RequestParam(name = "workDayId") workDayId: Long): String {
        workDayService.stopWorklog(workDayId)
        return "redirect:$URL?workDayId=$workDayId"
    }

    @GetMapping("/export")//FIXME: This should be Patch
    fun exportWorkDay(@RequestParam(name = "workDayId") workDayId: Long): String {
        val exportStatus = workDayService.exportWorkDay(workDayId)
        val urlEncodedMessage = URLEncoder.encode(exportStatus.message?:"Error", StandardCharsets.UTF_8.toString())
        return "redirect:$URL?workDayId=$workDayId&success=${exportStatus.success}&message=$urlEncodedMessage"
    }

    @PostMapping("/work-log")
    fun addWorkLog(
        @RequestParam(name = "workDayId") workDayId: Long,
        workLog: WorkLogDto
    ): String {
        workDayService.addWorkLog(workDayId, workLog)
        return "redirect:$URL?workDayId=$workDayId"
    }

    @PostMapping("/work-log/edit")
    fun editWorkLog(
        @RequestParam(name = "workDayId") workDayId: Long,
        workLog: WorkLogDto
    ): String {
        workDayService.editWorkLog(workDayId, workLog)
        return "redirect:$URL?workDayId=$workDayId"
    }

    @PostMapping("/work-log/delete")
    fun deleteReceiver(
        @RequestParam(name = "workDayId") workDayId: Long,
        @RequestParam(name = "workLogId") workLogId: Long
    ): String {
        workDayService.removeWorkLog(workDayId, workLogId)
        return "redirect:$URL?workDayId=$workDayId"
    }

    @GetMapping("/work-log/start")
    fun startWorkLog(
        @RequestParam(name = "workDayId") workDayId: Long,
        @RequestParam(name = "workLogId") workLogId: Long
    ): String {
        workDayService.startWorkLog(workDayId, workLogId)
        return "redirect:$URL?workDayId=$workDayId"
    }
    @GetMapping("/work-log/continue")
    fun continueWorkLog(
        @RequestParam(name = "workDayId") workDayId: Long,
        @RequestParam(name = "workLogId") workLogId: Long
    ): String {
        workDayService.continueWorkLog(workDayId, workLogId)
        return "redirect:$URL?workDayId=$workDayId"
    }

    companion object {
        const val URL = "/work-day/edit"
    }
}