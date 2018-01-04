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
package org.emonocot.api;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.emonocot.api.autocomplete.Match;
import org.emonocot.model.Base;
import org.emonocot.pager.CellSet;
import org.emonocot.pager.Cube;
import org.emonocot.pager.Page;

/**
 * @author ben
 * @param <T>
 */
public interface SearchableService<T extends Base> extends Service<T> {
	/**
	 * @param query
	 *            A lucene query
	 * @param spatialQuery
	 *            A spatial query to filter the results by
	 * @param pageSize
	 *            The maximum number of results to return
	 * @param pageNumber
	 *            The offset (in pageSize chunks, 0-based) from the beginning of
	 *            the recordset
	 * @param facets
	 *            The names of the facets you want to calculate
	 * @param facetPrefixes TODO
	 * @param selectedFacets
	 *            A map of facets which you would like to restrict the search by
	 * @param sort
	 *            a parameter indicating how the results should be sorted
	 * @param fetch
	 *            Set the fetch profile
	 * @return a Page from the resultset
	 */
	Page<T> search(String query, String spatialQuery, Integer pageSize,
			Integer pageNumber, String[] facets,
			Map<String, String> facetPrefixes, Map<String, String> selectedFacets, String sort, String fetch) throws SolrServerException;

	/**
	 *
	 * @param query The query to autocomplete on
	 * @param pageSize The number of matches to return
	 * @param selectedFacets any restrictions on the search
	 * @return a list of match objects
	 */
	List<Match> autocomplete(String query, Integer pageSize, Map<String, String> selectedFacets) throws SolrServerException;

	/**
	 *
	 * @param query
	 * @param pageSize
	 * @param pageNumber
	 * @param selectedFacets
	 * @param sort
	 * @return
	 */
	Page<SolrDocument> searchForDocuments(String query, Integer pageSize, Integer pageNumber, Map<String, String> selectedFacets, String sort) throws SolrServerException;

	/**
	 * Produce a 1 or 2 dimensional table of count data based on the solr
	 * pivot query
	 *
	 * @param rows the level for the rows of the results (if null, the default level is used)
	 * @param cols the level for the cols of the results (if null the table is 1-dimensional)
	 * @param firstCol the offset for the columns
	 * @param maxCols the maximum number of columns returned
	 * @param firstRow the offset for the rows
	 * @param maxRows the maximum number of rows returned
	 * @param selectedFacets the restrictions on the data
	 * @param facets the factes which you want to calculate
	 * @param cube the Cube object which contains the dimensions, levels and members
	 * @return a cell set
	 */
	CellSet analyse(String rows, String cols, Integer firstCol,
			Integer maxCols, Integer firstRow, Integer maxRows,
			Map<String, String> selectedFacets, String[] array, Cube cube) throws SolrServerException;

	/**
	 *
	 * @param solrDocument
	 * @return
	 */
	T loadObjectForDocument(SolrDocument solrDocument);

	void index(Long id);
}
