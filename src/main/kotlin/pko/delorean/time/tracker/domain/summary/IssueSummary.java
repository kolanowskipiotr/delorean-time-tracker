package pko.delorean.time.tracker.domain.summary;

import pko.delorean.time.tracker.domain.WorkLog;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class IssueSummary {

    private final String jiraId;
    private final Set<JiraIssue> jiraIssues;
    private final Long ordering;

    public IssueSummary(String jiraId, List<WorkLog> workLogs) {
        this.jiraId = jiraId;
        this.jiraIssues = emptyIfNull(workLogs).stream()
                .sorted(comparing(WorkLog::getStarted))
                .map(workLog -> new JiraIssue(workLog.getJiraName() , workLog.getJiraIssueType(), workLog.getComment()))
                .collect(Collectors.toSet());
        this.ordering = emptyIfNull(workLogs).stream()
                .sorted(comparing(WorkLog::getStarted).reversed())
                .map(WorkLog::getStarted)
                .map(Instant::toEpochMilli)
                .findFirst()
                .orElse(0L);
    }

    public String getJiraId() {
        return jiraId;
    }

    public Set<JiraIssue> getJiraIssues() {
        return jiraIssues;
    }

    public Long getOrdering() {
        return ordering;
    }
}
