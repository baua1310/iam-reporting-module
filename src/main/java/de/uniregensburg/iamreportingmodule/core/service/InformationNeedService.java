package de.uniregensburg.iamreportingmodule.core.service;

import de.uniregensburg.iamreportingmodule.core.exception.DeleteEntityException;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.data.entity.InformationNeed;
import de.uniregensburg.iamreportingmodule.data.repository.InformationNeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for persisting information needs
 *
 * @author Julian Bauer
 */
@Service
public class InformationNeedService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final InformationNeedRepository informationNeedRepository;

    /**
     *
     * @param informationNeedRepository
     */
    public InformationNeedService(InformationNeedRepository informationNeedRepository) {
        this.informationNeedRepository = informationNeedRepository;
    }

    /**
     * Returns all information needs
     *
     * @return
     */
    public List<InformationNeed> findAllInformationNeeds() {
        logger.info("Returning all information needs");
        // get and return all
        return informationNeedRepository.findAll();
    }

    /**
     * Saves an information need
     *
     * @param informationNeed
     * @throws SaveEntityException
     */
    public void saveInformationNeed(InformationNeed informationNeed) throws SaveEntityException {
        logger.info("Saving information need");
        // check information need
        if (informationNeed == null) {
            logger.info("Information need is null");
            throw new SaveEntityException("No information need provided");
        }
        // save information need
        try {
            informationNeedRepository.save(informationNeed);
            logger.info("Information need saved");
        } catch (Exception e) {
            logger.info("Error while saving information need: " + e.getMessage());
            throw new SaveEntityException("Error while saving information need");
        }
    }

    /**
     * Deletes an information need
     *
     * @param informationNeed
     * @throws DeleteEntityException
     */
    public void deleteInformationNeed(InformationNeed informationNeed) throws DeleteEntityException {
        logger.info("Deleting information need");
        // check information need
        if (informationNeed == null) {
            logger.info("Information need is null");
            return;
        }
        // delete information need
        try {
            informationNeedRepository.delete(informationNeed);
            logger.info("Information need deleted");
        } catch (Exception ex) {
            logger.info("Cannot delete information need: " + ex.getMessage());
            throw new DeleteEntityException("Information need is probably used in one or more measurables");
        }
    }

    /**
     * Returns an information need by id
     *
     * @param id
     * @return
     */
    public InformationNeed findInformationNeedById(UUID id) {
        logger.info("Searching information need with id " + id);
        // search information need by id
        Optional<InformationNeed> informationNeed = informationNeedRepository.findById(id);

        // check information need
        if (informationNeed.isEmpty()) {
            logger.info("InformationNeed not found");
            return null;
        }

        logger.info("InformationNeed found");
        // return information need
        return informationNeed.get();
    }
}
