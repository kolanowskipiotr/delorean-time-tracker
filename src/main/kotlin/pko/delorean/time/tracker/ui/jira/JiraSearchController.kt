package pko.delorean.time.tracker.ui.jira

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pko.delorean.time.tracker.application.WorkDayService
import pko.delorean.time.tracker.infrastructure.JiraService
import java.util.Random

@Controller
@RequestMapping("/jira/issue/search")
class JiraSearchController(
    private val jiraService: JiraService,
    private val workDayService: WorkDayService
) {

    @GetMapping
    fun showCredentials(
        @RequestParam(name ="workDayId") workDayId: Long,
        @RequestParam(name = "query", required = false) query: String?,
        model: Model
    ): String {
        model.addAttribute("random", Random())
        model.addAttribute("jiraUrl", jiraService.credentials()?.jiraUrl)
        model.addAttribute("workDayId", workDayId)
        if(query == null) {
            model.addAttribute("jiraIssues", workDayService.lastUsedIssues())
        } else {
            model.addAttribute("query", query)
            val jiraSearchResult = jiraService.findJiraIssues(query)
            model.addAttribute("jiraIssues", jiraSearchResult.value)
            if(!jiraSearchResult.success) {
                model.addAttribute("connectionResult", JiraService.ConnectionResult.error(jiraSearchResult.message))
            }
        }
        return URL
    }

    companion object {
        const val URL = "/jira/issue/search"
    }
}