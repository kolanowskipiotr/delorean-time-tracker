package pko.delorean.time.tracker.domain.statistics;

import pko.delorean.time.tracker.domain.JiraIssueType;

import java.util.Objects;

public class JiraIssue {
    private final String jiraName;
    private final JiraIssueType jiraIssueType;

    public JiraIssue(String jiraName, JiraIssueType jiraIssueType) {
        this.jiraName = jiraName;
        this.jiraIssueType = jiraIssueType;
    }

    public String getJiraName() {
        return jiraName;
    }

    public JiraIssueType getJiraIssueType() {
        return jiraIssueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraIssue that = (JiraIssue) o;
        return Objects.equals(jiraName, that.jiraName) && Objects.equals(jiraIssueType, that.jiraIssueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jiraName, jiraIssueType);
    }
}
