package org.emonocot.job.oaipmh;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.emonocot.api.ReferenceService;
import org.emonocot.api.TaxonService;
import org.emonocot.harvest.common.AuthorityAware;
import org.emonocot.harvest.common.TaxonRelationship;
import org.emonocot.harvest.common.TaxonRelationshipResolver;
import org.emonocot.model.Annotation;
import org.emonocot.model.Base;
import org.emonocot.model.Reference;
import org.emonocot.model.Taxon;
import org.emonocot.model.constants.AnnotationCode;
import org.emonocot.model.constants.AnnotationType;
import org.emonocot.model.constants.RecordType;
import org.emonocot.model.constants.ReferenceType;
import org.emonocot.model.geography.Location;
import org.gbif.ecat.voc.Rank;
import org.gbif.ecat.voc.TaxonomicStatus;
import org.openarchives.pmh.Record;
import org.openarchives.pmh.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.tdwg.voc.DefinedTermLinkType;
import org.tdwg.voc.Distribution;
import org.tdwg.voc.InfoItem;
import org.tdwg.voc.PublicationCitation;
import org.tdwg.voc.Relationship;
import org.tdwg.voc.RelationshipCategory;
import org.tdwg.voc.SpeciesProfileModel;
import org.tdwg.voc.TaxonConcept;
import org.tdwg.voc.TaxonName;
import org.tdwg.voc.TaxonRelationshipTerm;

/**
 *
 * @author ben
 *
 */
public class ProcessorImpl extends AuthorityAware implements
        Processor {

   /**
    *
    */
    private Logger logger
        = LoggerFactory.getLogger(ProcessorImpl.class);
    /**
     *
     */
    private ConversionService conversionService;

    /**
     *
     */
    private TaxonService taxonService;

    /**
     *
     */
    private ReferenceService referenceService;

    /**
     *
     */
    private Map<String, Reference> referencesWithinChunk
        = new HashMap<String, Reference>();

    /**
     *
     */
    private TaxonRelationshipResolver taxonRelationshipResolver;

    /**
     * @param resolver Set the taxon relationship resolver
     */
    @Autowired
    public final void setTaxonRelationshipResolver(
            final TaxonRelationshipResolver resolver) {
        this.taxonRelationshipResolver = resolver;
    }

    /**
     *
     * @param service Set the taxon service
     */
    @Autowired
    public final void setTaxonService(final TaxonService service) {
        this.taxonService = service;
    }

    /**
     *
     * @param service Set the reference service
     */
    @Autowired
    public final void setReferenceService(
            final ReferenceService service) {
        this.referenceService = service;
    }

    /**
    *
    * @param service Set the conversion service
    */
    @Autowired
    public final void setConversionService(
            final ConversionService service) {
        this.conversionService = service;
    }

    /**
     * @param record an OAI-PMH Record object
     * @return a taxon object
     * @throws Exception if there is a problem processing this record
     */
    public final Taxon process(final Record record) throws Exception {
        String identifier = record.getHeader().getIdentifier().toString();

        Taxon taxon = taxonService.find(identifier, "taxon-with-related");

        if (taxon == null) {
            // We don't have a record of this taxon yet
            if (record.getHeader().getStatus() != null
                    && record.getHeader().getStatus().equals(Status.deleted)) {
                // It was created and then deleted in between harvesting - so
                // ignore.
                return null;
            } else {
                // Create a new taxon
                taxon = new Taxon();
                TaxonConcept taxonConcept = record.getMetadata()
                        .getTaxonConcept();                
                taxon.setAuthority(getSource());
                taxon.setIdentifier(taxonConcept.getIdentifier().toString());
                taxon.getAnnotations().add(createAnnotation(taxon, RecordType.Taxon, AnnotationCode.Create, AnnotationType.Info));
                processTaxon(taxon, taxonConcept);
            }
        } else {
        	// We do have a record of this taxon already persisted
        	if(!taxon.getAuthority().getIdentifier().equals(this.getSource().getIdentifier())) {
        		// Skip taxa which do not belong to this authority
        		return null;
        	} else if (record.getHeader().getStatus() != null
                    && record.getHeader().getStatus().equals(Status.deleted)) {
                // We have a record of it and now we need to delete it
                taxon.setDeleted(true);
            } else {
                TaxonConcept taxonConcept = record.getMetadata().getTaxonConcept();

                taxon.getAnnotations().add(
                        createAnnotation(taxon, RecordType.Taxon,
                                AnnotationCode.Update, AnnotationType.Info));                

                taxon.setAuthority(getSource());
                // Allow the relationships to either be re-asserted or dropped
                taxon.setAcceptedNameUsage(null);
                taxon.setParentNameUsage(null);
                processTaxon(taxon, taxonConcept);
            }
        }


        return taxon;
    }

    /**
     *
     * @param taxon Set the taxon object
     * @param taxonConcept Set the taxonConcept object
     */
    public final void processTaxon(
            final Taxon taxon, final TaxonConcept taxonConcept) {
        taxonRelationshipResolver.bind(taxon);
        if (taxonConcept.getHasName() != null) {
            TaxonName taxonName = taxonConcept.getHasName();
            logger.info(taxonName.getNameComplete());
            taxon.setScientificName(taxonName.getNameComplete());
            taxon.setScientificNameAuthorship(taxonName.getAuthorship());
            taxon.setFamily(taxonName.getFamily());
            taxon.setGenus(taxonName.getGenusPart());
            taxon.setSpecificEpithet(taxonName.getSpecificEpithet());
            taxon.setInfraspecificEpithet(taxonName.getInfraSpecificEpithet());
            if (taxonName.getPublishedInCitations() != null
                    && !taxonName.getPublishedInCitations().isEmpty()) {
                PublicationCitation protologuePublicationCitation = taxonName
                        .getPublishedInCitations().iterator().next();
                Reference protologue = processPublicationCitation(protologuePublicationCitation);
                // concat(Reference.title,IFNULL(Reference.volume,''),IFNULL(Taxon.protologueMicroReference,''),IFNULL(Reference.datePublished,''))
                StringBuffer namePublishedInString = new StringBuffer();
                namePublishedInString.append(protologuePublicationCitation.getTpubTitle());
                if(protologuePublicationCitation.getVolume() != null) {
                	namePublishedInString.append(" " + protologuePublicationCitation.getVolume());
                }
                if(taxonName.getMicroReference() != null) {
                	namePublishedInString.append(" " + taxonName.getMicroReference());
                }
                if(protologuePublicationCitation.getDatePublished() != null) {
                	namePublishedInString.append(" " + protologuePublicationCitation.getDatePublished());
                }
                taxon.setNamePublishedInString(namePublishedInString.toString());
                taxon.setNamePublishedIn(protologue);

            }
            try {
                taxon.setTaxonRank(conversionService.convert(
                        taxonName.getRankString(), Rank.class));
            } catch (ConversionException ce) {
                taxon.getAnnotations().add(
                        addAnnotation(taxon, RecordType.Taxon, "rank", ce));
            }
        } else {
            taxon.setScientificName(taxonConcept.getTitle());
        }
        if (taxonConcept.getStatus() != null) {
            try {
                taxon.setTaxonomicStatus(conversionService.convert(taxonConcept
                        .getStatus().getIdentifier().toString(),
                        TaxonomicStatus.class));
            } catch (ConversionException ce) {
                taxon.getAnnotations().add(
                    addAnnotation(taxon, RecordType.Taxon, "status", ce));
            }
        }
        if (taxonConcept.getHasRelationship() != null) {
            for (Relationship relationship
                    : taxonConcept.getHasRelationship()) {
                addRelationship(taxon, relationship);
            }
        }
        
        // Clear Distributions
        taxon.getDistribution().clear();
        
        if (taxonConcept.getDescribedBy() != null) {
            for (SpeciesProfileModel spm : taxonConcept.getDescribedBy()) {
                if (spm.getHasInformation() != null) {
                    logger.info("hasInformation " + spm.getHasInformation());
                    for (InfoItem infoItem : spm.getHasInformation()) {
                        logger.info("hasInformation " + infoItem);
                        if (infoItem instanceof Distribution) {
                            logger.info("hasInformation = Distribution");
                            Distribution distribution = (Distribution) infoItem;                            
                            org.emonocot.model.Distribution dist
                                = resolveDistribution(distribution
                                    .getHasValueRelation(), taxon);
                            if (dist.getLocation() != null) {
                                dist.setTaxon(taxon);
                                taxon.getDistribution().add(dist);
                            }
                        }
                    }
                }
            }
        }

        if (taxonConcept.getPublishedInCitations() != null) {
            for (PublicationCitation publication : taxonConcept
                    .getPublishedInCitations()) {
                Reference reference  = processPublicationCitation(publication);
                taxon.getReferences().add(reference);
                reference.getTaxa().add(taxon);
            }
        }
    }

    /**
     *
     * @param publicationCitation Set the publication citation
     * @return a reference
     */
    private Reference processPublicationCitation(
            final PublicationCitation publicationCitation) {
    	if(publicationCitation == null || publicationCitation.getIdentifier() == null) {
    		return null;
    	}
        String referenceIdentifier = publicationCitation.getIdentifier()
                .toString();
        Reference reference = null;
        if (referencesWithinChunk.containsKey(referenceIdentifier)) {
            reference = referencesWithinChunk.get(referenceIdentifier);
        } else {
            reference = referenceService.find(referenceIdentifier, "reference-with-taxa");
            referencesWithinChunk.put(referenceIdentifier, reference);
        }
        if (reference == null) {
            // We've not seen this before
            reference = new Reference();
            reference.setIdentifier(referenceIdentifier);
            reference.setAuthority(getSource());
            reference.getAnnotations().add(
                    createAnnotation(reference, RecordType.Reference,
                            AnnotationCode.Create, AnnotationType.Info));
            referencesWithinChunk.put(referenceIdentifier, reference);
        }
        // TODO Created / modified dates on publications? Bridge too far?
        if(publicationCitation.getTpubTitle() != null
                && publicationCitation.getTpubTitle().length() > 0) {
            reference.setTitle(publicationCitation.getTpubTitle());
        }
        
        if(publicationCitation.getAuthorship() != null
                && publicationCitation.getAuthorship().length() > 0) {
            reference.setCreator(publicationCitation.getAuthorship());
        }        
        
        reference.setDate(publicationCitation.getDatePublished());
        reference.setCreator(publicationCitation.getAuthorship());
        String publishedInAuthor = null;
        String publisher = null;
        if (publicationCitation.getParentPublication() != null) {
        	if (reference.getCreator() == null) {
        		reference.setCreator(publicationCitation.getParentPublication().getAuthorship());
            } else {
            	publishedInAuthor = publicationCitation.getParentPublication().getAuthorship();
            }
        	
            if (reference.getTitle() == null) {
            	reference.setTitle(publicationCitation.getParentPublication().getTpubTitle());
            } else {
                reference.setSource(publicationCitation.getParentPublication().getTpubTitle());
            }
            
            if (publicationCitation.getParentPublication().getPublisher() == null) {
            	publisher = publicationCitation.getParentPublication().getPublisher();
            }
        }
        
        StringBuffer bibliographicCitation = new StringBuffer();
        bibliographicCitation.append(reference.getCreator());        	
        if(publicationCitation.getDatePublished() != null) {
        	bibliographicCitation.append(" (" + publicationCitation.getDatePublished() + ")");
        }
        bibliographicCitation.append(". " + reference.getTitle());
        if(publishedInAuthor != null) {
        	bibliographicCitation.append(" " + publishedInAuthor);
        }
        if(reference.getSource() != null) {
        	bibliographicCitation.append(" " + reference.getSource());
        }
        
        if(publicationCitation.getVolume() != null) {
        	bibliographicCitation.append(" " + publicationCitation.getVolume());
        }
        if(publicationCitation.getPages() != null) {
        	bibliographicCitation.append(": " + publicationCitation.getPages());
        }
        bibliographicCitation.append(". ");        
        
        if (publisher  != null) {
        	bibliographicCitation.append(publisher + ".");
        }
        
        reference.setBibliographicCitation(bibliographicCitation.toString());
        if (publicationCitation.getPublicationType() != null
                && publicationCitation.getPublicationType().getIdentifier() != null) {
            try {
                reference.setType(conversionService.convert(publicationCitation
                        .getPublicationType().getIdentifier().toString(),
                        ReferenceType.class));
            } catch (ConversionException ce) {
                reference.getAnnotations().add(addAnnotation(reference, RecordType.Reference, "type",ce));
            }
        }
        return reference;
    }

    /**
     *
     * @param object Set the type of object
     * @param property Set the property
     * @param recordType Set the record type
     * @param exception Set the exception
     * @return an annotation
     */
    private Annotation addAnnotation(final Base object,
            final RecordType recordType, final String property,
            final ConversionException exception) {
        Annotation a = createAnnotation(object, recordType,
                AnnotationCode.BadField, AnnotationType.Warn);
        a.setValue(property);
        a.setText(exception.getMessage());
        return a;
    }

   /**
    *
    */
   public final void beforeChunk() {
        this.referencesWithinChunk = new HashMap<String, Reference>();
   }

   /**
    *
    */
   public final void afterChunk() {

   }

    /**
     * Adds a relationship to a list of relationships which should be resolved
     * once the taxa in this chunk have been processed. Allows for forward
     * references within the chunk to prevent duplicate entries if a taxon is
     * proceeded by a related taxon within a chunk.
     *
     * @param taxon Set the from taxon.
     * @param relationship Set the relationship object.
     */
    private void addRelationship(final Taxon taxon,
            final Relationship relationship) {
        String identifier = null;
        if (relationship.getToTaxonRelation() != null
                && relationship.getToTaxonRelation().getTaxonConcept() != null
                && relationship.getToTaxonRelation().getTaxonConcept()
                        .getIdentifier() != null) {
            identifier = relationship.getToTaxonRelation().getTaxonConcept()
                    .getIdentifier().toString();
        } else if (relationship.getToTaxonRelation() != null
                && relationship.getToTaxonRelation().getResource() != null) {
            identifier = relationship.getToTaxonRelation().getResource()
                    .toString();
        }

		if (identifier != null) {
			TaxonRelationshipTerm term = resolveRelationshipTerm(relationship.getRelationshipCategoryRelation());
			if (term.equals(TaxonRelationshipTerm.IS_CHILD_TAXON_OF) || term.equals(TaxonRelationshipTerm.IS_SYNONYM_FOR)) {
				TaxonRelationship taxonRelationship = new TaxonRelationship(
						taxon, term);
				taxonRelationship.setToIdentifier(identifier);
				taxonRelationshipResolver.addTaxonRelationship(taxonRelationship, identifier);
			}
		} else {
			Annotation annotation = createAnnotation(taxon, RecordType.Taxon,AnnotationCode.BadField, AnnotationType.Warn);
			annotation.setText("Could not find identifier for relationship of taxon " + taxon.getIdentifier());
			annotation.setValue("related");
			taxon.getAnnotations().add(annotation);
		}
    }

    /**
     *
     * @param hasValueRelation a has value relation
     * @param taxon Set the taxon
     * @return a valid Distribution
     */
    private org.emonocot.model.Distribution resolveDistribution(
            final Set<DefinedTermLinkType> hasValueRelation, final Taxon taxon) {
        // TODO - what if there are no terms or multiple terms - throw an error?
        Location region = null;
        if (hasValueRelation == null || hasValueRelation.isEmpty()) {
            Annotation annotation = createAnnotation(taxon, RecordType.Taxon,
                    AnnotationCode.BadField, AnnotationType.Warn);
            annotation.setValue("distribution");
            annotation.setText("No geographical term returned"
                    + taxon.getIdentifier());
            taxon.getAnnotations().add(annotation);
            return null;
        }
        DefinedTermLinkType definedTermLinkType = hasValueRelation.iterator()
                .next();
        String id = null;
        if (definedTermLinkType.getDefinedTerm() != null) {
            id = definedTermLinkType
                    .getDefinedTerm().getIdentifier().toString();
        } else if (definedTermLinkType.getResource() != null) {
            id = definedTermLinkType
                    .getResource().toString();
        }
        org.emonocot.model.Distribution distribution
           = new org.emonocot.model.Distribution();
        distribution.setAuthority(getSource());
        distribution.setIdentifier(UUID.randomUUID().toString());
        try {
        	region = conversionService.convert(id, Location.class);
            distribution.setLocation(region);
        } catch (ConversionException ce) {
            distribution.getAnnotations().add(
                    addAnnotation(distribution, RecordType.Distribution,
                            "type", ce));
        }
        logger.info("Resolving " + definedTermLinkType
                + " returning " + region);
        return distribution;
    }

    /**
     *
     * @param relationshipCategory the relationship category
     * @return a taxon relationship term
     */
    private TaxonRelationshipTerm resolveRelationshipTerm(
            final RelationshipCategory relationshipCategory) {
        if (relationshipCategory.getTaxonRelationshipTerm() != null) {
            return relationshipCategory.getTaxonRelationshipTerm();
        } else {
            return TaxonRelationshipTerm.fromValue(relationshipCategory
                    .getResource().toString());
        }
    }
}
