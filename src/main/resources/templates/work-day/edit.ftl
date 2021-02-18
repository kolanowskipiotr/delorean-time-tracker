<#-- @ftlvariable name="workDay" type="pko.unity.time.tracker.ui.work.day.dto.WorkDayDto" -->

<#include "/header.ftl">

<@header "Work day: ${workDay.date}"/>

<div class="container" >
    <h1>Work day: ${workDay.date}</h1>
    <nav class="navbar navbar-light bg-light justify-content-between" style="background-color: #e3f2fd;">
        <a class="navbar-brand">Navigation</a>
        <a class="btn btn-outline-success" href="/work-day/list" role="button">📅 Work days</a>
    </nav>
    <div class="card">
        <div class="card-body">
            <form action="/work-day/edit" method="post">
                <div class="form-inline">
                    <div class="form-group mb-2">
                        <input type="hidden" class="form-control" id="workDayId" name="workDayId" value="${workDay.id?c}"/>
                        <label for="date" class="control-label col-sm-2" >Date: </label>
                        <input type="date" class="form-control" id="date" name="date" placeholder="dd.MM.yyyy" value="${workDay.date}" required="required"/>
                    </div>
                </div>
                <div class="form-inline">
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary">💾 Chanege date</button>
                        <a class="ml-2 btn btn-danger" href="/work-day/stop?workDayId=${workDay.id?c}" role="button">⏹️ Stop tracking</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-body">
            <h3 class="card-title">Work log: <a class="btn btn-outline-success" href="/jira/issue/search?workDayId=${workDay.id?c}" role="button">🔍 Search JIRA Issue</a></h3>

            <form action="/work-day/work-log" method="post">
                <div class="form-group mb-2">
                    <input type="hidden" class="form-control" id="workDayId" name="workDayId" value="${workDay.id?c}"/>
                    <input type="text" class="form-control" id="jiraIssiueId" name="jiraIssiueId" placeholder="JIRA ID" value="${searchedJiraIssueId!?html}" required="required"/>
                    <input type="text" class="form-control" id="jiraIssiueName" name="jiraIssiueName" placeholder="JIRA Issue summary" value="${searchedJiraIssueName!?html}"/>
                    <input type="text" class="form-control" id="jiraIssiueComment" name="jiraIssiueComment" placeholder="Comment" value="${searchedJiraIssueComment!?html}"/>
                    <div class="form-inline">
                        <div class="form-group">
                            <label for="started" class="control-label mr-2">Start: </label>
                            <input type="time" class="form-control" id="started" name="started"
                                   placeholder="Started at"/>
                            <label for="ended" class="control-label ml-4 mr-2">End: </label>
                            <input type="time" class="form-control" id="ended" name="ended" placeholder="Ended at"/>
                        </div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">💾 Save</button>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-body">
            <table class="table table-striped">
                <thead class="thead-dark">
                <tr>
                    <th></th>
                    <th>#</th>
                    <th>Started</th>
                    <th>Ended</th>
                    <th>Took</th>
                    <th>JIRA ID</th>
                    <th>JIRA Issue summary</th>
                    <th>Comment</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <#if workDay.workLogs??>
                    <#list workDay.workLogs as workLog>

                        <tr>
                            <td class="<#if workLogIdsInConflict?seq_contains(workLog.id) >bg-danger text-white<#elseif workLog.status == "EXPORTED">bg-success text-white <#elseif workLog.status == "IN_PROGRSS">bg-primary text-white</#if>" ></td>
                            <td>${workLog_index +1}</td>
                            <td>${workLog.started!?html}</td>
                            <td>${workLog.ended!?html}</td>
                            <td>${workLog.took!} minutes</td>
                            <td><a href="<#if jiraUrl??>${jiraUrl?html}/browse/${workLog.jiraIssiueId?html}</#if>" target="_blank">${workLog.jiraIssiueId?html}</a></td>
                            <td>${workLog.jiraIssiueName!?html}</td>
                            <td>${workLog.jiraIssiueComment!?html}</td>
                            <td>${workLog.status!?html}</td>
                            <td>
                                <div class="btn-toolbar" role="toolbar">
                                    <div class="btn-group mr-2" role="group">
                                        <a class="btn btn-primary" href="/work-day/work-log/edit?workDayId=${workDay.id?c}&workLogId=${workLog.id?c}" role="button">✏️ Edit</a>
                                    </div>
                                    <div class="btn-group mr-2" role="group">
                                        <@action "workDayId" "${workDay.id?c}" "workLogId" "${workLog.id?c}" "btn btn-danger" "/work-day/work-log/delete" "🗑️ Delete" />
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