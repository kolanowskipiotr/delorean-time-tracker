package pko.delorean.time.tracker.domain;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import pko.delorean.time.tracker.domain.dto.ExportableWorkLog;
import pko.delorean.time.tracker.domain.statistics.IssueStatistics;
import pko.delorean.time.tracker.domain.statistics.ProjectStatistics;
import pko.delorean.time.tracker.domain.statistics.WorkDayStatistics;
import pko.delorean.time.tracker.domain.summary.IssueSummary;
import pko.delorean.time.tracker.kernel.Utils;
import pko.delorean.time.tracker.ui.work.day.dto.JiraIssueTypeDto;
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogDto;
import pko.delorean.time.tracker.ui.work.day.dto.WorkLogTypeDto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static pko.delorean.time.tracker.domain.WorkDayStatus.*;
import static pko.delorean.time.tracker.domain.WorkLogType.PRIVATE_TIME;

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

    public Set<ExportableWorkLog> calculteUnexportedWorkLogs() {
        addBreaksToWorkLogs();

        return getExportableWorkLogsStream()
                .map(workLog -> new ExportableWorkLog(
                        workLog,
                        workLog.getBreak(),
                        buildExportComment(
                                workLog,
                                workLog.getBreak()),
                        createDate))
                .collect(toSet());
    }

    public Long getDuration() {
        return Utils.Companion.sumDurations(this.workLogs, this.createDate);
    }

    public WorkDayStatistics getStatistics() {
        List<ProjectStatistics> projectsStatistics = workLogsWithoutPrivateTimeStream()
                .collect(groupingBy(WorkLog::getProjectKey))
                .entrySet().stream()
                .map(it -> new ProjectStatistics(it.getKey(), it.getValue(), this.createDate))
                .sorted(comparing(ProjectStatistics::getDuration))
                .collect(toList());

        IssueStatistics privateTimeStatistics = new IssueStatistics(
                PRIVATE_TIME.name(),
                workLogs.stream()
                        .filter(wl -> wl.getType() == PRIVATE_TIME)
                        .collect(toList()),
                this.createDate);
        return new WorkDayStatistics(
                projectsStatistics.stream().mapToLong(ProjectStatistics::getDuration).sum(),
                projectsStatistics,
                privateTimeStatistics);
    }

    public List<IssueSummary> getSummary() {
        return this.getWorkLogs().stream()
                .collect(groupingBy(WorkLog::getJiraId))
                .entrySet().stream()
                .map(it -> new IssueSummary(it.getKey(), it.getValue()))
                .sorted(comparing(this::workLogTypeOrder)
                        .thenComparing(IssueSummary::getStarted)
                        .reversed())
                .collect(toList());
    }

    private Integer workLogTypeOrder(IssueSummary issueSummary) {
        switch (issueSummary.getWorkLogType()) {
            case PRIVATE_TIME:
                return 1;
            case BREAK:
                return 2;
            case WORK_ORGANIZATION:
                return 3;
            case WORK_LOG:
                return 4;
        }
        return 4;
    }

    public WorkDayStatus getStatus() {

        if (workLogsWithoutPrivateTimeStream().allMatch(wl -> wl.getStatus() == STOPPED)) {
            return STOPPED;
        }
        if (workLogsWithoutPrivateTimeStream().allMatch(wl -> wl.getStatus() == EXPORTED)) {
            return EXPORTED;
        }
        return IN_PROGRESS;
    }

    public Set<Long> workLogInConflictIds() {
        return this.workLogs.stream()
                .map(it -> workLogInConflictIds(it.getStarted(), endedOrEndOfDay(it), it.getId()))
                .flatMap(Collection::stream)
                .collect(toSet());
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

    public void toggleExtensibility(long workLogId) {
        this.workLogs.stream()
                .filter(workLog -> workLog.getId().equals(workLogId))
                .forEach(workLog -> {
                    if (workLog.isExtensible()) {
                        workLog.markUnextensible();
                    } else {
                        workLog.markExtensible();
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

    public void continueTracking() {
        this.workLogs.stream()
                .max(comparing(WorkLog::getStarted))
                .ifPresent(WorkLog::contiune);
    }

    public void startTracking(long workLogId, String comment) {
        Instant now = Utils.Companion.buildDateTimeInstant(this.createDate, now().truncatedTo(MINUTES));
        List<WorkLog> workLogsToCopy = this.workLogs.stream()
                .filter(workLog -> workLog.getId().equals(workLogId))
                .collect(toList());
        workLogsToCopy.forEach(it -> addWorkLog(
                new WorkLogDto(
                        null,
                        toApplicationModel(it.getType()),
                        it.isExtensible(),
                        it.getJiraId(),
                        JiraIssueTypeDto.Companion.buildJava(it.getJiraIssueType().getValue()),
                        Utils.Companion.getTIME_FORMATTER().format(now),
                        null,
                        null,
                        it.getBreak(),
                        it.getJiraName(),
                        ofNullable(comment).orElseGet(it::getComment),
                        null)));
    }

    public void addBreak(WorkLogTypeDto breakType) {
        addWorkLog(WorkLogDto.Companion.breakDto(
                breakType,
                requireNonNull(Utils.Companion.formatTime(now()))));
    }

    public void addWorkLog(WorkLogDto workLogDto) {
        Optional<Instant> startAt = modifyWorkLogs(workLogDto);

        startAt.map(it -> new WorkLog(
                this,
                toDomainModel(workLogDto.getType()),
                workLogDto.getExtensible(),
                it,
                isBlank(workLogDto.getEnded())
                        ? null
                        : Utils.Companion.buildDateTimeInstant(this.createDate, workLogDto.getEnded()),
                workLogDto.getJiraIssiueId(),
                toDomainModel(workLogDto.getJiraIssueType()),
                workLogDto.getJiraIssiueName(),
                workLogDto.getJiraIssiueComment(),
                Utils.Companion.buildDateTimeInstantEndOfDay(this.createDate)))
                .ifPresent(workLogs::add);
    }

    public void editWorkLog(WorkLogDto workLogDto) {
        Optional<Instant> startAt = modifyWorkLogs(workLogDto);
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

    private Stream<WorkLog> workLogsWithoutPrivateTimeStream() {
        return this.workLogs.stream()
                .filter(wl -> wl.getType() != PRIVATE_TIME);
    }

    private void endWorklogs(Instant ended) {
        Instant endOfDay = Utils.Companion.buildDateTimeInstantEndOfDay(this.createDate);
        this.workLogs.stream()
                .filter(WorkLog::isNotEnded)
                .forEach(it -> it.end(ended, endOfDay));
    }

    private Optional<Instant> modifyWorkLogs(WorkLogDto workLogDto) {
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
                .collect(toSet());
    }

    private void addBreaksToWorkLogs() {
        Instant defaultEnd = Utils.Companion.buildDateTimeInstant(this.createDate, now().truncatedTo(MINUTES));

        long breaksDuration = getExportableWorkLogsStream()
                .filter(WorkLog::isDividable)
                .peek(WorkLog::markExported)//☢️
                .mapToLong(workLog -> Utils.Companion.workLogDuration(workLog, this.createDate))
                .sum();

        List<WorkLog> workLogsToAddBreaks = getExportableWorkLogsStream()
                .filter(WorkLog::isUndividable)
                .filter(WorkLog::isExtensible)
                .collect(toList());
        workLogsToAddBreaks.sort(comparing(WorkLog::getStarted).reversed());

        long sumOfAllWorkLogDuration = workLogsToAddBreaks.stream()
                .map(workLog -> workLog.getDuration(defaultEnd))
                .reduce(0L, Long::sum);
        List<ImmutablePair<WorkLog, Long>> workLogsWithBreaks = workLogsToAddBreaks.stream()
                .map(workLog -> ImmutablePair.of(
                        workLog,
                        Math.round(breaksDuration * workLog.getDuration(defaultEnd) / (double) sumOfAllWorkLogDuration)))
                .sorted(Comparator.comparingLong(ImmutablePair::getValue))
                .collect(toList());

        long breaksSumCheck = workLogsWithBreaks.stream()
                .map(Pair::getValue)
                .mapToLong(Long::valueOf)
                .reduce(0L, Long::sum);

        if(breaksSumCheck > breaksDuration){
            workLogsWithBreaks = applyCorrection(
                    workLogsWithBreaks,
                    breaksSumCheck - breaksDuration,
                    breakDuration -> breakDuration - 1);

        } else if (breaksSumCheck < breaksDuration){
            workLogsWithBreaks = applyCorrection(
                    workLogsWithBreaks,
                    breaksDuration - breaksSumCheck,
                    breakDuration -> breakDuration + 1);
        }

        workLogsWithBreaks.forEach(pair -> pair.getKey().addBreak(pair.getValue()));
    }

    private  List<ImmutablePair<WorkLog, Long>> applyCorrection(List<ImmutablePair<WorkLog, Long>> workLogsWithBreaks, Long durationToCorrect, Function<Long, Long> corrector){
        final long[] durationToCorrectWrapped = {durationToCorrect};
        return workLogsWithBreaks.stream()
                .map(pair -> {
                    ImmutablePair<WorkLog, Long> pairAfterCorrection = pair;
                    if(durationToCorrectWrapped[0] > 0 && pair.getValue() > 0){
                        durationToCorrectWrapped[0] = durationToCorrectWrapped[0] - 1;
                        pairAfterCorrection = ImmutablePair.of(pair.getKey(), corrector.apply(pair.getValue()));
                    }
                    return pairAfterCorrection;
                })
                .collect(Collectors.toList());
    }

    private Queue<Long> calculateBreaks(long breaksDuration, int numberOfWorkLogsToExport) {
        long breakDuration = numberOfWorkLogsToExport == 0 ? 0 : breaksDuration / numberOfWorkLogsToExport;
        long lastBreakDuration = numberOfWorkLogsToExport == 0 ? 0 : breaksDuration % numberOfWorkLogsToExport;
        return IntStream.rangeClosed(1, numberOfWorkLogsToExport).boxed()
                .map(workLogNumber -> breakDuration + (workLogNumber <= lastBreakDuration ? 1 : 0))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Stream<WorkLog> getExportableWorkLogsStream(){
        return this.workLogs.stream()
                .filter(WorkLog::isExportable)
                .filter(WorkLog::isUnexported)
                .filter(workLog -> Utils.Companion.workLogDuration(workLog, this.createDate) > 0);
    }

    private String buildExportComment(WorkLog worklog, long breakDuration) {
        return defaultString(worklog.getComment()) + " "
                + Utils.Companion.getDATE_FORMATTER().format(createDate) + " "
                + Utils.Companion.getTIME_FORMATTER().format(worklog.getStarted()) + "-" + Utils.Companion.getTIME_FORMATTER().format(worklog.getEnded()) + " "
                + "(" + Utils.Companion.workLogDuration(worklog, this.createDate) + "m)"
                + (breakDuration > 0 ? " + czas organizacyjny " + breakDuration + "m" : "");
    }

    private WorkLogType toDomainModel(WorkLogTypeDto type) {
        switch (type) {
            case WORK_LOG:
                return WorkLogType.WORK_LOG;
            case BREAK:
                return WorkLogType.BREAK;
            case WORK_ORGANIZATION:
                return WorkLogType.WORK_ORGANIZATION;
            case PRIVATE_TIME:
                return WorkLogType.PRIVATE_TIME;
        }
        throw new IllegalStateException("Unknown work log type: " + type.name());
    }

    private WorkLogTypeDto toApplicationModel(WorkLogType type) {
        switch (type) {
            case WORK_LOG:
                return WorkLogTypeDto.WORK_LOG;
            case BREAK:
                return WorkLogTypeDto.BREAK;
            case WORK_ORGANIZATION:
                return WorkLogTypeDto.WORK_ORGANIZATION;
            case PRIVATE_TIME:
                return WorkLogTypeDto.PRIVATE_TIME;
        }
        throw new IllegalStateException("Unknown work log type: " + type.name());
    }

    private JiraIssueType toDomainModel(JiraIssueTypeDto jiraIssueType) {
        return new JiraIssueType(jiraIssueType.getName());
    }

    @Override
    public int hashCode() {
        return 31;
    }//FIXME: This is wrong

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