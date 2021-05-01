<#include "/macros/issue-utils.ftl">
<#include "/macros/duration-utils.ftl">
<#include "/macros/enum-utils.ftl">

<#macro projectsStatistics statistics, fullDayDuration>
    <#if statistics.privateTime??>
        <#local privateTimeIssueKey><@prityName statistics.privateTime.issueKey/></#local>
        <li class="list-group-item ist-group-item-action flex-column align-items-start py-1">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="my-1 w-75 font-weight-bold">${privateTimeIssueKey}</h5>
                <small>
                    <@duraton statistics.privateTime.duration fullDayDuration/>
                </small>
            </div>
            <ul class="list-group">
                <li class="list-group-item list-group-item-action flex-column align-items-start py-1">
                    <div class="d-flex w-100 justify-content-between my-0">
                        <blockquote class="blockquote my-0"w-75>
                            <footer class="blockquote-footer">
                                <@issueLink privateTimeIssueKey jiraUrl/>:
                                <#list statistics.privateTime.distinctJiraIssuesByName() as jiraIssue>
                                    <#if jiraIssue.jiraName?has_content>
                                        <#local jiraNameWithIcon>
                                            <@issueType jiraIssue.jiraIssueType jiraIssue.workLogType/> <@prityName jiraIssue.jiraName/>
                                            <#if jiraIssue_has_next>, </#if>
                                        </#local>
                                        <#if jiraIssue.workLogType == "WORK_LOG">
                                            ${jiraNameWithIcon}
                                        <#else>
                                            <small>${jiraNameWithIcon}</small>
                                        </#if>
                                    </#if>
                                </#list>
                            </footer>
                        </blockquote>
                        <small>
                            <@duraton statistics.privateTime.duration fullDayDuration/>
                        </small>
                    </div>
                </li>
            </ul>
        </li>
    </#if>
    <ul class="list-group">
        <div class="list-group-item list-group-item-primary flex-column align-items-start py-1">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1 font-weight-bold">Work day</h5>
                <small>
                    <@duraton statistics.duration fullDayDuration/>
                </small>
            </div>
        </div>
        <#list statistics.projectsStatistics as projectStatistics>
            <#assign projectDuration=projectStatistics.duration/>
            <li class="list-group-item list-group-item-action flex-column align-items-start py-1">
                <div class="d-flex w-100 justify-content-between">
                    <h5 class="my-1 w-75 font-weight-bold">${projectStatistics.projectKey}</h5>
                    <small>
                        <@duraton projectDuration fullDayDuration/>
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
                                                <#local jiraNameWithIcon>
                                                    <@issueType jiraIssue.jiraIssueType jiraIssue.workLogType/> ${jiraIssue.jiraName?html}
                                                    <#if jiraIssue_has_next>, </#if>
                                                </#local>
                                                <#if jiraIssue.workLogType == "WORK_LOG">
                                                    ${jiraNameWithIcon}
                                                <#else>
                                                    <small>${jiraNameWithIcon}</small>
                                                </#if>
                                            </#if>
                                        </#list>
                                    </footer>
                                </blockquote>
                                <small>
                                    <@duraton issiueDuration fullDayDuration/>
                                </small>
                            </div>
                        </li>
                    </#list>
                </ul>
            </li>
        </#list>
    </ul>
</#macro>