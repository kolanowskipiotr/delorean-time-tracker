<#-- @ftlvariable name="templates" type="pko.delorean.time.tracker.ui.work.day.dto.WorkDayDto[]" -->
<#-- @ftlvariable name="template" type="pko.delorean.time.tracker.ui.work.day.dto.WorkDayDto" -->

<#include "/header.ftl">

<@header "Work days"/>

<div class="container" >
    <h1>Work days</h1>
    <nav class="navbar navbar-light bg-light justify-content-between" style="background-color: #e3f2fd;">
        <a class="navbar-brand">Navigation</a>
        <a class="btn btn-outline-success" href="/jira/credentials/edit" role="button">🔑 Jira credentials</a>
        <a class="btn btn-outline-secondary" href="/h2-console" role="button">🗄️ H2 database console</a>
    </nav>
    <div class="card">
        <div class="card-body">
            <form action="/work-day/add" method="post">
                <div class="form-inline">
                    <div class="form-group mb-2 w-75">
                        <label for="date" class="control-label col-sm-2" >Date: </label>
                        <input type="date" class="form-control" id="date" name="date" placeholder="dd.MM.yyyy" required="required"/>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary mb-2">➕ New work day</button>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-body">
            <form action="/work-day/list" method="get">
                <div class="form-inline">
                    <div class="form-group mb-2 w-75">
                        <label for="date" class="control-label col-sm-2" >Created from: </label>
                        <input type="date" class="form-control" id="createDateStart" name="createDateStart" placeholder="dd.MM.yyyy" value="${filters.createDateStart!}"/>
                        <label for="date" class="control-label col-sm-2" >Created to: </label>
                        <input type="date" class="form-control" id="createDateEnd" name="createDateEnd" placeholder="dd.MM.yyyy" value="${filters.createDateEnd!}"/>
                    </div>
                </div>
                <div class="form-inline">
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary mb-2">🔍 Filter</button>
                        <a class="mb-2 ml-2 btn btn-warning" href="/work-day/list" role="button">🧹 Clear</a>
                    </div>
                </div>
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
                    <th>Day</th>
                    <th>Duration</th>
                    <th>Statistics</th>
                    <th>State</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <#list workDays as workDay>
                    <tr>
                        <td class="<#if workDay.state == "EXPORTED">bg-success text-white <#elseif workDay.state == "IN_PROGRSS">bg-primary text-white</#if>" ></td>
                        <td>${workDay_index +1}</td>
                        <td>${workDay.date?html}</td>
                        <td class="text-center"><#if workDay.duration??>${workDay.duration}m<br>(${(workDay.duration/60)?floor}h ${workDay.duration - ((workDay.duration/60)?floor * 60)}m)</#if> </td>
                        <td>
                            <#if workDay.statistics??>
                                <ul style="list-style-type:none;">
                                    <#list workDay.statistics?keys as key>
                                        <li>
                                            <#assign val=workDay.statistics[key]/>
                                            ${key} - <#if val?? && workDay.duration gt 0> ${val/workDay.duration*100}% - ${val}m (${(val/60)?floor}h ${val - ((val/60)?floor * 60)}m)<#else>0% - 0m (0h 0m)</#if>
                                        </li>
                                    </#list>
                                </ul>
                            </#if>
                        </td>
                        <td>${workDay.state?html}</td>
                        <td>
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group mr-2" role="group">
                                    <a class="btn btn-primary" href="/work-day/edit?workDayId=${workDay.id?c}" role="button">✏️ Edit</a>
                                </div>
                                <div class="btn-group mr-2" role="group">
                                    <@action "workDayId" "${workDay.id?c}" "" ""  "btn btn-danger" "/work-day/delete" "🗑️ Delete" />
                                </div>
                            </div>
                        </td>
                    </tr>
                </#list>
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
                        <h5 class="mb-1">Period ${filters.createDateStart} - ${filters.createDateEnd} </h5>
                        <small>100%</small>
                    </div>
                    <p class="mb-1"><#if periodStatistics.duration??>${periodStatistics.duration}m (${(periodStatistics.duration/60)?floor}h ${periodStatistics.duration - ((periodStatistics.duration/60)?floor * 60)}m)</#if></p>
                </div>
                <#if periodStatistics.statistics??>
                    <#list periodStatistics.statistics?keys as key>
                        <#assign val=periodStatistics.statistics[key]/>
                        <div class="list-group-item list-group-item-action flex-column align-items-start py-2">
                            <div class="d-flex w-100 justify-content-between">
                                <h5 class="mb-1">${key}</h5>
                                <small><#if periodStatistics.duration gt 0 >${val/periodStatistics.duration*100}<#else>0</#if>%</small>
                            </div>
                            <p class="mb-1"><#if val??>${val}m (${(val/60)?floor}h ${val - ((val/60)?floor * 60)}m)</#if></p>
                        </div>
                    </#list>
                </#if>
            </div>
        </div>
    </div>
</div> <!-- /container -->
<#include "/footer.ftl">