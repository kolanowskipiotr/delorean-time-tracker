package pko.delorean.time.tracker.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class JiraIssueType {

    @Column(name = "jira_issue_type")
    private String jiraIssueType;

    //Hibernate need this
    public JiraIssueType() {
    }

    public JiraIssueType(String jiraIssueType) {
        this.jiraIssueType = jiraIssueType;
    }

    public String getValue() {
        return jiraIssueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraIssueType that = (JiraIssueType) o;
        return Objects.equals(jiraIssueType, that.jiraIssueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jiraIssueType);
    }
}
