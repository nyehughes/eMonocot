package org.emonocot.job.dwc.reference;

import org.emonocot.api.ReferenceService;
import org.emonocot.job.dwc.NonOwnedProcessor;
import org.emonocot.job.dwc.RequiredFieldException;
import org.emonocot.model.Reference;
import org.emonocot.model.constants.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ben
 *
 */
public class Processor extends NonOwnedProcessor<Reference, ReferenceService> {

    private Logger logger = LoggerFactory.getLogger(Processor.class);
    
    @Autowired
    public void setReferenceService(ReferenceService service) {
    	super.service = service;
    }

	@Override
	protected void doUpdate(Reference persisted, Reference t) {
		persisted.setBibliographicCitation(t.getBibliographicCitation());
		persisted.setCreator(t.getCreator());        
        persisted.setDate(t.getDate());
        persisted.setDescription(t.getDescription());
        persisted.setLanguage(t.getLanguage());
        persisted.setSource(t.getSource());
        persisted.setSubject(t.getSubject());
        persisted.setTaxonRemarks(t.getTaxonRemarks());
        persisted.setTitle(t.getTitle());
        persisted.setType(t.getType());
	}

	@Override
	protected void doPersist(Reference t) {
		
	}

	@Override
	protected RecordType getRecordType() {
		return RecordType.Reference;
	}

	@Override
	protected void bind(Reference t) {
		if (t.getIdentifier() != null) {
            boundObjects.put(t.getIdentifier(), t);
        }
        if (t.getBibliographicCitation() != null) {
            boundObjects.put(t.getBibliographicCitation(), t);
        }
	}

	@Override
	protected Reference retrieveBound(Reference t) {
		if (t.getIdentifier() != null) {
            return service.find(t.getIdentifier());
        } else if (t.getBibliographicCitation() != null) {
            return service.findByBibliographicCitation(t.getBibliographicCitation());
        }
        return null;
	}

	@Override
	protected Reference lookupBound(Reference t) {
		if (t.getIdentifier() != null) {
            return boundObjects.get(t.getIdentifier());
        } else if (t.getBibliographicCitation() != null) {
            return boundObjects.get(t.getBibliographicCitation());
        }
        return null;
	}

	@Override
	protected void doValidate(Reference t) throws Exception {
		if (t.getBibliographicCitation() == null) {
            throw new RequiredFieldException(t + " has no bibliographicCitation set", RecordType.Reference, getStepExecution().getReadCount());
        }
	}
}