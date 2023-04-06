package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.Measurable;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for results
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface ResultRepository extends JpaRepository<Result, UUID> {

    /**
     * Returns first 10 results by measurable ordered by point in time descending
     *
     * @param measurable
     * @return
     */
    List<Result> findFirst10ByMeasurableOrderByPointInTimeDesc(Measurable measurable);

    /**
     * Returns first result by measurable ordered by point in time descending
     *
     * @param measurable
     * @return
     */
    Result findFirstByMeasurableOrderByPointInTimeDesc(Measurable measurable);
}
