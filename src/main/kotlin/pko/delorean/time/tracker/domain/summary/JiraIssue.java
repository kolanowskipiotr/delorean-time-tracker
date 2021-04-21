package pko.delorean.time.tracker.domain.summary;

import pko.delorean.time.tracker.domain.JiraIssueType;
import pko.delorean.time.tracker.domain.WorkLogType;

import java.util.Objects;

public class JiraIssue {
    private final String jiraName;
    private final JiraIssueType jiraIssueType;
    private final WorkLogType workLogType;
    private final String jiraComment;

    public JiraIssue(String jiraName, JiraIssueType jiraIssueType, WorkLogType workLogType, String jiraComment) {
        this.jiraName = jiraName;
        this.jiraIssueType = jiraIssueType;
        this.jiraComment = jiraComment;
        this.workLogType = workLogType;
    }

    public String getJiraName() {
        return jiraName;
    }

    public JiraIssueType getJiraIssueType() {
        return jiraIssueType;
    }

    public String getJiraComment() {
        return jiraComment;
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
                && Objects.equals(workLogType, that.workLogType)
                && Objects.equals(jiraComment, that.jiraComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jiraName, jiraIssueType, workLogType, jiraComment);
    }
}
