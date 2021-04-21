package pko.delorean.time.tracker.domain;

import com.google.common.annotations.VisibleForTesting;
import pko.delorean.time.tracker.kernel.Utils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import static org.apache.commons.lang3.StringUtils.substringBefore;
import static pko.delorean.time.tracker.domain.WorkDayStatus.EXPORTED;
import static pko.delorean.time.tracker.domain.WorkDayStatus.UNEXPORTABLE;

/**
 * A WorkLog.
 */
@Entity
@Table(name = "work_logs")
public class WorkLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jira_id", nullable = false)
    private String jiraId;

    @NotNull
    @Column(name = "jira_name", nullable = false)
    private String jiraName;

    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "started", nullable = false)
    private Instant started;

    @Column(name = "ended")
    private Instant ended;

    @ManyToOne
    private WorkDay workDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkDayStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private WorkLogType type;

    @Embedded
    private JiraIssueType jiraIssueType;

    @Column(name = "break_In_minutes")
    private Long breakInMinutes;

    //Hibernate need this
    public WorkLog() {
    }

    public WorkLog(
            WorkDay workDay,
            WorkLogType type,
            Instant started,
            Instant ended,
            String jiraIssueId,
            JiraIssueType jiraIssueType,
            String jiraIssueName,
            String jiraIssueComment,
            Instant endOfDay) {
        this.workDay = workDay;
        this.type = type;
        if(type.isUnexportable()){
            status = UNEXPORTABLE;
        }
        this.breakInMinutes = 0L;
        updateState(started, ended, jiraIssueId, jiraIssueType, jiraIssueName, jiraIssueComment, endOfDay);
    }

    public Long getId() {
        return id;
    }

    public WorkLogType getType() {
        return type;
    }

    boolean isDividable() {
        return type.isDividable();
    }

    public boolean isUndividable(){
        return type.isUndividable();
    }

    boolean isExportable() {
        return type.isExportable();
    }

    public boolean isUnexportable(){
        return type.isUnexportable();
    }

    public WorkDayStatus getStatus() {
        return status;
    }

    public void markExported() {
        changeStatus(EXPORTED);
    }

    public void markUnexported() {
            if (this.isEnded()) {
                changeStatus(WorkDayStatus.STOPPED);
            } else {
                changeStatus(WorkDayStatus.IN_PROGRESS);
            }
    }

    public boolean isExported() {
        return this.status == EXPORTED;
    }

    public boolean isUnexported() {
        return !isExported();
    }

    public String getJiraId() {
        return jiraId;
    }

    public String getJiraName() {
        return jiraName;
    }

    public JiraIssueType getJiraIssueType() {
        return jiraIssueType;
    }

    public String getComment() {
        return comment;
    }

    public Instant getStarted() {
        return started;
    }

    public Instant getEnded() {
        return ended;
    }

    public long getDuration(Instant endedDefault) {
        Duration duration = Duration.between(started, (ended == null ? endedDefault : ended));
        return duration.toMinutes();
    }

    public Long getBreak(){
        return breakInMinutes;
    }
    public void addBreak(Long minutes) {
        this.breakInMinutes += minutes;
    }

    @VisibleForTesting
    void setId(Long id) {
        this.id = id;
    }

    String getProjectKey() {
        return substringBefore(this.getJiraId().trim(), "-");
    }

    boolean isEnded() {
        return this.ended != null;
    }

    boolean isNotEnded() {
        return !isEnded();
    }

    void setStarted(Instant startedAt) {
        this.started = startedAt;
    }

    void setEnded(Instant endAt) {
        this.ended = endAt;
    }

    void updateState(Instant started, Instant ended, String jiraIssueId, JiraIssueType jiraIssueType, String jiraIssueName, String jiraIssueComment, Instant endOfDay) {
        this.jiraId = jiraIssueId;
        this.jiraIssueType = jiraIssueType;
        this.jiraName = jiraIssueName;
        this.comment = jiraIssueComment;
        this.start(started);
        this.end(ended, endOfDay);
    }

    void end(Instant endAt, Instant endOfDay) {
        if (endAt != null) {
            this.ended = this.started.isAfter(endAt) ? endOfDay : endAt;
            changeStatus(WorkDayStatus.STOPPED);
        }
    }

    void start(Instant startedAt) {
        this.started = startedAt;
        this.ended = null;
        changeStatus(WorkDayStatus.IN_PROGRESS);
    }

    void contiune() {
        this.ended = null;
        changeStatus(WorkDayStatus.IN_PROGRESS);
    }

    void changeDate(LocalDate date) {
        this.started = Utils.Companion.buildDateTimeInstant(date, this.started);
        if(isEnded()) {
            this.ended = Utils.Companion.buildDateTimeInstant(date, this.ended);
        }
    }

    private void changeStatus(WorkDayStatus status){
        if(type.isExportable()) {
            this.status = status;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkLog)) {
            return false;
        }
        return id != null && id.equals(((WorkLog) o).id);
    }

    @Override
    public int hashCode() {//FIXME: This is wrong
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkLog{" +
                "id=" + getId() +
                ", jiraId='" + getJiraId() + "'" +
                ", jiraName='" + getJiraName() + "'" +
                ", comment='" + getComment() + "'" +
                ", start='" + getStarted() + "'" +
                ", end='" + getEnded() + "'" +
                ", status='" + getStatus() + "'" +
                "}";
    }
}