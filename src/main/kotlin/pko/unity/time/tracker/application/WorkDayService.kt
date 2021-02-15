package pko.unity.time.tracker.application

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pko.unity.time.tracker.domain.WorkDay
import pko.unity.time.tracker.domain.WorkDayRepository
import pko.unity.time.tracker.ui.jira.dto.JiraIssueDto
import pko.unity.time.tracker.ui.work.day.dto.WorkDayDto
import pko.unity.time.tracker.ui.work.day.dto.WorkLogDto
import java.time.LocalDate

@Service
class WorkDayService @Autowired constructor(
    private val workDayRepository: WorkDayRepository
) {

    @Transactional(readOnly = true)
    fun allWorkDays(): List<WorkDayDto> =
        workDayRepository.findAll()
            .map { WorkDayDto(it.id, it.createDate, it.status.name) }
            .sortedByDescending { it.date }


    fun lastUsedIssues(): List<JiraIssueDto> {
        val issues = workDayRepository.findAll()
            .flatMap { it.workLogs }
            .sortedByDescending { it.started }
            .distinctBy { it.jiraId }
            .map { JiraIssueDto(it.jiraId, it.jiraName, it.comment) }
        return if(issues.size > 100) issues.subList(0, 99) else issues
    }

    @Transactional
    fun addWorkDay(workDayDto: WorkDayDto): Long =
        workDayRepository.saveAndFlush(WorkDay(workDayDto.date))
            .id

    @Transactional
    fun removeWorkDay(workDayId: Long) =
        workDayRepository.deleteInBatch(
            workDayRepository.findAllById(listOf(workDayId))
        )

    fun getWorkDay(workDayId: Long): WorkDayDto? =
        workDayRepository.getOne(workDayId)
            .let {
                WorkDayDto(
                    it.id,
                    it.createDate,
                    it.status.name,
                    it.workLogs
                        .map { WorkLogDto(
                            it.id,
                            it.jiraId,
                            it.started,
                            it.ended,
                            it.took,
                            it.jiraName,
                            it.comment) })
            }

    @Transactional
    fun updateWorkDay(workDayId: Long, date: LocalDate) {
        val workDay = workDayRepository.getOne(workDayId)
        workDay.update(date);
        workDayRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun addWorkLog(workDayId: Long, workLogDto: WorkLogDto) {
        val workDay = workDayRepository.getOne(workDayId)
        workDay.addWorkLog(workLogDto);
        workDayRepository.saveAndFlush(workDay)
    }

    @Transactional
    fun removeWorkLog(workDayId: Long, workLogId: Long) {
        val workDay = workDayRepository.getOne(workDayId)
        workDay.removeWorkLog(workLogId);
        workDayRepository.saveAndFlush(workDay)
    }
}