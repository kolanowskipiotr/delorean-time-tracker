package pko.unity.time.tracker.domain;

import org.jetbrains.annotations.Nullable;
import pko.unity.time.tracker.ui.jira.dto.JiraIssueDto;

import java.util.List;

public interface WorkDayRepository {

    List<WorkDay> findAll();

    List<WorkDay> findAllById(Iterable<Long> ids);

    WorkDay saveAndFlush(WorkDay entity);

    void deleteInBatch(Iterable<WorkDay> entities);

    WorkDay getOne(Long id);
}
