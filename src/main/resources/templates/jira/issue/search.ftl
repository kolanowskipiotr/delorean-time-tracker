<#-- @ftlvariable name="jiraCredentials" type="pko.unity.time.tracker.ui.jira.dto.JiraCredentialsDto" -->

<#include "/header.ftl">

<@header "Jira search"/>

<div class="container" >
    <h1>Jira search</h1>
    <nav class="navbar navbar-light bg-light justify-content-between" style="background-color: #e3f2fd;">
        <a class="navbar-brand">Navigation</a>
        <a class="btn btn-outline-success" href="/work-day/edit?workDayId=${workDayId?c}" role="button">Work day</a>
    </nav>

    <div class="card">
        <div class="card-body">
            <form action="/jira/issue/search" method="get">
                <div class="form-group">
                    <label for="query" class="sr-only">Query</label>
                    <input type="hidden" class="form-control" id="workDayId" name="workDayId" value="${workDayId?c}"/>
                    <input type="text" class="form-control" id="query" name="query" placeholder="Query" value="${query!}"/>
                </div>
                <button type="submit" class="btn btn-primary">Search</button>
            </form>
        </div>
        <table class="table table-striped">
            <thead class="thead-dark">
            <tr>
                <th style="width: 5%">#</th>
                <th style="width: 35%">JIRA ID</th>
                <th style="width: 35%">JIRA Issue name</th>
                <th style="width: 25%">Actions</th>
            </tr>
            </thead>
            <tbody>
                <#list jiraIssues as jiraIssue>
                    <tr>
                        <td>${jiraIssue_index +1}</td>
                        <td><a href="<#if jiraUrl??>${jiraUrl?html}/browse/${jiraIssue.jiraId?html}</#if>" target="_blank">${jiraIssue.jiraId?html}</a></td>
                        <td>${jiraIssue.jiraName?html}</td>
                        <td>
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group mr-2" role="group">
                                    <a class="btn btn-primary" href="/work-day/edit?workDayId=${workDayId?c}&searchedJiraIssueId=${jiraIssue.jiraId}&searchedJiraIssueName=${jiraIssue.jiraName}&searchedJiraIssueComment=${jiraIssue.comment!}" role="button">Choose</a>
                                </div>
                            </div>
                        </td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>
</div> <!-- /container -->
<#include "/footer.ftl">
