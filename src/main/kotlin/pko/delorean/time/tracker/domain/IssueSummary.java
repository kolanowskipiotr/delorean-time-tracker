package pko.delorean.time.tracker.domain;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class IssueSummary {

    private final String jiraId;
    private final Set<String> jiraNames;
    private final Set<String> comments;
    private final Long ordering;

    public IssueSummary(String jiraId, List<WorkLog> workLogs) {
        this.jiraId = jiraId;
        this.jiraNames = emptyIfNull(workLogs).stream()
                .sorted(comparing(WorkLog::getStarted))
                .map(WorkLog::getJiraName)
                .collect(Collectors.toSet());
        this.comments = emptyIfNull(workLogs).stream()
                .sorted(comparing(WorkLog::getStarted))
                .map(WorkLog::getComment)
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

    public Set<String> getJiraNames() {
        return jiraNames;
    }

    public Set<String> getComments() {
        return comments;
    }

    public Long getOrdering() {
        return ordering;
    }
}
