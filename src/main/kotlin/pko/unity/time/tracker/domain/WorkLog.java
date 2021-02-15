package pko.unity.time.tracker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pko.unity.time.tracker.ui.work.day.dto.WorkLogDto;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

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
    @JsonIgnoreProperties(value = "workLogs", allowSetters = true)
    private WorkDay workDay;

    //Hibernate nied this
    public WorkLog() {
    }

    public WorkLog(WorkDay workDay, WorkLogDto workLogDto) {
        this.workDay = workDay;
        this.jiraId = workLogDto.getJiraIssiueId();
        this.jiraName = workLogDto.getJiraIssiueName();
        this.comment = workLogDto.getJiraIssiueComment();
        this.started = adjustInstant(Instant.now());
    }

    private Instant adjustInstant(Instant instant){
        return instant
                .truncatedTo(ChronoUnit.MINUTES);
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJiraId() {
        return jiraId;
    }

    public WorkLog jiraId(String jiraId) {
        this.jiraId = jiraId;
        return this;
    }

    public void setJiraId(String jiraId) {
        this.jiraId = jiraId;
    }

    public String getJiraName() {
        return jiraName;
    }

    public WorkLog jiraName(String jiraName) {
        this.jiraName = jiraName;
        return this;
    }

    public void setJiraName(String jiraName) {
        this.jiraName = jiraName;
    }

    public String getComment() {
        return comment;
    }

    public WorkLog comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getStarted() {
        return started;
    }

    public WorkLog start(Instant start) {
        this.started = adjustInstant(start);
        return this;
    }

    public void setStarted(Instant start) {
        this.started = adjustInstant(start);
    }

    public Instant getEnded() {
        return ended;
    }

    public long getTook() {
        Duration duration = Duration.between(started, (ended == null ? Instant.now() : ended));
        return duration.toMinutes();
    }

    public WorkLog end(Instant end) {
        this.ended = adjustInstant(end);
        return this;
    }

    public void setEnded(Instant end) {
        this.ended = adjustInstant(end);
    }

    public WorkDay getWorkDay() {
        return workDay;
    }

    public WorkLog workDay(WorkDay workDay) {
        this.workDay = workDay;
        return this;
    }

    public void setWorkDay(WorkDay workDay) {
        this.workDay = workDay;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
                "}";
    }
}