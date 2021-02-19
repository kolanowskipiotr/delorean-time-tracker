package pko.unity.time.tracker.domain;

import pko.unity.time.tracker.ui.work.day.dto.WorkLogDto;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static pko.unity.time.tracker.domain.WorkDayStatus.*;

/**
 * A WorkDay.
 */
@Entity
@Table(name = "work_days")
public class WorkDay implements Serializable {

    private static final long serialVersionUID = 2915958533629303136L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "workDay")
    private Set<WorkLog> workLogs = new HashSet<>();

    //Hibernate nied this
    public WorkDay() {
    }

    public WorkDay(LocalDate createDate) {
        this.createDate = createDate;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public Set<WorkLog> getWorkLogs() {
        return workLogs;
    }

    public Long getDuration(){
        return this.workLogs.stream()
                .map(WorkLog::getDuration)
                .reduce(0L, Long::sum);
    }

    public void update(LocalDate date) {
        this.createDate = date;
        //TODO change day in all Instants in workDays
    }

    public WorkDayStatus getStatus() {
        if (this.workLogs.stream().allMatch(wl -> wl.getStatus() == STOPED)) {
            return STOPED;
        }
        if (this.workLogs.stream().allMatch(wl -> wl.getStatus() == EXPORTED)) {
            return EXPORTED;
        }
        return IN_PROGRSS;
    }

    public void stopTracking() {
        Instant now = buildDateTimeInstant(this.createDate, Instant.now().truncatedTo(ChronoUnit.MINUTES));
        endWorklogs(now);
    }

    public void startTracking(long workLogId) {
        Instant now = buildDateTimeInstant(this.createDate, Instant.now().truncatedTo(ChronoUnit.MINUTES));
        this.workLogs.stream()
                .filter(workLog -> workLog.getId().equals(workLogId))
                .forEach(it -> addWorkLog(new WorkLogDto(
                        null,
                        it.getJiraId(),
                        TIME_FORMATTER.format(now),
                        null,
                        null,
                        it.getJiraName(),
                        it.getComment(),
                        null)));
    }

    public void editWorkLog(WorkLogDto workLogDto) {
        Optional<Instant> startAt = modifyWorkloads(workLogDto);
        Instant ended = isBlank(workLogDto.getEnded()) ? null : buildDateTimeInstant(this.createDate, workLogDto.getEnded());
        Instant endOfDay = buildDateTimeInstantEndOfDay(this.createDate);

        startAt.ifPresent(started -> this.workLogs.stream()
                .filter(workLog -> workLog.getId().equals(workLogDto.getId()))
                .findFirst()
                .ifPresent(worklog -> worklog.updateState(
                        started,
                        ended,
                        workLogDto.getJiraIssiueId(),
                        workLogDto.getJiraIssiueName(),
                        workLogDto.getJiraIssiueComment(),
                        endOfDay)));
    }

    public void addWorkLog(WorkLogDto workLogDto) {
        Optional<Instant> startAt = modifyWorkloads(workLogDto);

        startAt.map(it -> new WorkLog(
                this,
                it,
                isBlank(workLogDto.getEnded())
                        ? null
                        : buildDateTimeInstant(this.createDate, workLogDto.getEnded()),
                workLogDto.getJiraIssiueId(),
                workLogDto.getJiraIssiueName(),
                workLogDto.getJiraIssiueComment(),
                buildDateTimeInstantEndOfDay(this.createDate)))
                .ifPresent(workLogs::add);
    }

    private Optional<Instant> modifyWorkloads(WorkLogDto workLogDto) {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        Instant started = isBlank(workLogDto.getStarted())
                ? buildDateTimeInstant(this.createDate, now)
                : buildDateTimeInstant(this.createDate, workLogDto.getStarted());
        Instant ended = isBlank(workLogDto.getEnded())
                ? buildDateTimeInstantEndOfDay(this.createDate)
                : buildDateTimeInstant(this.createDate, workLogDto.getEnded());

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

    public Set<Long> workLogInConflictIds() {
        return this.workLogs.stream()
                .map(it -> workLogInConflictIds(it.getStarted(), endedOrEndOfDay(it), it.getId()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<Long> workLogInConflictIds(Instant started, Instant ended, Long idToExclude) {
        return this.workLogs.stream()
                .filter(it -> !it.getId().equals(idToExclude))
                .filter(it -> started.isBefore(endedOrEndOfDay(it)) & ended.isAfter(it.getStarted()))
                .map(WorkLog::getId)
                .collect(Collectors.toSet());
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

    private Instant endedOrEndOfDay(WorkLog workLog) {
        return workLog.isEnded() ? workLog.getEnded() : buildDateTimeInstantEndOfDay(this.createDate);
    }

    private void endWorklogs(Instant ended) {
        Instant endOfDay = buildDateTimeInstantEndOfDay(this.createDate);
        this.workLogs.stream()
                .filter(WorkLog::isNotEnded)
                .forEach(it -> it.end(ended, endOfDay));
    }

    private boolean isOverridingOtherWorklog(Instant started, Instant ended, Long idToExclude) {
        return this.workLogs.stream()
                .filter(it -> !it.getId().equals(idToExclude))
                .anyMatch(it -> started.isBefore(it.getStarted()) && ended.isAfter(endedOrEndOfDay(it)));
    }

    private Instant buildDateTimeInstant(LocalDate date, Instant time) {
        Instant started = date.atStartOfDay().atZone(ZoneId.systemDefault())
                .withHour(time.atZone(ZoneId.systemDefault()).getHour())
                .withMinute(time.atZone(ZoneId.systemDefault()).getMinute())
                .toInstant();
        return started;
    }

    private Instant buildDateTimeInstant(LocalDate date, String time) {
        List<Integer> timeParts = Arrays.stream(time.trim().split(":")).
                map(it -> Integer.parseInt(it))
                .collect(Collectors.toList());
        Instant started = date.atStartOfDay().atZone(ZoneId.systemDefault())
                .withHour(timeParts.get(0))
                .withMinute(timeParts.get(1))
                .toInstant();
        return started;
    }

    private Instant buildDateTimeInstantEndOfDay(LocalDate date) {
        Instant started = date.atStartOfDay().atZone(ZoneId.systemDefault())
                .withHour(23)
                .withMinute(59)
                .toInstant();
        return started;
    }

    public void removeWorkLog(long workLogId) {
        this.workLogs.removeIf(it -> it.getId() == workLogId);
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

    @Override
    public int hashCode() {
        return 31;
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