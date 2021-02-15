<#-- @ftlvariable name="templates" type="pko.unity.time.tracker.ui.work.day.dto.WorkDayDto[]" -->
<#-- @ftlvariable name="template" type="pko.unity.time.tracker.ui.work.day.dto.WorkDayDto" -->

<#include "/header.ftl">

<@header "Work days"/>

<div class="container" >
    <h1>Work days</h1>
    <nav class="navbar navbar-light bg-light justify-content-between" style="background-color: #e3f2fd;">
        <a class="navbar-brand">Navigation</a>
        <a class="btn btn-outline-success" href="/jira/credentials/edit" role="button">Jira credentials</a>
        <a class="btn btn-outline-secondary" href="/h2-console" role="button">H2 database console</a>
    </nav>
    <div class="card">
        <div class="card-body">
            <form class="form-inline" action="/work-day/add" method="post">
                <div class="form-group mb-2">
                    <label for="date" class="sr-only">Date</label>
                    <input type="date" class="form-control" id="date" name="date" placeholder="dd.MM.yyyy" required="required"/>
                </div>

                <button type="submit" class="btn btn-primary mb-2">New work day</button>
            </form>
            <table class="table table-striped">
                <thead class="thead-dark">
                <tr>
                    <th style="width: 5%">#</th>
                    <th style="width: 35%">Day</th>
                    <th style="width: 35%">State</th>
                    <th style="width: 25%">Actions</th>
                </tr>
                </thead>
                <tbody>
                <#list workDays as workDay>
                    <tr>
                        <td>${workDay_index +1}</td>
                        <td>${workDay.date?html}</td>
                        <td>${workDay.state?html}</td>
                        <td>
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group mr-2" role="group">
                                    <a class="btn btn-primary" href="/work-day/edit?workDayId=${workDay.id?c}" role="button">Edit</a>
                                </div>
                                <div class="btn-group mr-2" role="group">
                                    <@action "workDayId" "${workDay.id?c}" "" ""  "btn btn-danger" "/work-day/delete" "Delete" />
                                </div>
                            </div>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
</div> <!-- /container -->
<#include "/footer.ftl">