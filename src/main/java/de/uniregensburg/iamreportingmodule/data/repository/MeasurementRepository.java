package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for measurements
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    /**
     * Returns all measurements by label
     *
     * @param label
     * @return
     */
    List<Measurement> findAllByLabel(String label);
}
