<#macro header title>
    <!DOCTYPE html>
    <html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <meta name="description" content="">
        <meta name="author" content="">
        <link rel="icon" href="../../favicon.ico"/>

        <title>${title}</title>

        <!-- Bootstrap core CSS -->
        <link href="/css/bootstrap.css" rel="stylesheet" th:href="@{/css/bootstrap.css}"/>
    </head>
    <body>
</#macro>

<#macro action param1 value1 param2 value2 class='' href='' label='' >
    <form action="${href}" method="post">
        <#if param1?has_content>
            <input type="hidden" class="form-control" id="${param1}" name="${param1}" value="${value1}">
        </#if>
        <#if param2?has_content>
            <input type="hidden" class="form-control" id="${param2}" name="${param2}" value="${value2}">
        </#if>
        <button type="submit" class="${class}">${label}</button>
    </form>
</#macro>