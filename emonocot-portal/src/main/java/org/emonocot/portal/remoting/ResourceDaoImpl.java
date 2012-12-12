package org.emonocot.portal.remoting;

import java.util.List;

import org.emonocot.model.registry.Resource;
import org.emonocot.persistence.dao.ResourceDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ben
 *
 */
@Repository
public class ResourceDaoImpl extends DaoImpl<Resource> implements ResourceDao {

    /**
     *
     */
    public ResourceDaoImpl() {
        super(Resource.class, "resource");
    }

    /**
     * @param sourceId Set the source identifier
     * @return the total number of jobs for a given source
     */
    public final Long count(final String sourceId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param sourceId Set the source identifier
     * @param page Set the offset (in size chunks, 0-based), optional
     * @param size Set the page size
     * @return A list of jobs
     */
    public final List<Resource> list(final String sourceId, final Integer page,
            final Integer size) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param id Set the job id
     * @return the job
     */
    public final Resource findByJobId(final Long id) {
        // TODO Auto-generated method stub
        return null;
    }

}