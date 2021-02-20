package pko.unity.time.tracker.domain;

import com.google.common.annotations.VisibleForTesting;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

import static org.apache.commons.lang3.StringUtils.substringBefore;
import static pko.unity.time.tracker.domain.WorkDayStatus.*;

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

    public void updateState(Instant started, Instant ended, String jiraIssueId, String jiraIssueName, String jiraIssueComment, Instant endOfDay) {
        this.jiraId = jiraIssueId;
        this.jiraName = jiraIssueName;
        this.comment = jiraIssueComment;
        this.start(started);
        this.end(ended, endOfDay);
    }

    public Long getId() {
        return id;
    }

    @VisibleForTesting
    void setId(Long id) {
        this.id = id;
    }

    public WorkDayStatus getStatus() {
        return status;
    }

    public void markExported() {
        this.status = EXPORTED;
    }

    public String getJiraId() {
        return jiraId;
    }

    public String getProjectKey() {
        return substringBefore(this.getJiraId().trim(), "-");
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

    public boolean isEnded() {
        return this.ended != null;
    }

    public boolean isNotEnded() {
        return !isEnded();
    }

    public void end(Instant endAt, Instant endOfDay) {
        if (endAt != null) {
            this.ended = this.started.isAfter(endAt) ? endOfDay : endAt;
            this.status = STOPED;
        }
    }

    void setEnded(Instant endAt) {
        this.ended = endAt;
    }

    public void start(Instant startedAt) {
        this.started = startedAt;
        this.ended = null;
        this.status = IN_PROGRSS;
    }

    public void contiune() {
        this.ended = null;
        this.status = IN_PROGRSS;
    }

    void setStarted(Instant startedAt) {
        this.started = startedAt;
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