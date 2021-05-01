<#-- @ftlvariable name="workDay" type="pko.delorean.time.tracker.ui.work.day.dto.WorkDayDto" -->

<#include "/header.ftl">
<#include "/macros/issue-utils.ftl">
<#include "/macros/project-utils.ftl">
<#include "/macros/duration-utils.ftl">
<#include "/macros/enum-utils.ftl">

<@header "Work day: ${workDay.date}"/>

<div class="container">
    <h1>Work day: ${workDay.date}</h1>
    <nav class="navbar navbar-light bg-light justify-content-between" style="background-color: #e3f2fd;">
        <a class="navbar-brand">Navigation</a>
        <a class="btn btn-outline-success" href="/jira/credentials/edit" role="button">🔑 Jira credentials</a>
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
                        <#if workDay.state == "IN_PROGRESS">
                            <a class="ml-2 btn btn-danger" href="/work-day/stop?workDayId=${workDay.id?c}" role="button">⏹️ Stop tracking</a>
                        <#elseif workDay.state == "STOPPED">
                            <a class="ml-2 btn btn-primary" href="/work-day/work-log/continue?workDayId=${workDay.id?c}" role="button">➡ Continue</a>
                        <#else>
                            <#assign prityState><@prityName workDay.state/></#assign>
                            <@stateIcon workDay.state!?html "ml-2 btn btn-info" prityState/>
                        </#if>
                        <a class="ml-2 btn btn-success"     href="/work-day/break?workDayId=${workDay.id?c}&breakType=BREAK"                role="button">🏖 Start break</a>
                        <a class="ml-2 btn btn-secondary"   href="/work-day/break?workDayId=${workDay.id?c}&breakType=WORK_ORGANIZATION"    role="button">🗄 Start work organization</a>
                        <a class="ml-2 btn btn-info"        href="/work-day/break?workDayId=${workDay.id?c}&breakType=PRIVATE_TIME"     role="button">🏡 Start private time</a>
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
                    <input type="hidden" class="form-control" id="workLogId" name="workLogId" value="${(searchedWorkLogId?c)!}"/>
                    <input type="text" class="form-control" id="jiraIssueId" name="jiraIssueId" placeholder="JIRA ID" value="${searchedJiraIssueId!?html}" required="required"/>
                    <input type="text" class="form-control" id="jiraIssueType" name="jiraIssueType" placeholder="JIRA Issue type" value="${searchedJiraIssueType!?html}" required="required"/>
                    <input type="text" class="form-control" id="jiraIssueName" name="jiraIssueName" placeholder="JIRA Issue summary" value="${searchedJiraIssueName!?html}"/>
                    <input type="text" class="form-control" id="jiraIssueComment" name="jiraIssueComment" placeholder="Work log comment" value="${searchedJiraIssueComment!?html}"/>
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
                    <th>JIRA Issue summary / Comment</th>
                    <th>State</th>
                    <th style="width: 12%">Actions</th>
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
                                <td class="text-center">
                                    <#if workLog.duration??>
                                        <@duraton workLog.duration/>
                                    </#if>
                                    <#if workLog.breakTime?? && workLog.breakTime gt 0 >
                                        <br><small> + 🏖/🗄 <@duraton workLog.breakTime/></small>
                                    </#if>
                                </td>
                                <td>
                                    <#if workLog.type == "WORK_LOG">
                                        <small><@issueLink workLog.jiraIssiueId jiraUrl/></small>
                                    </#if>
                                </td>
                                <td>
                                    <@issueType workLog.jiraIssueType workLog.type/> ${workLog.jiraIssiueName!?html}
                                    <blockquote class="blockquote">
                                        <#if workLog.jiraIssiueComment?has_content >
                                            <footer class="blockquote-footer float-right">${workLog.jiraIssiueComment!?html}</footer>
                                        </#if>
                                    </blockquote>
                                </td>
                                <td><@stateIcon workLog.status!?html/></td>
                                <td>
                                    <a class="btn btn-primary btn-sm" href="/work-day/edit?workDayId=${workDay.id?c}&searchedWorkLogId=${workLog.id?c}&searchedJiraIssueId=${workLog.jiraIssiueId?url}&searchedJiraIssueType=${workLog.jiraIssueType.name?url}&searchedJiraIssueName=${workLog.jiraIssiueName?url}&searchedJiraIssueComment=${workLog.jiraIssiueComment!?url}&searchedWorkLogStart=${workLog.started!?url}&searchedWorkLogEnd=${workLog.ended!?url}&searchedWorkLogStatus=${workLog.status!?url}" role="button">✏️ Edit</a>
                                    <@action "workDayId" "${workDay.id?c}" "workLogId" "${workLog.id?c}" "my-1 btn btn-danger btn-sm" "/work-day/work-log/delete" "🗑️ Delete" />
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

            <ul class="list-group">
                <div class="list-group-item list-group-item-action flex-column align-items-start active py-1">
                    <div class="d-flex w-100 justify-content-between">
                        <h5 class="mb-1 font-weight-bold">Whole day</h5>
                        <small>
                            <@duraton workDay.statistics.fullDuration workDay.statistics.fullDuration/>
                        </small>
                    </div>
                    <p class="mb-1">${workDay.date}</p>
                </div>
                <#if workDay.statistics??>
                    <@projectsStatistics workDay.statistics, workDay.statistics.fullDuration/>
                </#if>
            </ul>
        </div>
    </div>


    <#macro workDaySummary workDayToPresent>
        <div class="list-group">
            <#if workDayToPresent.summary??>
                <#list workDayToPresent.summary as issueSummary>
                    <div class="list-group-item list-group-item-action flex-column align-items-start py-1">

                        <div class="d-flex w-100 justify-content-between">
                            <h5 class="my-1 w-75">
                                <ul class="list-group">
                                    <#list issueSummary.distinctJiraIssuesByName() as jiraIssue>
                                        <li class="list-group-item border-0
                                            <#if jiraIssue.workLogType == "WORK_LOG">
                                                active
                                            <#elseif jiraIssue.workLogType == "BREAK">
                                                list-group-item-success
                                            <#elseif jiraIssue.workLogType == "WORK_ORGANIZATION">
                                                list-group-item-secondary
                                            <#elseif jiraIssue.workLogType == "PRIVATE_TIME">
                                                list-group-item-info
                                            </#if>
                                            py-1">
                                            <#local jiraNameWithIcon><@issueType jiraIssue.jiraIssueType jiraIssue.workLogType/> ${jiraIssue.jiraName?html}</#local>
                                            <#if jiraIssue.workLogType == "WORK_LOG">
                                                ${jiraNameWithIcon}
                                            <#else>
                                                <small>${jiraNameWithIcon}</small>
                                            </#if>
                                        </li>
                                    </#list>
                                </ul>
                            </h5>
                            <small class="text-muted font-weight-bold">
                                <@issueLink issueSummary.jiraId jiraUrl/>
                            </small>
                        </div>
                        <ul class="list-group">
                            <#list issueSummary.distinctJiraIssuesByComment() as jiraIssue>
                                <#if jiraIssue.jiraComment?has_content >
                                    <li class="list-group-item py-1">
                                    <blockquote class="blockquote my-0">
                                        <footer class="blockquote-footer">
                                            <#if jiraIssue.workLogType == "WORK_LOG">
                                                ${jiraIssue.jiraComment?html}
                                            <#else>
                                                <small>${jiraIssue.jiraComment?html}</small>
                                            </#if>
                                        </footer>
                                    </blockquote>
                                    </li>
                                </#if>
                            </#list>
                        </ul>
                    </div>
                </#list>
            </#if>
        </div>
    </#macro>
    <div class="card">
        <div class="card-body">
            <h3 class="card-title">Summary:</h3>
            <#if workDayBefore??>
                <h4 class="mt-1 card-title text-secondary">Previous work day: <a href="/work-day/edit?workDayId=${workDayBefore.id?c}" role="button">${workDayBefore.date}</a></h4>
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