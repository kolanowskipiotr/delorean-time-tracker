package pko.delorean.time.tracker.domain;

import pko.delorean.time.tracker.domain.dto.ExportableWorkLog;
import pko.delorean.time.tracker.domain.summary.IssueSummary;
import pko.delorean.time.tracker.kernel.Utils;
import pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto;
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogDto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static pko.delorean.time.tracker.domain.WorkDayStatus.*;

/**
 * A WorkDay.
 */
@Entity
@Table(name = "work_days")
public class WorkDay implements Serializable {

    private static final long serialVersionUID = 2915958533629303136L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "workDay")
    private Set<WorkLog> workLogs = new HashSet<>();

    //Hibernate need this
    public WorkDay() {
    }

    public WorkDay(LocalDate createDate) {
        this.createDate = createDate;
    }


    public Long getId() {
        return id;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public Set<WorkLog> getWorkLogs() {
        return workLogs;
    }

    public Set<ExportableWorkLog> getUnexportedWorkLogs() {
        return this.workLogs.stream()
                .filter(WorkLog::isUnexported)
                .filter(workLog -> Utils.Companion.workLogDuration(workLog, this.createDate) > 0)
                .map(workLog -> new ExportableWorkLog(workLog, buildExportComment(workLog), createDate))
                .collect(Collectors.toSet());
    }

    public Long getDuration() {
        return Utils.Companion.sumDurations(this.workLogs, this.createDate);
    }

    public List<ProjectStatistics>  getStatistics() {
        return this.workLogs.stream()
                .collect(groupingBy(WorkLog::getProjectKey))
                .entrySet().stream()
                .map(it -> new ProjectStatistics(it.getKey(), it.getValue(), this.createDate))
                .sorted(comparing(ProjectStatistics::getDuration))
                .collect(Collectors.toList());
    }

    public List<IssueSummary> getSummary() {
        return this.workLogs.stream()
                .collect(groupingBy(WorkLog::getJiraId))
                .entrySet().stream()
                .map(it -> new IssueSummary(it.getKey(), it.getValue()))
                .sorted(comparing(IssueSummary::getOrdering))
                .collect(Collectors.toList());
    }

    public WorkDayStatus getStatus() {
        if (this.workLogs.stream().allMatch(wl -> wl.getStatus() == STOPPED)) {
            return STOPPED;
        }
        if (this.workLogs.stream().allMatch(wl -> wl.getStatus() == EXPORTED)) {
            return EXPORTED;
        }
        return IN_PROGRESS;
    }

    public Set<Long> workLogInConflictIds() {
        return this.workLogs.stream()
                .map(it -> workLogInConflictIds(it.getStarted(), endedOrEndOfDay(it), it.getId()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public void update(LocalDate date) {
        this.createDate = date;
        this.workLogs.forEach(
                workLog -> workLog.changeDate(date)
        );
    }


    public void toggleExport(long workLogId) {
        this.workLogs.stream()
                .filter(workLog -> workLog.getId().equals(workLogId))
                .forEach(workLog -> {
                    if (workLog.isExported()) {
                        workLog.markUnexported();
                    } else {
                        workLog.markExported();
                    }
                });
    }

    public void markExported(List<Long> exportedIds) {
        this.workLogs.stream()
                .filter(workLog -> exportedIds.contains(workLog.getId()))
                .forEach(WorkLog::markExported);
    }

    public void stopTracking() {
        Instant now = Utils.Companion.buildDateTimeInstant(this.createDate, now().truncatedTo(MINUTES));
        endWorklogs(now);
    }

    public void continueTracking(long workLogId) {
        this.workLogs.stream()
                .max(comparing(WorkLog::getStarted))
                .filter(workLog -> workLog.getId().equals(workLogId))
                .ifPresent(WorkLog::contiune);
    }

    public void startTracking(long workLogId) {
        Instant now = Utils.Companion.buildDateTimeInstant(this.createDate, now().truncatedTo(MINUTES));
        List<WorkLog> workLogsToCopy = this.workLogs.stream()
                .filter(workLog -> workLog.getId().equals(workLogId))
                .collect(Collectors.toList());
        workLogsToCopy.forEach(it -> addWorkLog(
                new WorkLogDto(
                        null,
                        it.getJiraId(),
                        JiraIssueTypeDto.Companion.buildJava(it.getJiraIssueType().getValue()),
                        Utils.Companion.getTIME_FORMATTER().format(now),
                        null,
                        null,
                        it.getJiraName(),
                        it.getComment(),
                        null)));
    }

    public void addWorkLog(WorkLogDto workLogDto) {
        Optional<Instant> startAt = modifyWorkloads(workLogDto);

        startAt.map(it -> new WorkLog(
                this,
                it,
                isBlank(workLogDto.getEnded())
                        ? null
                        : Utils.Companion.buildDateTimeInstant(this.createDate, workLogDto.getEnded()),
                workLogDto.getJiraIssiueId(),
                workLogDto.getJiraIssiueName(),
                workLogDto.getJiraIssiueComment(),
                Utils.Companion.buildDateTimeInstantEndOfDay(this.createDate),
                toDomainModel(workLogDto.getJiraIssueType())))
                .ifPresent(workLogs::add);
    }

    private JiraIssueType toDomainModel(JiraIssueTypeDto jiraIssueType) {
        return new JiraIssueType(jiraIssueType.getName());
    }

    public void editWorkLog(WorkLogDto workLogDto) {
        Optional<Instant> startAt = modifyWorkloads(workLogDto);
        Instant ended = isBlank(workLogDto.getEnded()) ? null : Utils.Companion.buildDateTimeInstant(this.createDate, workLogDto.getEnded());
        Instant endOfDay = Utils.Companion.buildDateTimeInstantEndOfDay(this.createDate);

        startAt.ifPresent(started -> this.workLogs.stream()
                .filter(workLog -> workLog.getId().equals(workLogDto.getId()))
                .findFirst()
                .ifPresent(worklog -> worklog.updateState(
                        started,
                        ended,
                        workLogDto.getJiraIssiueId(),
                        toDomainModel(workLogDto.getJiraIssueType()),
                        workLogDto.getJiraIssiueName(),
                        workLogDto.getJiraIssiueComment(),
                        endOfDay)));
    }

    public void removeWorkLog(long workLogId) {
        this.workLogs.removeIf(it -> it.getId() == workLogId);
    }

    private void endWorklogs(Instant ended) {
        Instant endOfDay = Utils.Companion.buildDateTimeInstantEndOfDay(this.createDate);
        this.workLogs.stream()
                .filter(WorkLog::isNotEnded)
                .forEach(it -> it.end(ended, endOfDay));
    }

    private Optional<Instant> modifyWorkloads(WorkLogDto workLogDto) {
        Instant now = now().truncatedTo(MINUTES);
        Instant started = isBlank(workLogDto.getStarted())
                ? Utils.Companion.buildDateTimeInstant(this.createDate, now)
                : Utils.Companion.buildDateTimeInstant(this.createDate, workLogDto.getStarted());
        Instant ended = isBlank(workLogDto.getEnded())
                ? Utils.Companion.buildDateTimeInstantEndOfDay(this.createDate)
                : Utils.Companion.buildDateTimeInstant(this.createDate, workLogDto.getEnded());

        if (started.isBefore(ended)) {
            if (isBlank(workLogDto.getEnded())) {
                endWorklogs(started);
            }
            moveWorklogs(started, ended, workLogDto.getId());
            return Optional.of(started);
        }
        return Optional.empty();
    }

    private void moveWorklogs(Instant started, Instant ended, Long idToExclude) {
        if (!isOverridingOtherWorklog(started, ended, idToExclude)) {
            adjustNeighbourWorklogs(started, ended, idToExclude);
        }
    }

    private boolean isOverridingOtherWorklog(Instant started, Instant ended, Long idToExclude) {
        return this.workLogs.stream()
                .filter(it -> !it.getId().equals(idToExclude))
                .anyMatch(it -> started.isBefore(it.getStarted()) && ended.isAfter(endedOrEndOfDay(it)));
    }

    private Instant endedOrEndOfDay(WorkLog workLog) {
        return workLog.isEnded() ? workLog.getEnded() : Utils.Companion.buildDateTimeInstantEndOfDay(this.createDate);
    }

    private void adjustNeighbourWorklogs(Instant started, Instant ended, Long idToExclude) {
        this.workLogs.stream()
                .filter(it -> !it.getId().equals(idToExclude))
                .filter(it -> started.isAfter(it.getStarted()) && started.isBefore(endedOrEndOfDay(it)))
                .forEach(it -> it.setEnded(started));
        this.workLogs.stream()
                .filter(it -> !it.getId().equals(idToExclude))
                .filter(it -> ended.isAfter(it.getStarted()) && ended.isBefore(endedOrEndOfDay(it)))
                .forEach(it -> it.setStarted(ended));
    }

    private Set<Long> workLogInConflictIds(Instant started, Instant ended, Long idToExclude) {
        return this.workLogs.stream()
                .filter(it -> !it.getId().equals(idToExclude))
                .filter(it -> started.isBefore(endedOrEndOfDay(it)) & ended.isAfter(it.getStarted()))
                .map(WorkLog::getId)
                .collect(Collectors.toSet());
    }

    private String buildExportComment(WorkLog worklog) {
        return defaultString(worklog.getComment()) + " "
                + Utils.Companion.getDATE_FORMATTER().format(createDate) + ", "
                + Utils.Companion.getTIME_FORMATTER().format(worklog.getStarted()) + "-" + Utils.Companion.getTIME_FORMATTER().format(worklog.getEnded()) + " "
                + "(" + Utils.Companion.workLogDuration(worklog, this.createDate) + "m)";
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkDay)) {
            return false;
        }
        return id != null && id.equals(((WorkDay) o).id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkDay{" +
                "id=" + getId() +
                ", createDate='" + getCreateDate() + "'" +
                "}";
    }
}