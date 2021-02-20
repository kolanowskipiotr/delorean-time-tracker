package pko.delorean.time.tracker.domain.dto

import pko.delorean.time.tracker.domain.WorkLog
import java.time.LocalDate

data class ExportableWorkLog(
    val worklog: WorkLog,
    val comment: String,
    val date: LocalDate
) {
}