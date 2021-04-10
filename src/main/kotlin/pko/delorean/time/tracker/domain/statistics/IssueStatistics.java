package pko.delorean.time.tracker.domain.statistics;

import pko.delorean.time.tracker.domain.WorkLog;
import pko.delorean.time.tracker.kernel.Utils;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class IssueStatistics {

    private final String issueKey;
    private final Long duration;
    private final Set<JiraIssue> jiraIssues;

    public IssueStatistics(String issueKey, List<WorkLog> workLogs, LocalDate workDayDate) {
        this.issueKey = issueKey;
        this.duration = Utils.Companion.sumDurations(workLogs, workDayDate);
        this.jiraIssues = emptyIfNull(workLogs).stream()
                .sorted(comparing(WorkLog::getStarted))
                .map(workLog -> new JiraIssue(workLog.getJiraName(), workLog.getJiraIssueType()))
                .collect(Collectors.toSet());
    }

    public String getIssueKey() {
        return issueKey;
    }

    public Long getDuration() {
        return duration;
    }

    public Set<JiraIssue> getJiraIssues() {
        return jiraIssues;
    }
}
