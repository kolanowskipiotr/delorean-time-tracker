package pko.delorean.time.tracker.domain

import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogDto
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogTypeDto.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal class WorkDayBreakCalculationTest {

    @Test
    fun `should divide break on work logs proportionally`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"),"02:00", "04:00"))
        addWorklog(workDay, WorkLogDto(2L, BREAK, true, "2", JiraIssueTypeDto("Break"), "04:00", "06:00"))
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "3", JiraIssueTypeDto("Task"), "06:00", "07:00"))

        //when
        val workLogsToExport = workDay.calculteUnexportedWorkLogs()
        
        //then
        assertThat(workLogsToExport).hasSize(2)

        val worklogsToAssert = workLogsToExport.sortedBy { it.worklog.started }.toList()

        val workLog1 = workDay.workLogById(1L)
        assertThat(workLog1.`break`).isEqualTo(80)
        assertThat(worklogsToAssert[0].worklog.jiraId).isEqualTo("1")
        assertThat(worklogsToAssert[0].breakDurationInMinutes).isEqualTo(80)
        assertThat(worklogsToAssert[0].comment).contains(" + czas organizacyjny 80m")

        val breakTime = workDay.workLogById(2L)
        assertThat(breakTime.isExported).isTrue()
        assertThat(breakTime.`break`).isEqualTo(0)

        val workLog2 = workDay.workLogById(3L)
        assertThat(workLog2.`break`).isEqualTo(40)
        assertThat(worklogsToAssert[1].worklog.jiraId).isEqualTo("3")
        assertThat(worklogsToAssert[1].breakDurationInMinutes).isEqualTo(40)
        assertThat(worklogsToAssert[1].comment).contains(" + czas organizacyjny 40m")
    }

    @Test
    fun `should divide break on work logs evenly as possible`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"),"02:00", "03:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"),"03:00", "04:00"))
        addWorklog(workDay, WorkLogDto(3L, BREAK, true, "3", JiraIssueTypeDto("Break"), "04:00", "04:32"))
        addWorklog(workDay, WorkLogDto(4L, WORK_LOG, true, "4", JiraIssueTypeDto("Task"), "04:32", "05:32"))
        workDay.stopTracking()

        //when
        val workLogsToExport = workDay.calculteUnexportedWorkLogs()

        //then
        assertThat(workLogsToExport).hasSize(3)

        val worklogsToAssert = workLogsToExport.sortedBy { it.worklog.started }.toList()

        val workLog0 = workDay.workLogById(1L)
        assertThat(workLog0.`break`).isEqualTo(11)
        assertThat(worklogsToAssert[0].worklog.jiraId).isEqualTo("1")
        assertThat(worklogsToAssert[0].breakDurationInMinutes).isEqualTo(11)
        assertThat(worklogsToAssert[0].comment).contains(" + czas organizacyjny 11m")

        val workLog1 = workDay.workLogById( 2L)
        assertThat(workLog1.`break`).isEqualTo(11)
        assertThat(worklogsToAssert[1].worklog.jiraId).isEqualTo("2")
        assertThat(worklogsToAssert[1].breakDurationInMinutes).isEqualTo(11)
        assertThat(worklogsToAssert[1].comment).contains(" + czas organizacyjny 11m")

        val breakTime = workDay.workLogById(3L)
        assertThat(breakTime.isExported).isTrue()
        assertThat(breakTime.`break`).isEqualTo(0)

        val workLog3 = workDay.workLogById(4L)
        assertThat(workLog3.`break`).isEqualTo(10)
        assertThat(worklogsToAssert[2].worklog.jiraId).isEqualTo("4")
        assertThat(worklogsToAssert[2].breakDurationInMinutes).isEqualTo(10)
        assertThat(worklogsToAssert[2].comment).contains(" + czas organizacyjny 10m")
    }

    @Test
    fun `should add breaks only to some worklogs when break is to short`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"),"02:00", "03:00"))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"),"03:00", "04:00"))
        addWorklog(workDay, WorkLogDto(2L, BREAK, true, "3", JiraIssueTypeDto("Break"), "04:00", "04:02"))
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "4", JiraIssueTypeDto("Task"), "04:02", "05:02"))

        //when
        val workLogsToExport = workDay.calculteUnexportedWorkLogs()

        //then
        assertThat(workLogsToExport).hasSize(3)

        val worklogsToAssert = workLogsToExport.sortedBy { it.worklog.started }.toList()

        val workLog1 = workDay.workLogById(1L)
        assertThat(workLog1.`break`).isEqualTo(1)
        assertThat(worklogsToAssert[0].worklog.jiraId).isEqualTo("1")
        assertThat(worklogsToAssert[0].breakDurationInMinutes).isEqualTo(1)
        assertThat(worklogsToAssert[0].comment).contains(" + czas organizacyjny 1m")

        val workLog2 = workDay.workLogById(2L)
        assertThat(workLog2.`break`).isEqualTo(1)
        assertThat(worklogsToAssert[1].worklog.jiraId).isEqualTo("2")
        assertThat(worklogsToAssert[1].breakDurationInMinutes).isEqualTo(1)
        assertThat(worklogsToAssert[1].comment).contains(" + czas organizacyjny 1m")

        val breakTime = workDay.workLogById(3L)
        assertThat(breakTime.isExported).isTrue()
        assertThat(breakTime.`break`).isEqualTo(0)

        val workLog4 = workDay.workLogById( 4L)
        assertThat(workLog4.`break`).isEqualTo(0)
        assertThat(worklogsToAssert[2].worklog.jiraId).isEqualTo("4")
        assertThat(worklogsToAssert[2].breakDurationInMinutes).isEqualTo(0)
        assertThat(worklogsToAssert[2].comment).doesNotContain(" + czas organizacyjny ")
    }

    @Test
    fun `should not add break when there is no worklogs`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"),"02:00"))
        addWorklog(workDay, WorkLogDto(3L, WORK_LOG, true, "3", JiraIssueTypeDto("Task"), "06:00"))
        workDay.stopTracking()

        //when
        val workLogsToExport = workDay.calculteUnexportedWorkLogs()

        //then
        assertThat(workLogsToExport).hasSize(2)

        val worklogsToAssert = workLogsToExport.sortedBy { it.worklog.started }.toList()

        val workLog1 = workDay.workLogById( 1L)
        assertThat(workLog1.`break`).isEqualTo(0)
        assertThat(worklogsToAssert[0].worklog.jiraId).isEqualTo("1")
        assertThat(worklogsToAssert[0].breakDurationInMinutes).isEqualTo(0)
        assertThat(worklogsToAssert[0].comment).doesNotContain(" + czas organizacyjny ")

        val workLog3 = workDay.workLogById( 3L)
        assertThat(workLog3.`break`).isEqualTo(0)
        assertThat(worklogsToAssert[1].worklog.jiraId).isEqualTo("3")
        assertThat(worklogsToAssert[1].breakDurationInMinutes).isEqualTo(0)
        assertThat(worklogsToAssert[1].comment).doesNotContain(" + czas organizacyjny ")
    }

    @Test
    fun `should not add break when there is no woklogs`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, BREAK, true, "1", JiraIssueTypeDto("Task"),"02:00"))
        addWorklog(workDay, WorkLogDto(3L, WORK_ORGANIZATION, true, "3", JiraIssueTypeDto("Task"), "06:00"))
        workDay.stopTracking()

        //when
        val workLogsToExport = workDay.calculteUnexportedWorkLogs()

        //then
        assertThat(workLogsToExport).hasSize(0)

        val breakTime1 = workDay.workLogById(1L)
        assertThat(breakTime1.isExported).isTrue()
        assertThat(breakTime1.`break`).isEqualTo(0)

        val breakTime3 = workDay.workLogById( 3L)
        assertThat(breakTime3.isExported).isTrue()
        assertThat(breakTime3.`break`).isEqualTo(0)
    }

    @Test
    fun `should not add all break to one worklog`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, BREAK, true, "1", JiraIssueTypeDto("Task"),"02:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_LOG, true, "2", JiraIssueTypeDto("Task"),"04:00"))
        addWorklog(workDay, WorkLogDto(3L, BREAK, true, "3", JiraIssueTypeDto("Task"),"06:00"))
        addWorklog(workDay, WorkLogDto(4L, WORK_ORGANIZATION, true, "4", JiraIssueTypeDto("Task"), "08:00", "10:00"))
        workDay.stopTracking()

        //when
        val workLogsToExport = workDay.calculteUnexportedWorkLogs()

        //then
        assertThat(workLogsToExport).hasSize(1)

        val worklogsToAssert = workLogsToExport.sortedBy { it.worklog.started }.toList()

        val breakTime1 = workDay.workLogById(1L)
        assertThat(breakTime1.isExported).isTrue()
        assertThat(breakTime1.`break`).isEqualTo(0)

        assertThat(worklogsToAssert[0].worklog.jiraId).isEqualTo("2")
        assertThat(worklogsToAssert[0].breakDurationInMinutes).isEqualTo(360)
        assertThat(worklogsToAssert[0].comment).contains(" + czas organizacyjny 360m")

        val breakTime3 = workDay.workLogById(3L)
        assertThat(breakTime3.isExported).isTrue()
        assertThat(breakTime3.`break`).isEqualTo(0)

        val breakTime4 = workDay.workLogById(4L)
        assertThat(breakTime4.isExported).isTrue()
        assertThat(breakTime4.`break`).isEqualTo(0)
    }

    @Test
    fun `should skip private work logs`() {
        //given
        val workDay = WorkDay(LocalDate.of(2021, 11,23))
        addWorklog(workDay, WorkLogDto(1L, WORK_LOG, true, "1", JiraIssueTypeDto("Task"),"02:00", "03:00"))
        addWorklog(workDay, WorkLogDto(2L, WORK_ORGANIZATION, true, "2", JiraIssueTypeDto("Task"),"03:00", "04:00"))
        addWorklog(workDay, WorkLogDto(3L, PRIVATE_TIME, true, "3", JiraIssueTypeDto("Task"),"04:00", "05:00"))
        addWorklog(workDay, WorkLogDto(4L, BREAK, true, "4", JiraIssueTypeDto("Break"), "05:00", "06:00"))
        addWorklog(workDay, WorkLogDto(5L, WORK_LOG, true, "5", JiraIssueTypeDto("Task"), "06:00", "07:00"))

        //when
        val workLogsToExport = workDay.calculteUnexportedWorkLogs()

        //then
        assertThat(workLogsToExport).hasSize(2)

        val worklogsToAssert = workLogsToExport.sortedBy { it.worklog.started }.toList()

        val breakTime1 = workDay.workLogById(1L)
        assertThat(breakTime1.isExported).isFalse()
        assertThat(breakTime1.`break`).isEqualTo(60)
        assertThat(worklogsToAssert[0].worklog.jiraId).isEqualTo("1")
        assertThat(worklogsToAssert[0].breakDurationInMinutes).isEqualTo(60)
        assertThat(worklogsToAssert[0].comment).contains(" + czas organizacyjny 60m")

        val breakTime2 = workDay.workLogById(2L)
        assertThat(breakTime2.isExported).isTrue()
        assertThat(breakTime2.`break`).isEqualTo(0)

        val privateTime = workDay.workLogById(3L)
        assertThat(privateTime.isExported).isFalse()
        assertThat(privateTime.status).isEqualTo(WorkDayStatus.UNEXPORTABLE)
        assertThat(privateTime.`break`).isEqualTo(0)

        val breakTime4 = workDay.workLogById(4L)
        assertThat(breakTime4.isExported).isTrue()
        assertThat(breakTime4.`break`).isEqualTo(0)

        assertThat(worklogsToAssert[1].worklog.jiraId).isEqualTo("5")
        assertThat(worklogsToAssert[1].breakDurationInMinutes).isEqualTo(60)
        assertThat(worklogsToAssert[1].comment).contains(" + czas organizacyjny 60m")
    }

    private fun addWorklog(
        workDay: WorkDay,
        workLogDto: WorkLogDto
    ) {
        workDay.addWorkLog(workLogDto)
        workDay.workLogs.forEach { it -> it.id = Integer.parseInt(it.jiraId).toLong() }
    }

    private fun WorkDay.workLogById(id: Long) =
        this.workLogs.first { it.id == id }

}