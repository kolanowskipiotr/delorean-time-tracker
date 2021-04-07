package pko.delorean.time.tracker.ui.work.day.dto

data class IssueSummaryDto(
    val jiraId: String,
    val jiraNames: Set<String>,
    val comments: Set<String>) {
}