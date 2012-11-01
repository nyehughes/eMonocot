package org.emonocot.persistence.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.emonocot.model.TypeAndSpecimen;
import org.emonocot.model.hibernate.Fetch;
import org.emonocot.persistence.dao.TypeAndSpecimenDao;
import org.springframework.stereotype.Repository;

@Repository
public class TypeAndSpecimenDaoImpl extends DaoImpl<TypeAndSpecimen> implements
		TypeAndSpecimenDao {

	private static Map<String, Fetch[]> FETCH_PROFILES;
	
	static {
	       FETCH_PROFILES = new HashMap<String, Fetch[]>();
	}
	
	public TypeAndSpecimenDaoImpl() {
		super(TypeAndSpecimen.class);
	}

	@Override
	protected Fetch[] getProfile(String profile) {
		return TypeAndSpecimenDaoImpl.FETCH_PROFILES.get(profile);
	}

	
}
