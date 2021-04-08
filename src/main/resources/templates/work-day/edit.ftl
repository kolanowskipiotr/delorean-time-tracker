<#-- @ftlvariable name="workDay" type="pko.delorean.time.tracker.ui.work.day.dto.WorkDayDto" -->

<#include "/header.ftl">

<@header "Work day: ${workDay.date}"/>

<div class="container" >
    <h1>Work day: ${workDay.date}</h1>
    <nav class="navbar navbar-light bg-light justify-content-between" style="background-color: #e3f2fd;">
        <a class="navbar-brand">Navigation</a>
        <a class="btn btn-outline-success" href="/work-day/list" role="button">📅 Work days</a>
    </nav>

    <#if connectionResult?? >
        <div class="alert <#if connectionResult.success == true >alert-success<#else>alert-danger</#if>" role="alert">
            <h4 class="alert-heading"><#if connectionResult.success == true >Success!<#else>Error!</#if></h4>
            <p>${connectionResult.value}</p>
        </div>
    </#if>

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
                        <a class="ml-2 btn btn-primary" href="/work-day/export?workDayId=${workDay.id?c}" role="button">📤 Export to JIRA</a>
                        <a class="ml-2 btn btn-danger" href="/work-day/stop?workDayId=${workDay.id?c}" role="button">⏹️ Stop tracking</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-body" id="edit-form">
            <h3 class="card-title">Work log: <a class="btn btn-outline-success" href="/jira/issue/search?workDayId=${workDay.id?c}" role="button">🔍 Search JIRA Issue</a></h3>

            <form action="/work-day/work-log<#if searchedWorkLogId??>/edit</#if>" method="post">
                <div class="form-group mb-2">
                    <input type="hidden" class="form-control" id="workDayId" name="workDayId" value="${workDay.id?c}"/>
                    <input type="hidden" class="form-control" id="id" name="id" value="${(searchedWorkLogId?c)!}"/>
                    <input type="text" class="form-control" id="jiraIssiueId" name="jiraIssiueId" placeholder="JIRA ID" value="${searchedJiraIssueId!?html}" required="required"/>
                    <input type="text" class="form-control" id="jiraIssiueName" name="jiraIssiueName" placeholder="JIRA Issue summary" value="${searchedJiraIssueName!?html}"/>
                    <input type="text" class="form-control" id="jiraIssiueComment" name="jiraIssiueComment" placeholder="Comment" value="${searchedJiraIssueComment!?html}"/>
                    <div class="form-inline">
                        <div class="form-group">
                            <label for="started" class="control-label mr-2">Start: </label>
                            <input type="time" class="form-control" id="started" name="started" placeholder="Started at" value="${searchedWorkLogStart!}"/>
                            <label for="ended" class="control-label ml-4 mr-2">End: </label>
                            <input type="time" class="form-control" id="ended" name="ended" placeholder="Ended at" value="${searchedWorkLogEnd!}"/>
                        </div>
                    </div>
                </div>
                <#if searchedWorkLogId?? >
                    <div class="form-inline">
                        <div class="form-group">
                            <button type="submit" class="btn btn-primary">💾 Save</button>
                            <a class="ml-2 btn btn-primary" href="/work-day/work-log/start?workDayId=${workDay.id?c}&workLogId=${searchedWorkLogId?c}" role="button">▶ Start new like this</a>
                            <a class="ml-2 btn btn-primary<#if !workDay.workLogs?has_content || workDay.workLogs?last.id != searchedWorkLogId> disabled</#if>" href="/work-day/work-log/continue?workDayId=${workDay.id?c}&workLogId=${searchedWorkLogId?c}" role="button">➡ Continue</a>
                            <a class="ml-2 btn btn-primary" href="/work-day/work-log/export/toggle?workDayId=${workDay.id?c}&workLogId=${searchedWorkLogId?c}" role="button"><#if searchedWorkLogStatus == "EXPORTED">🔓 Enable export to JIRA<#else>🔒 Disable export to JIRA</#if></a>
                            <a class="ml-2 btn btn-warning" href="/work-day/edit?workDayId=${workDay.id?c}" role="button">❌️ Cancel</a>
                        </div>
                    </div>
                <#else>
                    <button type="submit" class="btn btn-primary">➕ add</button>
                </#if>
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
                    <th>Duration</th>
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
                                <td class="text-center"><#if workLog.duration??>${workLog.duration}m<br>(${(workLog.duration/60)?floor}h ${workLog.duration - ((workLog.duration/60)?floor * 60)}m)</#if> </td>
                                <td><a href="<#if jiraUrl??>${jiraUrl?html}/browse/${workLog.jiraIssiueId?html}</#if>" target="_blank">${workLog.jiraIssiueId?html}</a></td>
                                <td>${workLog.jiraIssiueName!?html}</td>
                                <td>${workLog.jiraIssiueComment!?html}</td>
                                <td>${workLog.status!?html}</td>
                                <td>
                                    <div class="btn-toolbar" role="toolbar">
                                        <div class="btn-group mr-2" role="group">
                                            <a class="btn btn-primary" href="/work-day/edit?workDayId=${workDay.id?c}&searchedWorkLogId=${workLog.id?c}&searchedJiraIssueId=${workLog.jiraIssiueId?url}&searchedJiraIssueName=${workLog.jiraIssiueName?url}&searchedJiraIssueComment=${workLog.jiraIssiueComment!?url}&searchedWorkLogStart=${workLog.started!?url}&searchedWorkLogEnd=${workLog.ended!?url}&searchedWorkLogStatus=${workLog.status!?url}" role="button">✏️ Edit</a>
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

    <div class="card">
        <div class="card-body">
            <h3 class="card-title">Statistics:</h3>

            <div class="list-group">
                <div class="list-group-item list-group-item-action flex-column align-items-start active py-2">
                    <div class="d-flex w-100 justify-content-between">
                        <h5 class="mb-1">Whole day</h5>
                        <small>${workDay.date} 100%</small>
                    </div>
                    <p class="mb-1"><#if workDay.duration??>${workDay.duration}m (${(workDay.duration/60)?floor}h ${workDay.duration - ((workDay.duration/60)?floor * 60)}m)</#if></p>
                </div>
                <#if workDay.statistics??>
                    <#list workDay.statistics?keys as key>
                        <#assign val=workDay.statistics[key]/>
                        <div class="list-group-item list-group-item-action flex-column align-items-start py-2">
                            <div class="d-flex w-100 justify-content-between">
                                <h5 class="mb-1">${key}</h5>
                                <small><#if workDay.duration gt 0 >${val/workDay.duration*100}<#else>0</#if>%</small>
                            </div>
                            <p class="mb-1"><#if val??>${val}m (${(val/60)?floor}h ${val - ((val/60)?floor * 60)}m)</#if></p>
                        </div>
                    </#list>
                </#if>
            </div>
        </div>
    </div>


    <#macro workDaySummary workDayToPresent>
        <div class="list-group">
            <#if workDayToPresent.summary??>
                <#list workDayToPresent.summary as issueSummary>
                    <div class="list-group-item list-group-item-action flex-column align-items-start">

                        <div class="d-flex w-100 justify-content-between">
                            <h5 class="mb-1 w-75">
                                <ul class="list-group">
                                    <#list issueSummary.jiraNames as jiraName>
                                        <li class="list-group-item border-0 active py-1">${jiraName?html}</li>
                                    </#list>
                                </ul>
                            </h5>
                            <small class="text-muted font-weight-bold">
                                <a href="<#if jiraUrl??>${jiraUrl?html}/browse/${issueSummary.jiraId?url}</#if>" target="_blank">${issueSummary.jiraId?html}</a>
                            </small>
                        </div>
                        <p class="mb-1">
                            <ul class="list-group">
                                <#list issueSummary.comments as comment>
                                    <li class="list-group-item py-1">${comment?html}</li>
                                </#list>
                            </ul>
                        </p>
                    </div>
                </#list>
            </#if>
        </div>
    </#macro>
    <div class="card">
        <div class="card-body">
            <h3 class="card-title">Summary:</h3>
            <#if workDayBefore??>
                <h4 class="mt-1 card-title text-secondary">Previous work day: ${workDayBefore.date}</h4>
                <@workDaySummary workDayBefore />
            </#if>
            <h4 class="mt-1  card-title">Current work day: ${workDay.date}</h4>
            <@workDaySummary workDay />
        </div>
    </div>
</div> <!-- /container -->
<#include "/footer.ftl">
<script>
    $('document').ready(function(){
        <#if searchedJiraIssueId?has_content>
        catchFocus();
        </#if>
    });

    function catchFocus() {
        let glower = $("#edit-form");
        glower.addClass('active');
        window.setTimeout(function() {
            glower.removeClass('active');
        }, 2000);
    }
</script>
<style>
    #edit-form.active {
        border-color: #6c757d;
        -webkit-box-shadow: 0 0 10px #6c757d;
        -moz-box-shadow: 0 0 10px #6c757d;
        box-shadow: 0 0 10px #6c757d;
    }
</style>