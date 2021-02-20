package pko.unity.time.tracker.ui.jira

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pko.unity.time.tracker.infrastructure.JiraService
import pko.unity.time.tracker.ui.jira.dto.JiraCredentialsDto
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Controller
@RequestMapping("/jira/credentials")
class JiraCredentialsController(
    private val jiraService: JiraService
) {

    @GetMapping("/edit")
    fun showCredentials(
        @RequestParam(name = "success", required = false) success: Boolean?,
        @RequestParam(name = "message", required = false) message: String?,
        model: Model
    ): String {
        model.addAttribute("jiraCredentials", jiraService.credentials())
        if(success != null) {
            model.addAttribute("connectionResult",
                if (success) JiraService.ConnectionResult.success(message)
                else JiraService.ConnectionResult.error(message)
            )
        }
        return URL
    }

    @PostMapping("/edit")
    fun updateCredentials(jiraCredentialsDto: JiraCredentialsDto): String {
        jiraService.updateCredential(jiraCredentialsDto)
        val connectionResult = jiraService.findJiraServerInfo()
        val urlEncodedMessage = URLEncoder.encode(connectionResult.message?:"Error", StandardCharsets.UTF_8.toString())
        return "redirect:$URL?success=${connectionResult.success}&message=$urlEncodedMessage"
    }

    companion object {
        const val URL = "/jira/credentials/edit"
    }
}