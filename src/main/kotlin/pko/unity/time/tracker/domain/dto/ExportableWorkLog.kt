package pko.unity.time.tracker.domain.dto

import pko.unity.time.tracker.domain.WorkLog
import java.time.LocalDate

data class ExportableWorkLog(
    val worklog: WorkLog,
    val comment: String,
    val date: LocalDate
) {
}