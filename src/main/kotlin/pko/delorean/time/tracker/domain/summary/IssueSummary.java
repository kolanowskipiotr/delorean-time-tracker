package pko.delorean.time.tracker.domain.summary;

import pko.delorean.time.tracker.domain.WorkLog;
import pko.delorean.time.tracker.domain.WorkLogType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.Instant.MIN;
import static java.util.Comparator.comparing;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class IssueSummary {

    private final String jiraId;
    private final Set<JiraIssue> jiraIssues;
    private final Instant started;
    private WorkLogType workLogType;

    public IssueSummary(String jiraId, List<WorkLog> workLogs) {
        this.jiraId = jiraId;
        List<WorkLog> sortedWorkLogs = emptyIfNull(workLogs).stream()
                .sorted(comparing(WorkLog::getStarted)
                        .reversed())
                .collect(Collectors.toList());
        this.jiraIssues = sortedWorkLogs.stream()
                .map(workLog -> new JiraIssue(workLog.getJiraName() , workLog.getJiraIssueType(), workLog.getType(), workLog.getComment()))
                .collect(Collectors.toSet());

        Optional<WorkLog> workLogRepresentative = sortedWorkLogs.stream().findFirst();
        this.started = workLogRepresentative
                .map(WorkLog::getStarted)
                .orElse(MIN);
        this.workLogType = workLogRepresentative
                .map(WorkLog::getType)
                .orElse(WorkLogType.WORK_LOG);
    }

    public String getJiraId() {
        return jiraId;
    }

    public Set<JiraIssue> getJiraIssues() {
        return jiraIssues;
    }

    public Instant getStarted() {
        return started;
    }

    public WorkLogType getWorkLogType() {
        return workLogType;
    }
}
