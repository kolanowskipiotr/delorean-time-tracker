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

    //Hibernate need this
    public WorkLog() {
    }

    public WorkLog(
            WorkDay workDay,
            Instant started,
            Instant ended,
            String jiraIssueId,
            String jiraIssueName,
            String jiraIssueComment,
            Instant endOfDay
    ) {
        this.workDay = workDay;
        updateState(started, ended, jiraIssueId, jiraIssueName, jiraIssueComment, endOfDay);
    }

    public Long getId() {
        return id;
    }

    public WorkDayStatus getStatus() {
        return status;
    }

    public void markExported() {
        this.status = WorkDayStatus.EXPORTED;
    }

    public String getJiraId() {
        return jiraId;
    }

    public String getJiraName() {
        return jiraName;
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

    void updateState(Instant started, Instant ended, String jiraIssueId, String jiraIssueName, String jiraIssueComment, Instant endOfDay) {
        this.jiraId = jiraIssueId;
        this.jiraName = jiraIssueName;
        this.comment = jiraIssueComment;
        this.start(started);
        this.end(ended, endOfDay);
    }

    void end(Instant endAt, Instant endOfDay) {
        if (endAt != null) {
            this.ended = this.started.isAfter(endAt) ? endOfDay : endAt;
            this.status = WorkDayStatus.STOPPED;
        }
    }

    void start(Instant startedAt) {
        this.started = startedAt;
        this.ended = null;
        this.status = WorkDayStatus.IN_PROGRESS;
    }

    void contiune() {
        this.ended = null;
        this.status = WorkDayStatus.IN_PROGRESS;
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
    public int hashCode() {
        return 31;
    }

    void changeDate(LocalDate date) {
        this.started = Utils.Companion.buildDateTimeInstant(date, this.started);
        if(isEnded()) {
            this.ended = Utils.Companion.buildDateTimeInstant(date, this.ended);
        }
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