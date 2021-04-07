package pko.delorean.time.tracker.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pko.delorean.time.tracker.domain.WorkDay;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkDayJpaRepository extends JpaRepository<WorkDay, Long> {

    List<WorkDay> findAllByCreateDateBetween(
            LocalDate createDateStart,
            LocalDate createDateEnd);

    @Query("select a from WorkDay a where a.createDate < :creationDate order by a.createDate DESC ")
    List<WorkDay> findAllWithCreateDateBefore(Pageable pageable, @Param("creationDate") LocalDate creationDate);
}
