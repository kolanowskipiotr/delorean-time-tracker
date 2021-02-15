package pko.unity.time.tracker.ui.jira

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pko.unity.time.tracker.application.WorkDayService
import pko.unity.time.tracker.domain.WorkDayRepository
import pko.unity.time.tracker.infrastructure.JiraRepository
import pko.unity.time.tracker.ui.jira.dto.JiraCredentialsDto
import pko.unity.time.tracker.ui.jira.dto.JiraIssueDto
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Controller
@RequestMapping("/jira/issue/search")
class JiraSearchController(
    private val jiraRepository: JiraRepository,
    private val workDayService: WorkDayService
) {

    @GetMapping
    fun showCredentials(
        @RequestParam(name ="workDayId") workDayId: Long,
        @RequestParam(name = "query", required = false) query: String?,
        model: Model
    ): String {
        model.addAttribute("jiraUrl", jiraRepository.credentials()?.jiraUrl)
        model.addAttribute("workDayId", workDayId)
        if(query == null) {
            model.addAttribute("jiraIssues", workDayService.lastUsedIssues())
        } else {
            model.addAttribute("query", query)
            model.addAttribute("jiraIssues", jiraRepository.findJiraIssues(query))
        }
        return URL
    }

    companion object {
        const val URL = "/jira/issue/search"
    }
}