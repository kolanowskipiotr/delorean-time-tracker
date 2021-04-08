package pko.delorean.time.tracker.domain;

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
    private final Set<String> jiraNames;

    public IssueStatistics(String issueKey, List<WorkLog> workLogs, LocalDate workDayDate) {
        this.issueKey = issueKey;
        this.duration = Utils.Companion.sumDurations(workLogs, workDayDate);
        this.jiraNames = emptyIfNull(workLogs).stream()
                .sorted(comparing(WorkLog::getStarted))
                .map(WorkLog::getJiraName)
                .collect(Collectors.toSet());
    }

    public String getIssueKey() {
        return issueKey;
    }

    public Long getDuration() {
        return duration;
    }

    public Set<String> getJiraNames() {
        return jiraNames;
    }
}
