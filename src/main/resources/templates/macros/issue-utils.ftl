<#include "/macros/random-utils.ftl">

<#macro issueLink jiraIssiueId jiraUrl="">
    <a href="<#if jiraUrl?has_content>${jiraUrl?html}/browse/${jiraIssiueId?html}</#if>" target="_blank">${jiraIssiueId?html}</a>
</#macro>

<#macro issueType jiraType  workLogType = "WORK_LOG">
    <#local workLogTypeDisplayName = workLogType!?html?replace("_", " ")?capitalize/>
    <#if workLogType == "WORK_LOG">
        <#if jiraType.iconUri??>
            <img src="${jiraType.iconUri!}" class="mb-1" title="${jiraType.name?html} <#if jiraType.subtask>(subtask) </#if>- ${jiraType.description!?trim?html}" />
        <#else>
            <@emojiWithAlt "👷" jiraType.name/>
        </#if>
    <#elseif workLogType = "BREAK">
        <@emojiWithAlt "🏖" workLogTypeDisplayName/>
    <#elseif workLogType = "WORK_ORGANIZATION">
        <@emojiWithAlt "🗄" workLogTypeDisplayName/>
    <#elseif workLogType = "PRIVATE_TIME">
        <@emojiWithAlt "🏡" workLogTypeDisplayName/>
    <#else>
        <@randomEmoji random workLogType.name/>
    </#if>
</#macro>

<#macro stateIcon state, class = "", text = "">
    <#switch state>
        <#case "IN_PROGRESS">
            <@emojiWithAlt "➡️" state class text/>
            <#break>
        <#case "STOPPED">
            <@emojiWithAlt "⏹" state class text/>
            <#break>
        <#case "EXPORTED">
            <@emojiWithAlt "✅" state class text/>
            <#break>
        <#case "UNEXPORTABLE">
            <@emojiWithAlt "⛔" state class text/>
            <#break>
        <#default>
            <@randomEmoji random state class text/>
    </#switch>
</#macro>