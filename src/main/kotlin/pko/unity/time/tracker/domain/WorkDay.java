package pko.unity.time.tracker.domain;

import pko.unity.time.tracker.ui.work.day.dto.WorkLogDto;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static pko.unity.time.tracker.domain.WorkDayStatus.IN_PROGRSS;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkDayStatus status;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true, mappedBy = "workDay")
    private Set<WorkLog> workLogs = new HashSet<>();

    //Hibernate nied this
    public WorkDay() {
    }

    public WorkDay(LocalDate createDate) {
        this.createDate = createDate;
        this.status = IN_PROGRSS;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public WorkDay createDate(LocalDate createDate) {
        this.createDate = createDate;
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public WorkDayStatus getStatus() {
        return status;
    }

    public WorkDay status(WorkDayStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(WorkDayStatus status) {
        this.status = status;
    }

    public Set<WorkLog> getWorkLogs() {
        return workLogs;
    }

    public WorkDay workLogs(Set<WorkLog> workLogs) {
        this.workLogs = workLogs;
        return this;
    }

    public WorkDay addWorkLog(WorkLog workLog) {
        this.workLogs.add(workLog);
        workLog.setWorkDay(this);
        return this;
    }

    public WorkDay removeWorkLog(WorkLog workLog) {
        this.workLogs.remove(workLog);
        workLog.setWorkDay(null);
        return this;
    }

    public void setWorkLogs(Set<WorkLog> workLogs) {
        this.workLogs = workLogs;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
                ", status='" + getStatus() + "'" +
                "}";
    }

    public void update(LocalDate date) {
        this.createDate = date;
    }

    public void addWorkLog(WorkLogDto workLogDto) {
        this.workLogs.add(new WorkLog(this, workLogDto));
    }

    public void removeWorkLog(long workLogId) {
        this.workLogs.removeIf(it -> it.getId() == workLogId);
    }
}