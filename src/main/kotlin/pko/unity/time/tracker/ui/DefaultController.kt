package pko.unity.time.tracker.ui

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import pko.unity.time.tracker.infrastructure.JiraRepository
import pko.unity.time.tracker.ui.jira.JiraCredentialsController
import pko.unity.time.tracker.ui.work.day.WorkDayListController

@Controller
class DefaultController(
    private val jiraRepository: JiraRepository
) {
    @GetMapping("/")
    fun main() = if(jiraRepository.credentialsAreValid()) "redirect:${WorkDayListController.URL}" else "redirect:${JiraCredentialsController.URL}"
}