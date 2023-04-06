package de.uniregensburg.iamreportingmodule.core.service;

import de.uniregensburg.iamreportingmodule.core.exception.DeleteEntityException;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.data.entity.Audience;
import de.uniregensburg.iamreportingmodule.data.entity.Group;
import de.uniregensburg.iamreportingmodule.data.entity.Stakeholder;
import de.uniregensburg.iamreportingmodule.data.entity.User;
import de.uniregensburg.iamreportingmodule.data.repository.AudienceRepository;
import de.uniregensburg.iamreportingmodule.data.repository.GroupRepository;
import de.uniregensburg.iamreportingmodule.data.repository.StakeholderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service for persisting groups (audiences and stakeholders)
 *
 * @author Julian Bauer
 */
@Service
public class GroupService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AudienceRepository audienceRepository;
    private final StakeholderRepository stakeholderRepository;
    private final GroupRepository groupRepository;

    /**
     *
     * @param audienceRepository
     * @param stakeholderRepository
     * @param groupRepository
     */
    public GroupService(AudienceRepository audienceRepository, StakeholderRepository stakeholderRepository, GroupRepository groupRepository) {
        this.audienceRepository = audienceRepository;
        this.stakeholderRepository = stakeholderRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Returns all audiences
     *
     * @return
     */
    public List<Audience> findAllAudiences() {
        logger.info("Returning all audiences");
        // get and return all
        return audienceRepository.findAll();
    }

    /**
     * Returns all stakeholders
     *
     * @return
     */
    public List<Stakeholder> findAllStakeholders() {
        logger.info("Returning all stakeholders");
        // return all
        return stakeholderRepository.findAll();
    }

    /**
     * Saves a group
     *
     * @param group
     * @throws SaveEntityException
     */
    public void saveGroup(Group group) throws SaveEntityException {
        logger.info("Saving group");
        // check group
        if (group == null) {
            logger.info("Group is null");
            throw new SaveEntityException("Group is null");
        }
        // save group
        try {
            groupRepository.save(group);
            logger.info("Group saved");
        } catch (Exception e) {
            logger.info("Error while saving group: " + e.getMessage());
            throw new SaveEntityException("Error while saving group");
        }
    }

    /**
     * Deletes a group
     *
     * @param group
     * @throws DeleteEntityException
     */
    public void deleteGroup(Group group) throws DeleteEntityException {
        logger.info("Deleting group");
        // check group
        if (group == null) {
            logger.info("Group is null");
            return;
        }
        // delete group
        try {
            groupRepository.delete(group);
            logger.info("Group deleted");
        } catch (Exception ex) {
            logger.info("Cannot delete group: " + ex.getMessage());
            throw new DeleteEntityException("Group is probably used in one or more measurables");
        }
    }

    /**
     * Returns a group by id
     *
     * @param id
     * @return
     */
    public Group findGroupById(UUID id) {
        logger.info("Searching group with id " + id);
        // search group by id
        Optional<Group> group = groupRepository.findById(id);

        // check group
        if (group.isEmpty()) {
            logger.info("Group not found");
            return null;
        }

        logger.info("Group found");
        // return group
        return group.get();
    }

    /**
     * Returns an audience by id
     *
     * @param id
     * @return
     */
    public Audience findAudienceById(UUID id) {
        logger.info("Searching audience with id " + id);
        // search group
        Group group = findGroupById(id);
        if (group == null) {
            logger.info("Group is null");
            return null;
        }
        // check if group is an audience
        if (group.getClass().equals(Audience.class)) {
            // return audience
            return (Audience) group;
        } else {
            logger.info("Group is not an audience");
            return null;
        }
    }

    /**
     * Returns a stakeholder by id
     *
     * @param id
     * @return
     */
    public Stakeholder findStakeholderById(UUID id) {
        logger.info("Searching stakeholder with id " + id);
        // search group
        Group group = findGroupById(id);
        if (group == null) {
            logger.info("Group is null");
            return null;
        }
        // check if group is a stakeholder
        if (group.getClass().equals(Stakeholder.class)) {
            // return stakeholder
            return (Stakeholder) group;
        } else {
            logger.info("Group is not a stakeholder");
            return null;
        }
    }

    /**
     * Returns all audiences containing specific member
     *
     * @param member
     * @return
     */
    public List<Audience> findAudiencesByMember(User member) {
        logger.info("Searching audiences with member " + member.getUsername());
        // search and return all audiences containing specific member
        return audienceRepository.findAllByMembersIn(Set.of(member));
    }
}
