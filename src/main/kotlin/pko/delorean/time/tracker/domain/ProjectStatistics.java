package pko.delorean.time.tracker.domain;

import pko.delorean.time.tracker.kernel.Utils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

public class ProjectStatistics {

    private final String projectKey;
    private final Long duration;
    private final List<IssueStatistics> issuesStatistics;

    public ProjectStatistics(String projectKey, List<WorkLog> workLogs, LocalDate workDayDate) {
        this.projectKey = projectKey;
        this.duration = Utils.Companion.sumDurations(workLogs, workDayDate);
        issuesStatistics = workLogs.stream()
                .collect(groupingBy(WorkLog::getJiraId))
                .entrySet().stream()
                .map(it -> new IssueStatistics(it.getKey(), it.getValue(), workDayDate))
                .sorted(comparing(IssueStatistics::getDuration))
                .collect(Collectors.toList());
    }

    public String getProjectKey() {
        return projectKey;
    }

    public Long getDuration() {
        return duration;
    }

    public List<IssueStatistics> getIssuesStatistics() {
        return issuesStatistics;
    }
}
