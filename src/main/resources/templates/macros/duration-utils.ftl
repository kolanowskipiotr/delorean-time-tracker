<#macro duraton value fullDuration = "">
    <#if fullDuration?has_content>
        <#if fullDuration gt 0 >${(value/fullDuration*100)?string["0.#"]}<#else>0</#if>% -
    </#if>
    <#if value??>${(value/60)?floor}h ${value - ((value/60)?floor * 60)}m <small>(${value}m)</small></#if>
</#macro>