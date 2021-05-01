package pko.delorean.time.tracker.domain.statistics;

import java.util.List;

public class WorkDayStatistics {
    private final Long workDayDuration;
    private final List<ProjectStatistics> projectsStatistics;
    private final IssueStatistics privateTime;

    public WorkDayStatistics(Long workDayDuration, List<ProjectStatistics> projectsStatistics, IssueStatistics privateTime) {
        this.workDayDuration = workDayDuration;
        this.projectsStatistics = projectsStatistics;
        this.privateTime = privateTime;
    }

    public Long getWorkDayDuration() {
        return workDayDuration;
    }

    public List<ProjectStatistics> getProjectsStatistics() {
        return projectsStatistics;
    }

    public IssueStatistics getPrivateTime() {
        return privateTime;
    }
}
