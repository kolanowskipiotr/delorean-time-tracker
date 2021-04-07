package pko.delorean.time.tracker.ui.work.day.form

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class WorkDaysFilterForm(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val createDateStart: LocalDate? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val createDateEnd: LocalDate? = null
) {
    fun defaultIfNull(): WorkDaysFilterForm {
        val initial = LocalDate.now()
        return WorkDaysFilterForm(
            createDateStart ?: initial.with(TemporalAdjusters.firstDayOfMonth()),
            createDateEnd ?: initial.with(TemporalAdjusters.lastDayOfMonth())
        )
    }
}
