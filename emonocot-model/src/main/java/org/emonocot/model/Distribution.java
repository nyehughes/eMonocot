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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.emonocot.model.geography.GeographicalRegion;
import org.emonocot.model.hibernate.DistributionBridge;
import org.emonocot.model.marshall.json.GeographicalRegionDeserializer;
import org.emonocot.model.marshall.json.ReferenceDeserializer;
import org.emonocot.model.marshall.json.ReferenceSerializer;
import org.gbif.ecat.voc.EstablishmentMeans;
import org.gbif.ecat.voc.OccurrenceStatus;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

/**
 *
 * @author ben
 *
 */
@Entity
@Indexed
@ClassBridge(name = "geographicalRegion", impl = DistributionBridge.class, index = Index.UN_TOKENIZED)
public class Distribution extends OwnedEntity {

    /**
     *
     */
    private static final long serialVersionUID = -970244833684895241L;

    /**
    *
    */
    private Taxon taxon;

    /**
     *
     */
    private GeographicalRegion location;
    
    /**
     *
     */
    private String locality;
    
    /**
     *
     */
    private String occurrenceRemarks;
    
    /**
     *
     */
    private OccurrenceStatus occurrenceStatus;
    
    /**
     *
     */
    private EstablishmentMeans establishmentMeans;
    

    /**
    *
    */
    private Set<Annotation> annotations = new HashSet<Annotation>();
    
    /**
    *
    */
   private Set<Reference> references = new HashSet<Reference>();

    /**
   *
   */
    private Long id;

    /**
     *
     * @param newId
     *            Set the identifier of this object.
     */
    public void setId(Long newId) {
        this.id = newId;
    }

    /**
     *
     * @return Get the identifier for this object.
     */
    @Id
    @GeneratedValue(generator = "system-increment")
    @DocumentId
    public Long getId() {
        return id;
    }

    /**
     * Set the lowest level this georegion is concerned with.
     *
     * @param geoRegion
     *            the geographical region this distribution is concerned with
     */
    @JsonDeserialize(using = GeographicalRegionDeserializer.class)
    public void setLocation(GeographicalRegion geoRegion) {
        this.location = geoRegion;
    }

    /**
     *
     * @return the lowest level geo region this distribution is concerned with
     */
    @Type(type = "tdwgRegionUserType")
    public GeographicalRegion getLocation() {
        return location;
    }

    /**
     *
     * @param newTaxon
     *            Set the taxon that this distribution is about.
     */
    @JsonBackReference("distribution-taxon")
    public void setTaxon(Taxon newTaxon) {
        this.taxon = newTaxon;
    }

    /**
     * @return the annotations
     */
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "annotatedObjId")
    @Where(clause = "annotatedObjType = 'Distribution'")
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

    /**
     *
     * @return Get the taxon that this distribution is about.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @ContainedIn
    @JsonBackReference("distribution-taxon")
    public Taxon getTaxon() {
        return taxon;
    }

    
    
    /**
	 * @return the locality
	 */
	public String getLocality() {
		return locality;
	}

	/**
	 * @param locality the locality to set
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}

	/**
	 * @return the occurrenceRemarks
	 */
	public String getOccurrenceRemarks() {
		return occurrenceRemarks;
	}

	/**
	 * @param occurrenceRemarks the occurrenceRemarks to set
	 */
	public void setOccurrenceRemarks(String occurrenceRemarks) {
		this.occurrenceRemarks = occurrenceRemarks;
	}

	/**
	 * @return the occurrenceStatus
	 */
	@Enumerated(value = EnumType.STRING)
	public OccurrenceStatus getOccurrenceStatus() {
		return occurrenceStatus;
	}

	/**
	 * @param occurrenceStatus the occurrenceStatus to set
	 */
	public void setOccurrenceStatus(OccurrenceStatus occurrenceStatus) {
		this.occurrenceStatus = occurrenceStatus;
	}

	/**
	 * @return the establishmentMeans
	 */
	@Enumerated(value = EnumType.STRING)
	public EstablishmentMeans getEstablishmentMeans() {
		return establishmentMeans;
	}

	/**
	 * @param establishmentMeans the establishmentMeans to set
	 */
	public void setEstablishmentMeans(EstablishmentMeans establishmentMeans) {
		this.establishmentMeans = establishmentMeans;
	}

	/**
	 * @return the references
	 */
	@ManyToMany(fetch = FetchType.LAZY)
    @Cascade({ CascadeType.SAVE_UPDATE })
    @JoinTable(name = "Distribution_Reference", joinColumns = { @JoinColumn(name = "Distribution_id") }, inverseJoinColumns = { @JoinColumn(name = "references_id") })
    @JsonSerialize(contentUsing = ReferenceSerializer.class)
	public Set<Reference> getReferences() {
		return references;
	}

	/**
	 * @param references the references to set
	 */
	@JsonDeserialize(contentUsing = ReferenceDeserializer.class)
	public void setReferences(Set<Reference> references) {
		this.references = references;
	}

	@Transient
    @JsonIgnore
    public final String getClassName() {
        return "Distribution";
    }
}
