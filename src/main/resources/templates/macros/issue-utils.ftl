<#include "/macros/random-utils.ftl">

<#macro issueLink jiraIssiueId jiraUrl="">
    <a href="<#if jiraUrl?has_content>${jiraUrl?html}/browse/${jiraIssiueId?html}</#if>" target="_blank">${jiraIssiueId?html}</a>
</#macro>

<#macro issueType type>
    <#if type.iconUri??>
        <img src="${type.iconUri!}" class="mb-1" title="${type.name?html} <#if type.subtask>(subtask) </#if>- ${type.description!?trim?html}" />
    <#else>
        <@randomEmoji random type.name/>
    </#if>
</#macro>