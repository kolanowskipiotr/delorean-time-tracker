<#-- @ftlvariable name="workDay" type="pko.unity.time.tracker.ui.work.day.dto.WorkDayDto" -->

<#include "/header.ftl">

<@header "Work day: ${workDay.date}"/>

<div class="container" >
    <h1>Work day: ${workDay.date}</h1>
    <nav class="navbar navbar-light bg-light justify-content-between" style="background-color: #e3f2fd;">
        <a class="navbar-brand">Navigation</a>
        <a class="btn btn-outline-success" href="/work-day/list" role="button">Work days</a>
    </nav>
    <div class="card">
        <div class="card-body">
            <form action="/work-day/edit" method="post">
                <div class="form-group">
                    <input type="hidden" class="form-control" id="workDayId" name="workDayId" value="${workDay.id?c}"/>
                    <label for="date" class="sr-only">Date</label>
                    <input type="date" class="form-control" id="date" name="date" placeholder="dd.MM.yyyy" value="${workDay.date}" required="required"/>
                </div>
                <button type="submit" class="btn btn-primary">Save</button>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-body">
            <h3 class="card-title">Work logs</h3>
            <form class="" action="/work-day/work-log" method="post">
                <input type="hidden" class="form-control" id="workDayId" name="workDayId" value="${workDay.id?c}"/>
                <div class="form-group mb-2">
                    <a class="btn btn-outline-success" href="/jira/issue/search?workDayId=${workDay.id?c}" role="button">Search for JIRA Issue</a>
                    <input type="text" class="form-control" id="jiraIssiueId" name="jiraIssiueId" placeholder="JIRA ID" value="${searchedJiraIssueId!?html}" required="required"/>
                    <input type="text" class="form-control" id="jiraIssiueName" name="jiraIssiueName" placeholder="JIRA Issue summary" value="${searchedJiraIssueName!?html}"/>
                    <input type="text" class="form-control" id="jiraIssiueComment" name="jiraIssiueComment" placeholder="Comment" value="${searchedJiraIssueComment!?html}"/>
                </div>
                <button type="submit" class="btn btn-primary mb-2">Add work log</button>
            </form>
            <table class="table table-striped">
                <thead class="thead-dark">
                <tr>
                    <th>#</th>
                    <th>Started</th>
                    <th>Ended</th>
                    <th>Took</th>
                    <th>JIRA ID</th>
                    <th>JIRA Issue summary</th>
                    <th>Comment</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                    <#if workDay.workLogs??>
                        <#list workDay.workLogs as workLog>
                            <tr>
                                <td>${workLog_index +1}</td>
                                <td>${timeFormat.getTimeString(workLog.started)}</td>
                                <td>${timeFormat.getTimeString(workLog.ended)}</td>
                                <td>${workLog.took!} minutes</td>
                                <td><a href="<#if jiraUrl??>${jiraUrl?html}/browse/${workLog.jiraIssiueId?html}</#if>" target="_blank">${workLog.jiraIssiueId?html}</a></td>
                                <td>${workLog.jiraIssiueName!?html}</td>
                                <td>${workLog.jiraIssiueComment!?html}</td>
                                <td>
                                    <div class="btn-toolbar" role="toolbar">
                                        <div class="btn-group mr-2" role="group">
                                            <a class="btn btn-primary" href="/work-day/work-log/edit?workDayId=${workDay.id?c}&workLogId=${workLog.id?c}" role="button">Edit</a>
                                        </div>
                                        <div class="btn-group mr-2" role="group">
                                            <@action "workDayId" "${workDay.id?c}" "workLogId" "${workLog.id?c}" "btn btn-danger" "/work-day/work-log/delete" "Delete" />
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </#list>
                    </#if>
                </tbody>
            </table>
        </div>
    </div>
</div> <!-- /container -->
<#include "/footer.ftl">