package pko.delorean.time.tracker.ui

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import pko.delorean.time.tracker.infrastructure.JiraService
import pko.delorean.time.tracker.ui.jira.JiraCredentialsController
import pko.delorean.time.tracker.ui.work.day.WorkDayListController

@Controller
class DefaultController(
    private val jiraService: JiraService
) {
    @GetMapping("/")
    fun main() =
        if (jiraService.credentialsAreValid())
            "redirect:${WorkDayListController.URL}"
        else
            "redirect:${JiraCredentialsController.URL}"
}