<#include "/macros/issue-utils.ftl">
<#include "/macros/duration-utils.ftl">

<#macro projectsStatistics statistics fullDuration>
    <#list statistics as projectStatistics>
        <#assign projectDuration=projectStatistics.duration/>
        <li class="list-group-item list-group-item-action flex-column align-items-start py-1">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="my-1 w-75 font-weight-bold">${projectStatistics.projectKey}</h5>
                <small>
                    <@duraton projectDuration fullDuration/>
                </small>
            </div>
            <ul class="list-group">
                <#list projectStatistics.issuesStatistics as issueStatistics>
                    <#assign issiueDuration=issueStatistics.duration/>
                    <li class="list-group-item list-group-item-action flex-column align-items-start py-1">
                        <div class="d-flex w-100 justify-content-between my-0">
                            <blockquote class="blockquote my-0"w-75>
                                <footer class="blockquote-footer">
                                    <@issueLink issueStatistics.issueKey jiraUrl/>:
                                    <#list issueStatistics.distinctJiraIssuesByName() as jiraIssue>
                                        <#if jiraIssue.jiraName?has_content>
                                            <@issueType jiraIssue.jiraIssueType/> ${jiraIssue.jiraName?html}
                                            <#if jiraIssue_has_next>, </#if>
                                        </#if>
                                    </#list>
                                </footer>
                            </blockquote>
                            <small>
                                <@duraton issiueDuration fullDuration/>
                            </small>
                        </div>
                    </li>
                </#list>
            </ul>
        </li>
    </#list>
</#macro>