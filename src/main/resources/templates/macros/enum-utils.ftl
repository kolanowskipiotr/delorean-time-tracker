<#macro prityName name>
    ${name!?html?replace("_", " ")?lower_case?cap_first}
</#macro>