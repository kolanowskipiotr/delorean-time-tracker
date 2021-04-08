<#macro issueLink jiraIssiueId jiraUrl="">
    <a href="<#if jiraUrl?has_content>${jiraUrl?html}/browse/${jiraIssiueId?html}</#if>" target="_blank">${jiraIssiueId?html}</a>
</#macro>