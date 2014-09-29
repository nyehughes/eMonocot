/**
 * 
 */
package org.emonocot.model;

import java.util.List;
import java.util.Set;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.solr.common.SolrInputDocument;
import org.emonocot.model.constants.MediaFormat;

/**
 * @author jk00kg
 * 
 * See <a href="http://rs.gbif.org/extension/gbif/1.0/multimedia.xml">http://rs.gbif.org/extension/gbif/1.0/multimedia.xml</a>
 */
@MappedSuperclass
public abstract class Multimedia extends SearchableObject implements NonOwned, Media {

    private static final long serialVersionUID = -8178055800655899536L;

    private String title;

    private String description;

    private MediaFormat format;

    private String creator;

    private String references;

    private String contributor;

    private String publisher;

    private String audience;

    private String source;

    /**
     * @return the title
     */
    @Size(max = 255)
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    @Lob
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the format
     */
    @Enumerated(EnumType.STRING)
    public MediaFormat getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(MediaFormat format) {
        this.format = format;
    }

    /**
     * @return the taxa
     */
    @Transient
    abstract public Set<Taxon> getTaxa();

    /**
     * @param taxa the taxa to set
     */
    abstract public void setTaxa(Set<Taxon> taxa);

    /**
     * @return the creator
     */
    @Size(max = 255)
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
     * @return the references
     */
    @Size(max = 255)
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
     * @return the contributor
     */
    @Size(max = 255)
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
    @Size(max = 255)
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
    @Size(max = 255)
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
     * @return the comments
     */
    @Transient
    abstract public List<Comment> getComments();

    /**
     * @param comments the comments to set
     */
    abstract public void setComments(List<Comment> comments);

    /**
     * @return the annotations
     */
    @Transient
    abstract public Set<Annotation> getAnnotations();

    /*(non-Javadoc)
     * @see org.emonocot.model.Annotated#setAnnotations(java.util.Set)
     */
    abstract public void setAnnotations(Set<Annotation> annotations);

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public SolrInputDocument toSolrInputDocument() {
        SolrInputDocument sid = super.toSolrInputDocument();
        sid.addField("searchable.label_sort", getTitle());

        return sid;
    }

}