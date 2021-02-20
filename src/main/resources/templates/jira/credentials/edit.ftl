<#-- @ftlvariable name="jiraCredentials" type="pko.unity.time.tracker.ui.jira.dto.JiraCredentialsDto" -->

<#include "/header.ftl">

<@header "Jira credentials"/>

<div class="container" >
    <h1>Jira credentials</h1>
    <nav class="navbar navbar-light bg-light justify-content-between" style="background-color: #e3f2fd;">
        <a class="navbar-brand">Navigation</a>
        <a class="btn btn-outline-success" href="/work-day/list" role="button">📅 Work days</a>
    </nav>

    <div class="card">
        <#if connectionResult?? >
            <div class="alert <#if connectionResult.success == true >alert-success<#else>alert-danger</#if>" role="alert">
                <h4 class="alert-heading"><#if connectionResult.success == true >Success!<#else>Error!</#if></h4>
                <p>${connectionResult.value}</p>
            </div>
        </#if>
        <div class="card-body">
            <form action="/jira/credentials/edit" method="post">
                <div class="form-group">
                    <label for="jiraUrl" class="sr-only">Jira url</label>
                    <input type="text" class="form-control" id="jiraUrl" name="jiraUrl" placeholder="Jira url" value="${(jiraCredentials.jiraUrl)!"https://jira.unity.pl"}" required="required"/>
                    <label for="jiraUserName" class="sr-only">Jira user</label>
                    <input type="text" class="form-control" id="jiraUserName" name="jiraUserName" placeholder="Jira user" value="${(jiraCredentials.jiraUserName)!}" required="required"/>
                    <label for="jiraUserPassword" class="sr-only">Jira user</label>
                    <div class="form-inline">
                        <div class="form-group">
                            <input type="password" class="form-control" id="jiraUserPassword" name="jiraUserPassword" placeholder="Jira password" value="${(jiraCredentials.jiraUserPassword)!}" required="required"/>
                            <a class="btn btn-secondary btn-lg active" onclick="showHidePassword()">👁️</a>
                        </div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">💾 Save</button>
            </form>
        </div>
    </div>
</div> <!-- /container -->
<#include "/footer.ftl">

<script>
    function showHidePassword() {
        let x = document.getElementById("jiraUserPassword");
        if (x.type === "password") {
            x.type = "text";
        } else {
            x.type = "password";
        }
    }
</script>