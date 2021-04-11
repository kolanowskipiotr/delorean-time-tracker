<#macro duraton value fullDuration>
    <#if fullDuration gt 0 >${(value/fullDuration*100)?string["0.#"]}<#else>0</#if>%
    -
    <#if value??>${(value/60)?floor}h ${value - ((value/60)?floor * 60)}m (${value}m)</#if>
</#macro>