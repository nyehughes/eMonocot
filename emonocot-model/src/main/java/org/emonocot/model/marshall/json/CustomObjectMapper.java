package org.emonocot.model.marshall.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.emonocot.api.ConceptService;
import org.emonocot.api.GroupService;
import org.emonocot.api.IdentificationKeyService;
import org.emonocot.api.ImageService;
import org.emonocot.api.JobInstanceService;
import org.emonocot.api.PhylogeneticTreeService;
import org.emonocot.api.ReferenceService;
import org.emonocot.api.OrganisationService;
import org.emonocot.api.TaxonService;
import org.emonocot.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.module.hibernate.HibernateModule;

/**
*
*/
public class CustomObjectMapper extends ObjectMapper {
	
	Logger logger = LoggerFactory.getLogger(CustomObjectMapper.class);

    private ReferenceService referenceService;

    private TaxonService taxonService;

    private ImageService imageService;

    private UserService userService;

    private GroupService groupService;

    private OrganisationService organisationService;

    private JobInstanceService jobInstanceService;

    private IdentificationKeyService identificationKeyService;
    
    private PhylogeneticTreeService phylogeneticTreeService;
    
    private ConceptService conceptService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }


    public void setIdentificationKeyService(IdentificationKeyService identificationKeyService) {
        this.identificationKeyService = identificationKeyService;
    }

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    public void setTaxonService(TaxonService taxonService) {
        this.taxonService = taxonService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public void setJobInstanceService(JobInstanceService jobInstanceService) {
        this.jobInstanceService = jobInstanceService;
    }

    public void setOrganisationService(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    public void setPhylogeneticTreeService(PhylogeneticTreeService phylogeneticTreeService) {
		this.phylogeneticTreeService = phylogeneticTreeService;
	}
    
    public void setConceptService(ConceptService conceptService) {
    	this.conceptService = conceptService;
    }

	/**
    *
    */
    protected CustomObjectMapper() {
    }

    /**
    *
    */
    protected void init() {
    	logger.debug("init() called"); 
        CustomHandlerInstantiator handlerInstantiator = new CustomHandlerInstantiator();
        handlerInstantiator.setReferenceService(referenceService);
        handlerInstantiator.setTaxonService(taxonService);
        handlerInstantiator.setImageService(imageService);
        handlerInstantiator.setGroupService(groupService);
        handlerInstantiator.setUserService(userService);
        handlerInstantiator.setOrganisationService(organisationService);
        handlerInstantiator.setIdentificationKeyService(identificationKeyService);
        handlerInstantiator.setPhylogeneticTreeService(phylogeneticTreeService);
        handlerInstantiator.setConceptService(conceptService);
        setHandlerInstantiator(handlerInstantiator);
        CustomModule module = new CustomModule(jobInstanceService);
        registerModule(module);
        registerModule(new HibernateModule());
    }
}
