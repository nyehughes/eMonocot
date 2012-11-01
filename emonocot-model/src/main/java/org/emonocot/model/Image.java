package org.emonocot.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.emonocot.model.constants.ImageFormat;
import org.emonocot.model.hibernate.TaxonomyBridge;
import org.emonocot.model.marshall.json.TaxonDeserializer;
import org.emonocot.model.marshall.json.TaxonSerializer;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.vividsolutions.jts.geom.Point;

/**
 *
 * @author ben
 *
 */
@Entity
@Indexed(index = "org.emonocot.model.common.SearchableObject")
@ClassBridge(name = "taxon", impl = TaxonomyBridge.class, index = Index.UN_TOKENIZED,
        analyzer = @Analyzer(definition = "facetAnalyzer"))
public class Image extends SearchableObject implements NonOwned {
    /**
     *
     */
    private static long serialVersionUID = 3341900807619517602L;

    /**
     *
     */
    private String title;

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String spatial;

    /**
     *
     */
    private ImageFormat format;

    /**
     *
     */
    private String subject;

    /**
     *
     */
    private Point location;

    /**
     *
     */
    private Taxon taxon;

    /**
     *
     */
    private Set<Taxon> taxa = new HashSet<Taxon>();

    /**
     *
     */
    private Set<Annotation> annotations = new HashSet<Annotation>();

    /**
     *
     */
    private Long id;
    
    /**
     *
     */
    private String creator;
    
    /**
     *
     */
    private String references;
    
    /**
     *
     */
    private String contributor;
    
    /**
     *
     */
    private String publisher;
    
    /**
     *
     */
    private String audience;

    /**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * REMEMBER: references is a reserved word in mysql
	 * @return the references
	 */
	@Column(name = "source")
	public String getReferences() {
		return references;
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(String references) {
		this.references = references;
	}

	/**
    *
    * @return the description
    */
   @Lob
   @Field
   public String getDescription() {
       return description;
   }

   /**
    *
    * @param description Set the description
    */
   public void setDescription(String description) {
       this.description = description;
   }

   /**
    * REMEMBER: spatial is a reserved word in mysql!
    * @return the location as a string
    */
   @Field
   @Column(name = "locality")
   public String getSpatial() {
       return spatial;
   }

   /**
    *
    * @param locality Set the location as a string
    */
   public void setSpatial(final String locality) {
       this.spatial = locality;
   }

   /**
    *
    * @return the format of the image
    */
   @Enumerated(EnumType.STRING)
   public ImageFormat getFormat() {
       return format;
   }

   /**
    *
    * @param format Set the format of the image
    */
   public void setFormat(ImageFormat format) {
       this.format = format;
   }

   /**
    *
    * @return the keywords as a string, comma separated
    */
   @Field
   public String getSubject() {
       return subject;
   }

   /**
    *
    * @param keywords Set the keywords as a string, comma separated
    */
   public void setSubject(String keywords) {
       this.subject = keywords;
   }

   /**
    *
    * @return the location the image was taken
    */
   @Type(type = "spatialType")
   public Point getLocation() {
       return location;
   }

   /**
    *
    * @param location Set the location the image was taken
    */
   public void setLocation(Point location) {
       this.location = location;
   }

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
     *
     * @return the caption
     */
    @Fields({ @Field, @Field(name = "label", index = Index.UN_TOKENIZED) })
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param caption
     *            Set the caption
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
	 * @return the contributor
	 */
	public String getContributor() {
		return contributor;
	}

	/**
	 * @param contributor the contributor to set
	 */
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * @param publisher the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return the audience
	 */
	public String getAudience() {
		return audience;
	}

	/**
	 * @param audience the audience to set
	 */
	public void setAudience(String audience) {
		this.audience = audience;
	}

	/**
     * The taxon (page) this image should link to - should be the lowest rank
     * taxon in taxa?
     *
     * @return the taxon this image is of
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @IndexedEmbedded
    public Taxon getTaxon() {
        return taxon;
    }

    /**
     *
     * @param taxon
     *            Set the taxon this image is of
     */
    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    /**
     * The list of all taxa associated with this image - including e.g. genera
     * family, and so on.
     *
     * @return a list of taxa
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Taxon_Image", joinColumns = {@JoinColumn(name = "images_id")}, inverseJoinColumns = {@JoinColumn(name = "Taxon_id")})
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
    @JsonSerialize(contentUsing = TaxonSerializer.class)
    public Set<Taxon> getTaxa() {
        return taxa;
    }

    /**
     *
     * @param taxa
     *            Set the taxa associated with this image
     */
    @JsonDeserialize(contentUsing = TaxonDeserializer.class)
    public void setTaxa(Set<Taxon> taxa) {
        this.taxa = taxa;
    }

    /**
     * @return the annotations
     */
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "annotatedObjId")
    @Where(clause = "annotatedObjType = 'Image'")
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
