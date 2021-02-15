package pko.unity.time.tracker.ui.jira

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pko.unity.time.tracker.infrastructure.JiraRepository
import pko.unity.time.tracker.ui.jira.dto.JiraCredentialsDto
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Controller
@RequestMapping("/jira/credentials")
class JiraCredentialsController(
    private val jiraRepository: JiraRepository
) {

    @GetMapping("/edit")
    fun showCredentials(
        @RequestParam(name = "success", required = false) success: Boolean?,
        @RequestParam(name = "message", required = false) message: String?,
        model: Model
    ): String {
        model.addAttribute("jiraCredentials", jiraRepository.credentials())
        if(success != null) {
            model.addAttribute(
                "connectionResult", if (success) JiraRepository.ConnectionResult.succes(message)
                else JiraRepository.ConnectionResult.error(message)
            )
        }
        return URL
    }

    @PostMapping("/edit")
    fun updateCredentials(jiraCredentialsDto: JiraCredentialsDto): String {
        jiraRepository.updateCredential(jiraCredentialsDto)
        val connectionResult = jiraRepository.findJiraServerInfo()
        val urlEncodedMessage = URLEncoder.encode(connectionResult.message, StandardCharsets.UTF_8.toString())
        return "redirect:$URL?success=${connectionResult.success}&message=$urlEncodedMessage"
    }

    companion object {
        const val URL = "/jira/credentials/edit"
    }
}