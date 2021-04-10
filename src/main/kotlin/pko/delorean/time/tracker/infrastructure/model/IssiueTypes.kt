package pko.delorean.time.tracker.infrastructure.model

import pko.delorean.time.tracker.ui.jira.dto.JiraIssueTypeDto

data class IssiueTypes(
    val jiraissiueTypes: List<JiraIssueTypeDto> = listOf()
 ) {
    fun getType(issueTypeName: String): JiraIssueTypeDto =
        jiraissiueTypes
            .find { it.name.equals(issueTypeName, ignoreCase = true) }
            ?: JiraIssueTypeDto(issueTypeName)

    fun contains(issueTypeName: String): Boolean =
        jiraissiueTypes.any{ it.name.equals(issueTypeName, ignoreCase = true) }
}
