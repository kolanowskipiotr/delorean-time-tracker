package pko.unity.time.tracker.application

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pko.unity.time.tracker.domain.WorkDay
import pko.unity.time.tracker.infrastructure.WorkDayJpaRepository
import pko.unity.time.tracker.ui.jira.dto.JiraIssueDto
import pko.unity.time.tracker.ui.work.day.dto.WorkDayDto
import pko.unity.time.tracker.ui.work.day.dto.WorkLogDto
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class WorkDayService @Autowired constructor(
    private val workDayJpaRepository: WorkDayJpaRepository
) {

    var TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())

    @Transactional(readOnly = true)
    fun allWorkDays(): List<WorkDayDto> =
        workDayJpaRepository.findAll()
            .map { WorkDayDto(it.id, it.createDate, it.status.name) }
            .sortedByDescending { it.date }

    @Transactional(readOnly = true)
    fun workLogInConflictIds(workDayId: Long): Set<Long> =
        workDayJpaRepository.getOne(workDayId).workLogInConflictIds()


    fun lastUsedIssues(): List<JiraIssueDto> {
        val issues = workDayJpaRepository.findAll()
            .flatMap { it.workLogs }
            .sortedByDescending { it.started }
            .distinctBy { it.jiraId }
            .map { JiraIssueDto(it.jiraId, it.jiraName, it.comment) }
        return if (issues.size > 100) issues.subList(0, 99) else issues
    }

    @Transactional
    fun addWorkDay(workDayDto: WorkDayDto): Long =
        workDayJpaRepository.saveAndFlush(WorkDay(workDayDto.date))
            .id

    @Transactional
    fun removeWorkDay(workDayId: Long) {
        workDayJpaRepository.deleteAll(
            workDayJpaRepository.findAllById(listOf(workDayId))
        )
    }

    fun getWorkDay(workDayId: Long): WorkDayDto? =
        workDayJpaRepository.getOne(workDayId)
            .let {
                WorkDayDto(
                    it.id,
                    it.createDate,
                    it.status.name,
                    it.workLogs
                        .sortedBy { it.started }
                        .map {
                            WorkLogDto(
                                it.id,
                                it.jiraId,
                                timeString(it.started),
                                timeString(it.ended),
                                it.took,
                                it.jiraName,
                                it.comment,
                                it.status.name
                            )
                        })
            }

    @Transactional
    fun updateWorkDay(workDayId: Long, date: LocalDate) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.update(date);
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun addWorkLog(workDayId: Long, workLogDto: WorkLogDto) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.addWorkLog(workLogDto);
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun removeWorkLog(workDayId: Long, workLogId: Long) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.removeWorkLog(workLogId);
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun stopWorklog(workDayId: Long) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.stopTracking();
        workDayJpaRepository.saveAndFlush(workDay)
    }

    private fun timeString(instatnt: Instant?): String? {
        if(instatnt != null) {
            return TIME_FORMATTER.format(instatnt)
        }
        return StringUtils.EMPTY
    }
}