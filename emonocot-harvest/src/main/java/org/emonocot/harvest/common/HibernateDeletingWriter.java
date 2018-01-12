/*
 * This is eMonocot, a global online biodiversity information resource.
 *
 * Copyright © 2011–2015 The Board of Trustees of the Royal Botanic Gardens, Kew and The University of Oxford
 *
 * eMonocot is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * eMonocot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * The complete text of the GNU Affero General Public License is in the source repository as the file
 * ‘COPYING’.  It is also available from <http://www.gnu.org/licenses/>.
 */
package org.emonocot.harvest.common;

import java.util.List;

import org.emonocot.api.TaxonService;
import org.emonocot.model.Base;
import org.emonocot.model.Reference;
import org.emonocot.model.Taxon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateDeletingWriter<T extends Base> extends HibernateDaoSupport implements
ItemWriter<T> {

    private static Logger logger = LoggerFactory.getLogger(HibernateDeletingWriter.class);

	@Autowired
	TaxonService taxonService;

	public void setTaxonService(TaxonService taxonService) {
		this.taxonService = taxonService;
	}

	@Override
	public void write(List<? extends T> items) throws Exception {
        logger.debug("Starting write | items size:" + items.size() + " | item0:" + items.get(0).getId());
		if (items.get(0) instanceof Reference) {//If so, they should all be
			for (T t : items) {
				//Check all taxa?!?
				Taxon example = new Taxon();
				example.setNamePublishedIn((Reference) t);
				List<Taxon> linkedTaxa = taxonService.searchByExample(example, false, false).getRecords();
                logger.debug("Getting the Taxa list");
				for (Taxon taxon : linkedTaxa) {
					taxon.setNamePublishedIn(null);
				}
                logger.debug("Set all namePublishedIn to null");
				getHibernateTemplate().saveOrUpdateAll(linkedTaxa);
				example = new Taxon();
				example.setNameAccordingTo((Reference) t);
				linkedTaxa = taxonService.searchByExample(example, false, false).getRecords();
				for (Taxon taxon : linkedTaxa) {
					taxon.setNameAccordingTo(null);
				}
				getHibernateTemplate().saveOrUpdateAll(linkedTaxa);
			}
		}
		getHibernateTemplate().deleteAll(items);
        logger.debug("Ending write | Deleted all items");
	}

}
