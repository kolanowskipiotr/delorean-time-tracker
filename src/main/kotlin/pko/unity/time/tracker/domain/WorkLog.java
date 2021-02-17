package pko.unity.time.tracker.domain;

import com.google.common.annotations.VisibleForTesting;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.OptionalDataException;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static pko.unity.time.tracker.domain.WorkDayStatus.IN_PROGRSS;
import static pko.unity.time.tracker.domain.WorkDayStatus.STOPED;

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

    //Hibernate nied this
    public WorkLog() {
    }

    public WorkLog(WorkDay workDay, Instant started, Instant ended, String jiraIssueId, String jiraIssueName, String jiraIssueComment) {
        this.workDay = workDay;
        this.jiraId = jiraIssueId;
        this.jiraName = jiraIssueName;
        this.comment = jiraIssueComment;
        this.start(started);
        ofNullable(ended)
                .ifPresent(this::end);
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

    public long getTook() {
        Duration duration = Duration.between(started, (ended == null ? Instant.now() : ended));
        return duration.toMinutes();
    }

    public boolean isEnded() {
        return this.ended!= null;
    }

    public boolean isNotEnded() {
        return !isEnded();
    }

    public void end(Instant endAt) {
        this.ended = endAt;
        this.status = STOPED;
    }

    void setEnded(Instant endAt) {
        this.ended = endAt;
    }

    private void start(Instant startedAt) {
        this.started = startedAt;
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