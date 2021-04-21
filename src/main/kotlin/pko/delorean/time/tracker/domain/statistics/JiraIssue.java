package pko.delorean.time.tracker.domain.statistics;

import pko.delorean.time.tracker.domain.JiraIssueType;
import pko.delorean.time.tracker.domain.WorkLogType;

import java.util.Objects;

public class JiraIssue {
    private final String jiraName;
    private final JiraIssueType jiraIssueType;
    private final WorkLogType workLogType;

    public JiraIssue(String jiraName, JiraIssueType jiraIssueType, WorkLogType workLogType) {
        this.jiraName = jiraName;
        this.jiraIssueType = jiraIssueType;
        this.workLogType = workLogType;
    }

    public String getJiraName() {
        return jiraName;
    }

    public JiraIssueType getJiraIssueType() {
        return jiraIssueType;
    }

    public WorkLogType getWorkLogType() {
        return workLogType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraIssue that = (JiraIssue) o;
        return Objects.equals(jiraName, that.jiraName)
                && Objects.equals(jiraIssueType, that.jiraIssueType)
                && Objects.equals(workLogType, that.workLogType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jiraName, jiraIssueType, workLogType);
    }
}
