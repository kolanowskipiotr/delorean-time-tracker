package pko.delorean.time.tracker.domain

import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import pko.delorean.time.tracker.domain.WorkLogType.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.random.Random

internal class IssueSummaryTest {

    @Test
    fun `should sort work logs`(){
        //given
        val workLogs = setOf(
            mockWorkLog(WORK_LOG, "11:00"),
            mockWorkLog(WORK_LOG, "12:00"),
            mockWorkLog(WORK_LOG, "08:00"),
            mockWorkLog(PRIVATE_TIME, "09:00"),
            mockWorkLog(BREAK, "07:00") ,
            mockWorkLog(WORK_ORGANIZATION, "22:00")
        )
        val workDay = Mockito.spy(WorkDay())
        given(workDay.workLogs).willReturn(workLogs)

        //when
        val workDaySummary = workDay.summary

        //then
        assertThat(workDaySummary.map { "${it.workLogType} ${timeString(it.started)}" })
            .isEqualTo(listOf(
                "WORK_LOG 12:00",
                "WORK_LOG 11:00",
                "WORK_LOG 08:00",
                "WORK_ORGANIZATION 22:00",
                "BREAK 07:00",
                "PRIVATE_TIME 09:00"))
    }

    private fun mockWorkLog(
        workLogType: WorkLogType,
        startTime: String
    ): WorkLog? {
        val wl = Mockito.mock(WorkLog::class.java)
        given(wl.jiraId).willReturn("TEST-${Random.nextInt()}")
        given(wl.type).willReturn(workLogType)
        given(wl.started).willReturn(LocalDateTime.parse("2018-12-30T$startTime").toInstant(ZoneOffset.UTC))
        given(wl.comment).willReturn("$workLogType $startTime")
        return wl
    }

    private fun timeString(instatnt: Instant?): String? {
        if(instatnt != null) {
            return TIME_FORMATTER.format(instatnt)
        }
        return StringUtils.EMPTY
    }
}

private var TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    .withZone(ZoneId.of("UTC"))
