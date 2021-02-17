package pko.unity.time.tracker.ui.work.day

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import pko.unity.time.tracker.application.WorkDayService
import pko.unity.time.tracker.ui.work.day.dto.WorkLogDto
import java.time.LocalDate

@Controller
@RequestMapping("/work-day")
class WorkDayEditController(
    private val workDayService: WorkDayService
) {

    @GetMapping("/edit")
    fun showWorkDay(
        @RequestParam(name = "workDayId") workDayId: Long,
        @RequestParam(name = "searchedJiraIssueId", required = false) searchedJiraIssueId: String?,
        @RequestParam(name = "searchedJiraIssueName", required = false) searchedJiraIssueName: String?,
        @RequestParam(name = "searchedJiraIssueComment", required = false) searchedJiraIssueComment: String?,
                  model: Model): String {
        model.addAttribute("workDay", workDayService.getWorkDay(workDayId))
        model.addAttribute("searchedJiraIssueId", searchedJiraIssueId)
        model.addAttribute("searchedJiraIssueName", searchedJiraIssueName)
        model.addAttribute("searchedJiraIssueComment",searchedJiraIssueComment)
        model.addAttribute("workLogIdsInConflict" , workDayService.workLogInConflictIds(workDayId))
        return URL
    }

    @PostMapping("/edit")
    fun updateWorkDay(@RequestParam(name = "workDayId") workDayId: Long,
                  @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate): String {
        workDayService.updateWorkDay(workDayId, date)
        return "redirect:$URL?workDayId=$workDayId"
    }

    @PostMapping("/work-log")
    fun addWorkLog(@RequestParam(name = "workDayId") workDayId: Long,
                    workLog: WorkLogDto): String{
        workDayService.addWorkLog(workDayId, workLog)
        return "redirect:$URL?workDayId=$workDayId"
    }

    @PostMapping("/delete-receiver")
    fun deleteReceiver(@RequestParam(name = "workDayId") workDayId: Long,
                       @RequestParam(name = "workLogId") workLogId: Long): String{
        workDayService.removeWorkLog(workDayId, workLogId)
        return "redirect:$URL?workDayId=$workDayId"
    }

    companion object {
        const val URL = "/work-day/edit"
    }
}