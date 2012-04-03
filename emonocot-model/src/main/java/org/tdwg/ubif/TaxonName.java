package org.tdwg.ubif;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 *
 * @author ben
 *
 */
public class TaxonName {

    /**
     *
     */
    @XStreamAsAttribute
    private String id;

    /**
     *
     */
    @XStreamAlias("Representation")
    private Representation representation;

    /**
     * @return the representation
     */
    public final Representation getRepresentation() {
        return representation;
    }

    /**
     * @param newRepresentation the representation to set
     */
    public final void setRepresentation(
            final Representation newRepresentation) {
        this.representation = newRepresentation;
    }

    /**
     *
     * @param newId Set the id
     */
    public final void setId(final String newId) {
        this.id = newId;
    }

    /**
     *
     * @return the id
     */
    public final String getId() {
        return id;
    }
}
