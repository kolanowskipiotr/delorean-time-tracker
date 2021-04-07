package pko.delorean.time.tracker.ui.work.day.dto

import java.time.LocalDate

data class WorkDaysFilterDto(
    var createDateStart: LocalDate?,
    var createDateEnd: LocalDate?
    )
