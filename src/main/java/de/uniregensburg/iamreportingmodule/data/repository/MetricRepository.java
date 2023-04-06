package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.Audience;
import de.uniregensburg.iamreportingmodule.data.entity.InformationNeed;
import de.uniregensburg.iamreportingmodule.data.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Repository for metrics
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface MetricRepository extends JpaRepository<Metric, UUID> {

    /**
     * Returns all metrics by information needs and audiences
     *
     * @param informationNeeds
     * @param audiences
     * @return
     */
    List<Metric> findAllByInformationNeedsInAndAudiencesIn(Set<InformationNeed> informationNeeds, Set<Audience> audiences);

    /**
     * Returns all metrics by label
     *
     * @param label
     * @return
     */
    List<Metric> findAllByLabel(String label);
}
