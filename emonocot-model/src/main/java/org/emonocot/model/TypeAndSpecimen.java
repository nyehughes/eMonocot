package org.emonocot.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.emonocot.model.constants.TypeDesignationType;
import org.emonocot.model.marshall.json.TaxonDeserializer;
import org.emonocot.model.marshall.json.TaxonSerializer;
import org.gbif.ecat.voc.Rank;
import org.gbif.ecat.voc.Sex;
import org.gbif.ecat.voc.TypeStatus;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

/**
 *
 * @author ben
 *
 */
@Entity
@Indexed
public class TypeAndSpecimen extends BaseData implements NonOwned {
	
   /**
	 * 
	 */
	private static final long serialVersionUID = -843014945343629009L;

/**
    *
    */
    private Long id;
    
    /**
     *
     */
    private TypeStatus typeStatus;
    
    /**
     *
     */
    private TypeDesignationType typeDesignationType;
    
    /**
     *
     */
    private String typeDesignatedBy;
    
    /**
     *
     */
    private String scientificName;
    
    /**
     *
     */
    private Rank taxonRank;
    
    /**
     *
     */
    private String bibliographicCitation;
    
    /**
     *
     */
    private String institutionCode;
    
    /**
     *
     */
    private String collectionCode;
    
    /**
     *
     */
    private String catalogNumber;
    
    /**
     *
     */
    private String locality;
    
    /**
     *
     */
    private Sex sex;
    
    /**
     *
     */
    private String recordedBy;
    
    /**
     *
     */
    private String source;
   
    /**
     *
     */
    private String verbatimEventDate;
    
    /**
     *
     */
    private String verbatimLabel;
    
    /**
     *
     */
    private String verbatimLongitude;
    
    /**
     *
     */
    private String verbatimLattitude;
    
    /**
     *
     * @return
     */
    private Set<Taxon> taxa = new HashSet<Taxon>();
    
    private Set<Annotation> annotations = new HashSet<Annotation>();
   

	@Override
	@Id
    @GeneratedValue(generator = "system-increment")
    @DocumentId
	public Long getId() {
		return id;
	}
	
	/**
	 *
	 * @param id Set the id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	@Enumerated(value = EnumType.STRING)
	public TypeStatus getTypeStatus() {
		return typeStatus;
	}

	public void setTypeStatus(TypeStatus typeStatus) {
		this.typeStatus = typeStatus;
	}

	@Enumerated(value = EnumType.STRING)
	public TypeDesignationType getTypeDesignationType() {
		return typeDesignationType;
	}

	public void setTypeDesignationType(TypeDesignationType typeDesignationType) {
		this.typeDesignationType = typeDesignationType;
	}

	public String getTypeDesignatedBy() {
		return typeDesignatedBy;
	}

	public void setTypeDesignatedBy(String typeDesignatedBy) {
		this.typeDesignatedBy = typeDesignatedBy;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	@Enumerated(value = EnumType.STRING)
	public Rank getTaxonRank() {
		return taxonRank;
	}

	public void setTaxonRank(Rank taxonRank) {
		this.taxonRank = taxonRank;
	}

	public String getBibliographicCitation() {
		return bibliographicCitation;
	}

	public void setBibliographicCitation(String bibliographicCitation) {
		this.bibliographicCitation = bibliographicCitation;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public String getCollectionCode() {
		return collectionCode;
	}

	public void setCollectionCode(String collectionCode) {
		this.collectionCode = collectionCode;
	}

	public String getCatalogNumber() {
		return catalogNumber;
	}

	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	@Enumerated(value = EnumType.STRING)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getRecordedBy() {
		return recordedBy;
	}

	public void setRecordedBy(String recordedBy) {
		this.recordedBy = recordedBy;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVerbatimEventDate() {
		return verbatimEventDate;
	}

	public void setVerbatimEventDate(String verbatimEventDate) {
		this.verbatimEventDate = verbatimEventDate;
	}

	public String getVerbatimLabel() {
		return verbatimLabel;
	}

	public void setVerbatimLabel(String verbatimLabel) {
		this.verbatimLabel = verbatimLabel;
	}

	public String getVerbatimLongitude() {
		return verbatimLongitude;
	}

	public void setVerbatimLongitude(String verbatimLongitude) {
		this.verbatimLongitude = verbatimLongitude;
	}

	public String getVerbatimLattitude() {
		return verbatimLattitude;
	}

	public void setVerbatimLattitude(String verbatimLattitude) {
		this.verbatimLattitude = verbatimLattitude;
	}

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Taxon_TypeAndSpecimen", joinColumns = {@JoinColumn(name = "typesAndSpecimens_id")}, inverseJoinColumns = {@JoinColumn(name = "Taxon_id")})
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
    @JsonSerialize(contentUsing = TaxonSerializer.class)
	public Set<Taxon> getTaxa() {
		return taxa;
	}

	@JsonDeserialize(contentUsing = TaxonDeserializer.class)
	public void setTaxa(Set<Taxon> taxa) {
		this.taxa = taxa;
	}
	
	@Transient
    @JsonIgnore
    public final String getClassName() {
        return "TypeAndSpecimen";
    }
	
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "annotatedObjId")
    @Where(clause = "annotatedObjType = 'TypeAndSpecimen'")
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.DELETE })
    @JsonIgnore
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * @param annotations
     *            the annotations to set
     */
    public void setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
    }
}
