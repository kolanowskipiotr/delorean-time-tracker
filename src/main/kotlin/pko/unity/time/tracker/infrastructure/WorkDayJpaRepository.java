package pko.unity.time.tracker.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pko.unity.time.tracker.domain.WorkDay;

@Repository
public interface WorkDayJpaRepository extends JpaRepository<WorkDay, Long> {

}
