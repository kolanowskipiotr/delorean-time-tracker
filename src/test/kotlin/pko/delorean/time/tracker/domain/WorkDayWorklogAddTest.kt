package pko.delorean.time.tracker.domain

import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogDto
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogTypeDto.WORK_LOG
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal class WorkDayWorklogAddTest {

    @Test
    fun `should adjust neighbours`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"),"02:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"), "04:00"))
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "3", JiraIssueTypeDto("Task"), "06:00"))

        //when
        addWorklog(workDay, WorkLogDto(4L, WORK_LOG, true, "4", JiraIssueTypeDto("Task"), "03:00", "05:00"))

        //then
        val worklogsAfterChanges = workDay.workLogs.sortedBy { it.started }
        assertThat(worklogsAfterChanges[0].jiraId).isEqualTo("1")
        assertThat(timeString(worklogsAfterChanges[0].started)).isEqualTo("02:00")
        assertThat(timeString(worklogsAfterChanges[0].ended)).isEqualTo("03:00")

        assertThat(worklogsAfterChanges[1].jiraId).isEqualTo("4")
        assertThat(timeString(worklogsAfterChanges[1].started)).isEqualTo("03:00")
        assertThat(timeString(worklogsAfterChanges[1].ended)).isEqualTo("05:00")

        assertThat(worklogsAfterChanges[2].jiraId).isEqualTo("2")
        assertThat(timeString(worklogsAfterChanges[2].started)).isEqualTo("05:00")
        assertThat(timeString(worklogsAfterChanges[2].ended)).isEqualTo("06:00")

        assertThat(worklogsAfterChanges[3].jiraId).isEqualTo("3")
        assertThat(timeString(worklogsAfterChanges[3].started)).isEqualTo("06:00")
        assertThat(worklogsAfterChanges[3].ended).isNull()

        assertThat(workDay.workLogInConflictIds()).isEmpty()
    }

    @Test
    fun `should adjust neighbour in the back if enought space in front`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"), "01:00", "02:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"), "06:00"))

        //when
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "3", JiraIssueTypeDto("Task"), "03:00", "07:00"))

        //then
        val worklogsAfterChanges = workDay.workLogs.sortedBy { it.started }
        assertThat(worklogsAfterChanges[0].jiraId).isEqualTo("1")
        assertThat(timeString(worklogsAfterChanges[0].started)).isEqualTo("01:00")
        assertThat(timeString(worklogsAfterChanges[0].ended)).isEqualTo("02:00")

        assertThat(worklogsAfterChanges[1].jiraId).isEqualTo("3")
        assertThat(timeString(worklogsAfterChanges[1].started)).isEqualTo("03:00")
        assertThat(timeString(worklogsAfterChanges[1].ended)).isEqualTo("07:00")

        assertThat(worklogsAfterChanges[2].jiraId).isEqualTo("2")
        assertThat(timeString(worklogsAfterChanges[2].started)).isEqualTo("07:00")
        assertThat(worklogsAfterChanges[2].ended).isNull()

        assertThat(workDay.workLogInConflictIds()).isEmpty()
    }

    @Test
    fun `should adjust front neighbour if enougt space in back`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"), "02:00", "04:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"), "07:00"))

        //when
        addWorklog(workDay, WorkLogDto(4L, WORK_LOG, true, "4", JiraIssueTypeDto("Task"), "03:00", "05:00"))

        //then
        val worklogsAfterChanges = workDay.workLogs.sortedBy { it.started }
        assertThat(worklogsAfterChanges[0].jiraId).isEqualTo("1")
        assertThat(timeString(worklogsAfterChanges[0].started)).isEqualTo("02:00")
        assertThat(timeString(worklogsAfterChanges[0].ended)).isEqualTo("03:00")

        assertThat(worklogsAfterChanges[1].jiraId).isEqualTo("4")
        assertThat(timeString(worklogsAfterChanges[1].started)).isEqualTo("03:00")
        assertThat(timeString(worklogsAfterChanges[1].ended)).isEqualTo("05:00")

        assertThat(worklogsAfterChanges[2].jiraId).isEqualTo("2")
        assertThat(timeString(worklogsAfterChanges[2].started)).isEqualTo("07:00")
        assertThat(worklogsAfterChanges[2].ended).isNull()

        assertThat(workDay.workLogInConflictIds()).isEmpty()
    }

    @Test
    fun `should add worklog wihout changeing naigbours if there is space`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"), "01:00", "02:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"), "06:00"))

        //when
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "3", JiraIssueTypeDto("Task"), "03:00", "04:00"))

        //then
        val worklogsAfterChanges = workDay.workLogs.sortedBy { it.started }
        assertThat(worklogsAfterChanges[0].jiraId).isEqualTo("1")
        assertThat(timeString(worklogsAfterChanges[0].started)).isEqualTo("01:00")
        assertThat(timeString(worklogsAfterChanges[0].ended)).isEqualTo("02:00")

        assertThat(worklogsAfterChanges[1].jiraId).isEqualTo("3")
        assertThat(timeString(worklogsAfterChanges[1].started)).isEqualTo("03:00")
        assertThat(timeString(worklogsAfterChanges[1].ended)).isEqualTo("04:00")

        assertThat(worklogsAfterChanges[2].jiraId).isEqualTo("2")
        assertThat(timeString(worklogsAfterChanges[2].started)).isEqualTo("06:00")
        assertThat(worklogsAfterChanges[2].ended).isNull()

        assertThat(workDay.workLogInConflictIds()).isEmpty()
    }

    @Test
    fun `should add worklog when it its ovveriding other worklogs and consider them in conflict`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"), "02:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"), "04:00"))
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "3", JiraIssueTypeDto("Task"), "06:00"))

        //when
        addWorklog(workDay, WorkLogDto(4L, WORK_LOG, true, "4", JiraIssueTypeDto("Task"), "03:00", "07:00"))

        //then
        val worklogsAfterChanges = workDay.workLogs.sortedBy { it.started }
        val workLogInConflictIds = workDay.workLogInConflictIds()

        assertThat(worklogsAfterChanges[0].jiraId).isEqualTo("1")
        assertThat(timeString(worklogsAfterChanges[0].started)).isEqualTo("02:00")
        assertThat(timeString(worklogsAfterChanges[0].ended)).isEqualTo("04:00")
        assertThat(workLogInConflictIds).contains(1L)

        assertThat(worklogsAfterChanges[1].jiraId).isEqualTo("4")
        assertThat(timeString(worklogsAfterChanges[1].started)).isEqualTo("03:00")
        assertThat(timeString(worklogsAfterChanges[1].ended)).isEqualTo("07:00")
        assertThat(workLogInConflictIds).contains(4L)

        assertThat(worklogsAfterChanges[2].jiraId).isEqualTo("2")
        assertThat(timeString(worklogsAfterChanges[2].started)).isEqualTo("04:00")
        assertThat(timeString(worklogsAfterChanges[2].ended)).isEqualTo("06:00")
        assertThat(workLogInConflictIds).contains(2L)

        assertThat(worklogsAfterChanges[3].jiraId).isEqualTo("3")
        assertThat(timeString(worklogsAfterChanges[3].started)).isEqualTo("06:00")
        assertThat(worklogsAfterChanges[3].ended).isNull()
        assertThat(workLogInConflictIds).contains(3L)
    }

    @Test
    fun `should add worklog when it its ovveriding other worklogs and consider them in conflict edge start and stop`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"), "02:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"), "04:00"))
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "3", JiraIssueTypeDto("Task"), "06:00"))

        //when
        addWorklog(workDay, WorkLogDto(4L, WORK_LOG, true, "4", JiraIssueTypeDto("Task"), "04:00", "06:00"))

        //then
        val worklogsAfterChanges = workDay.workLogs.sortedBy { it.started }
        val workLogInConflictIds = workDay.workLogInConflictIds()

        assertThat(worklogsAfterChanges[0].jiraId).isEqualTo("1")
        assertThat(timeString(worklogsAfterChanges[0].started)).isEqualTo("02:00")
        assertThat(timeString(worklogsAfterChanges[0].ended)).isEqualTo("04:00")
        assertThat(workLogInConflictIds).doesNotContain(1L)

        assertThat(worklogsAfterChanges[1].jiraId).isEqualTo("2")
        assertThat(timeString(worklogsAfterChanges[1].started)).isEqualTo("04:00")
        assertThat(timeString(worklogsAfterChanges[1].ended)).isEqualTo("06:00")
        assertThat(workLogInConflictIds).contains(2L)

        assertThat(worklogsAfterChanges[2].jiraId).isEqualTo("4")
        assertThat(timeString(worklogsAfterChanges[2].started)).isEqualTo("04:00")
        assertThat(timeString(worklogsAfterChanges[2].ended)).isEqualTo("06:00")
        assertThat(workLogInConflictIds).contains(4L)

        assertThat(worklogsAfterChanges[3].jiraId).isEqualTo("3")
        assertThat(timeString(worklogsAfterChanges[3].started)).isEqualTo("06:00")
        assertThat(worklogsAfterChanges[3].ended).isNull()
        assertThat(workLogInConflictIds).doesNotContain(3L)
    }

    @Test
    fun `should add worklog when it its overiding other worklogs and consider them in conflict edge start and stop on conflictedworklog only`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"), "01:00", "02:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"), "04:00", "05:00"))
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "3", JiraIssueTypeDto("Task"), "07:00"))

        //when
        addWorklog(workDay, WorkLogDto(4L, WORK_LOG, true, "4", JiraIssueTypeDto("Task"), "03:00", "06:00"))

        //then
        val worklogsAfterChanges = workDay.workLogs.sortedBy { it.started }
        val workLogInConflictIds = workDay.workLogInConflictIds()

        assertThat(worklogsAfterChanges[0].jiraId).isEqualTo("1")
        assertThat(timeString(worklogsAfterChanges[0].started)).isEqualTo("01:00")
        assertThat(timeString(worklogsAfterChanges[0].ended)).isEqualTo("02:00")
        assertThat(workLogInConflictIds).doesNotContain(1L)

        assertThat(worklogsAfterChanges[1].jiraId).isEqualTo("4")
        assertThat(timeString(worklogsAfterChanges[1].started)).isEqualTo("03:00")
        assertThat(timeString(worklogsAfterChanges[1].ended)).isEqualTo("06:00")
        assertThat(workLogInConflictIds).contains(4L)

        assertThat(worklogsAfterChanges[2].jiraId).isEqualTo("2")
        assertThat(timeString(worklogsAfterChanges[2].started)).isEqualTo("04:00")
        assertThat(timeString(worklogsAfterChanges[2].ended)).isEqualTo("05:00")
        assertThat(workLogInConflictIds).contains(2L)

        assertThat(worklogsAfterChanges[3].jiraId).isEqualTo("3")
        assertThat(timeString(worklogsAfterChanges[3].started)).isEqualTo("07:00")
        assertThat(worklogsAfterChanges[3].ended).isNull()
        assertThat(workLogInConflictIds).doesNotContain(3L)
    }

    private fun addWorklog(
        workDay: WorkDay,
        workLogDto: WorkLogDto
    ) {
        workDay.addWorkLog(workLogDto)
        workDay.workLogs.forEach { it -> it.id = Integer.parseInt(it.jiraId).toLong() }
    }


    var TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())

    private fun timeString(instatnt: Instant?): String? {
        if(instatnt != null) {
            return TIME_FORMATTER.format(instatnt)
        }
        return StringUtils.EMPTY
    }
}