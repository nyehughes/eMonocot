package org.emonocot.persistence.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.emonocot.model.Distribution;
import org.emonocot.model.hibernate.Fetch;
import org.emonocot.persistence.dao.DistributionDao;
import org.springframework.stereotype.Repository;

@Repository
public class DistributionDaoImpl extends DaoImpl<Distribution> implements
		DistributionDao {
	
    private static Map<String, Fetch[]> FETCH_PROFILES;
	
	static {
	       FETCH_PROFILES = new HashMap<String, Fetch[]>();
	}

	public DistributionDaoImpl() {
		super(Distribution.class);
	}

	@Override
	protected Fetch[] getProfile(String profile) {
		return DistributionDaoImpl.FETCH_PROFILES.get(profile);
	}

	

}
