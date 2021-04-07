package pko.delorean.time.tracker.ui.work.day

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pko.delorean.time.tracker.application.WorkDayService
import pko.delorean.time.tracker.ui.work.day.dto.WorkDayDto
import pko.delorean.time.tracker.ui.work.day.dto.WorkDaysFilterDto
import pko.delorean.time.tracker.ui.work.day.form.WorkDaysFilterForm

@Controller
@RequestMapping("/work-day")
class WorkDayListController (
        private val workDayService: WorkDayService
) {

    companion object {
        const val URL = "/work-day/list"
    }

    @GetMapping("/list")
    fun showTemplates(model: Model, workDaysFilter: WorkDaysFilterForm): String {
        val filters = workDaysFilter.defaultIfNull()
        model.addAttribute("workDays", workDayService.allWorkDays(filters.createDateStart!!, filters.createDateEnd!!))
        model.addAttribute("filters", filters.toDto())
            return URL
    }

    @PostMapping("/add")
    fun addTemplate(workDayDto: WorkDayDto): String{
        val workDayId = workDayService.addWorkDay(workDayDto)
        return "redirect:${WorkDayEditController.editUrl(workDayId)}"
    }

    @PostMapping("/delete")
    fun removeTemplate(@RequestParam(name = "workDayId") workDayId: Long): String{
        workDayService.removeWorkDay(workDayId)

        return "redirect:$URL"
    }

    private fun WorkDaysFilterForm.toDto() =
        WorkDaysFilterDto(createDateStart, createDateEnd)
}
