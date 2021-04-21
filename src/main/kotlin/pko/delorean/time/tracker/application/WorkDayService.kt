package pko.delorean.time.tracker.application

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pko.delorean.time.tracker.domain.*
import pko.delorean.time.tracker.domain.dto.ExportableWorkLog
import pko.delorean.time.tracker.domain.statistics.IssueStatistics
import pko.delorean.time.tracker.domain.summary.IssueSummary
import pko.delorean.time.tracker.infrastructure.JiraService
import pko.delorean.time.tracker.infrastructure.JiraService.ConnectionResult
import pko.delorean.time.tracker.infrastructure.WorkDayJpaRepository
import pko.delorean.time.tracker.kernel.Utils.Companion.formatTime
import pko.delorean.time.tracker.kernel.Utils.Companion.workLogDuration
import pko.delorean.time.tracker.ui.jira.dto.JiraIssueDto
import pko.delorean.time.tracker.ui.jira.dto.JiraIssueTypeDto
import pko.delorean.time.tracker.ui.work.day.dto.*
import pko.delorean.time.tracker.ui.work.day.dto.statistics.IssueStatisticsDto
import pko.delorean.time.tracker.ui.work.day.dto.statistics.ProjectStatisticsDto
import pko.delorean.time.tracker.ui.work.day.dto.statistics.WorkDaysPeriodStatisticsDto
import pko.delorean.time.tracker.ui.work.day.dto.summary.IssueSummaryDto
import java.time.LocalDate

@Service
class WorkDayService @Autowired constructor(
    private val workDayJpaRepository: WorkDayJpaRepository,
    private val jiraService: JiraService
) {

    @Transactional(readOnly = true)
    fun findWorkDays(createDateStart: LocalDate, createDateEnd: LocalDate): List<WorkDayDto> {
        return workDayJpaRepository.findAllByCreateDateBetween(createDateStart, createDateEnd)
                .map { it.toDto() }
                .sortedByDescending { it.date }
    }

    @Transactional(readOnly = true)
    fun getWorkDay(workDayId: Long): WorkDayDto? =
        workDayJpaRepository.getOne(workDayId).toDto()

    @Transactional(readOnly = true)
    fun findWorkDayBefore(workDayId: Long): WorkDayDto? {
        val workDay = workDayJpaRepository.getOne(workDayId)
        return workDayJpaRepository.findAllWithCreateDateBefore(PageRequest.of(0, 1), workDay.createDate)
            .maxBy { it.createDate }?.toDto()
    }

    @Transactional(readOnly = true)
    fun workLogInConflictIds(workDayId: Long): Set<Long> =
        workDayJpaRepository.getOne(workDayId).workLogInConflictIds()

    @Transactional(readOnly = true)
    fun lastUsedIssues(): List<JiraIssueDto> {
        val issues = workDayJpaRepository.findAll()
            .flatMap { it.workLogs }
            .sortedByDescending { it.started }
            .distinctBy { it.jiraId }
            .map { it.toDto() }
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

    @Transactional
    fun updateWorkDay(workDayId: Long, date: LocalDate) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.update(date)
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun addWorkLog(workDayId: Long, workLogDto: WorkLogDto) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.addWorkLog(workLogDto)
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun addBreak(workDayId: Long, breakType: WorkLogTypeDto) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.addBreak(breakType)
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun editWorkLog(workDayId: Long, workLogDto: WorkLogDto) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.editWorkLog(workLogDto)
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun removeWorkLog(workDayId: Long, workLogId: Long) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.removeWorkLog(workLogId)
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun stopWorklog(workDayId: Long) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.stopTracking()
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun startWorkLog(workDayId: Long, workLogId: Long) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.startTracking(workLogId)
        workDayJpaRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun continueWorkLog(workDayId: Long) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.continueTracking()
        workDayJpaRepository.saveAndFlush(workDay)
    }

    fun exportWorkDay(workDayId: Long): ConnectionResult<List<Long>> {
        val unexportedWorkLogs = stopTracking(workDayId)
        return exportWorkLogs(workDayId, unexportedWorkLogs)
    }

    @Transactional
    fun stopTracking(workDayId: Long): Set<ExportableWorkLog> {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.stopTracking()
        return workDay.calculteUnexportedWorkLogs().also {
            workDayJpaRepository.saveAndFlush(workDay)
        }
    }

    @Transactional
    fun exportWorkLogs(workDayId: Long, unexportedWorkLogs: Set<ExportableWorkLog>): ConnectionResult<List<Long>> {
        val workDay = workDayJpaRepository.getOne(workDayId)
        val exportStatus = jiraService.exportWorkDay(unexportedWorkLogs)
        workDay.markExported(exportStatus.value.orEmpty())
        workDayJpaRepository.saveAndFlush(workDay)
        return exportStatus
    }

    @Transactional
    fun toggleExport(workDayId: Long, workLogId: Long) {
        val workDay = workDayJpaRepository.getOne(workDayId)
        workDay.toggleExport(workLogId)
        workDayJpaRepository.saveAndFlush(workDay)
    }

    fun calculateStatistics(workDays: List<WorkDayDto>): WorkDaysPeriodStatisticsDto {
        val statistics = workDays.flatMap { it.projectsStatistics.orEmpty() }
            .groupBy { it.projectKey }
            .map { ProjectStatisticsDto.fromMultipleStatistics(it.key, it.value) }
            .sortedByDescending { it.duration }
        return WorkDaysPeriodStatisticsDto(statistics, statistics.map { it.duration }.sum())
    }

    private fun WorkDay.toDto() =
        WorkDayDto(
            id,
            createDate,
            status.name,
            duration,
            statistics.map { it.toDto() }.sortedByDescending { it.duration },
            summary.map { it.toDto() },
            workLogs
                .sortedBy { it.started }
                .map { it.toDto(this) }
        )

    private fun WorkLog.toDto(workDay: WorkDay) =
        WorkLogDto(
            id,
            type.toDto(),
            jiraId,
            jiraIssueType.toDto(),
            formatTime(started),
            formatTime(ended),
            `break`,
            workLogDuration(this, workDay.createDate),
            jiraName,
            comment,
            status.name
        )
    private fun WorkLog.toDto() =
        JiraIssueDto(jiraId, jiraName, jiraIssueType.toJiraDto(), comment)

    private fun IssueSummary.toDto() =
        IssueSummaryDto(jiraId, jiraIssues.map { it.toDto() })

    private fun ProjectStatistics.toDto() =
        ProjectStatisticsDto(projectKey, duration, issuesStatistics.map { it.toDto() }.sortedByDescending { it.duration })

    private fun IssueStatistics.toDto() =
        IssueStatisticsDto(issueKey, duration, jiraIssues.map { it.toDto() })

    private fun pko.delorean.time.tracker.domain.statistics.JiraIssue.toDto() =
        pko.delorean.time.tracker.ui.work.day.dto.statistics.JiraIssueDto(jiraName, jiraIssueType.toDto(), workLogType.toDto())

    private fun pko.delorean.time.tracker.domain.summary.JiraIssue.toDto() =
        pko.delorean.time.tracker.ui.work.day.dto.summary.JiraIssueDto(jiraName, jiraIssueType.toDto(), workLogType.toDto(), jiraComment)

    private fun JiraIssueType.toDto() =
        jiraService.getIssueType(value).toDto()

    private fun JiraIssueType.toJiraDto() =
        jiraService.getIssueType(value)

    private fun JiraIssueTypeDto.toDto() =
        pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto(name, self, id, iconUri, description, subtask)

    private fun WorkLogType.toDto() =
        when(this){
            WorkLogType.WORK_LOG -> WorkLogTypeDto.WORK_LOG
            WorkLogType.BREAK -> WorkLogTypeDto.BREAK
            WorkLogType.WORK_ORGANIZATION -> WorkLogTypeDto.WORK_ORGANIZATION
            WorkLogType.PRIVATE_WORK_LOG -> WorkLogTypeDto.PRIVATE_WORK_LOG
        }
}

