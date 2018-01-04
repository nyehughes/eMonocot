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
package org.emonocot.portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.solr.client.solrj.SolrServerException;
import org.emonocot.api.OrganisationService;
import org.emonocot.api.ResourceService;
import org.emonocot.model.registry.Organisation;
import org.emonocot.pager.Page;
import org.emonocot.portal.format.annotation.FacetRequestFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author ben
 *
 */
@Controller
@RequestMapping("/organisation")
public class OrganisationController extends GenericController<Organisation, OrganisationService> {

	private static Logger logger = LoggerFactory.getLogger(OrganisationController.class);

	/**
	 *
	 */
	public OrganisationController() {
		super("organisation", Organisation.class);
	}

	/**
	 *
	 */
	private ResourceService resourceService;

	/**
	 *
	 * @param resourceService
	 *            Set the source service
	 */
	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	/**
	 *
	 * @param organisationService
	 *            Set the source service
	 */
	@Autowired
	public void setOrganisationService(OrganisationService organisationService) {
		super.setService(organisationService);
	}

	/**
	 *
	 * @param model
	 *            Set the model
	 * @param limit
	 *            Set the maximum number of objects to return
	 * @param start
	 *            Set the offset
	 * @return the name of the view
	 */
	@RequestMapping(method = RequestMethod.GET, params = {"!form"}, produces = "text/html")
	public String list(
			Model model,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(value = "facet", required = false) @FacetRequestFormat List<FacetRequest> facets,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "view", required = false) String view) throws SolrServerException {

		Map<String, String> selectedFacets = new HashMap<String, String>();
		if (facets != null && !facets.isEmpty()) {
			for (FacetRequest facetRequest : facets) {
				selectedFacets.put(facetRequest.getFacet(),
						facetRequest.getSelected());
			}
		}
		selectedFacets.put("base.class_s", "org.emonocot.model.registry.Organisation");
		Page<Organisation> result = getService().search(query, null, limit, start,
				new String[] { "organisation.subject_t" }, null, selectedFacets, sort, "source-with-jobs");
		model.addAttribute("result", result);
		result.putParam("query", query);
		return "organisation/list";
	}

	/**
	 *
	 * @param model
	 *            Set the model
	 * @return the name of the view
	 */
	@RequestMapping(method = RequestMethod.GET, params = "form", produces = "text/html")
	public String create(Model model) {
		model.addAttribute(new Organisation());
		return "organisation/create";
	}

	/**
	 * @param session
	 *            Set the session
	 * @param organisation
	 *            Set the source
	 * @param result
	 *            Set the binding results
	 * @return a model and view
	 */
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String post(@Valid Organisation organisation,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "organisation/create";
		}

		getService().saveOrUpdate(organisation);
		String[] codes = new String[] { "organisation.was.created" };
		Object[] args = new Object[] { organisation.getTitle() };
		DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(
				codes, args);
		redirectAttributes.addFlashAttribute("info", message);
		return "redirect:/organisation";
	}

	/**
	 * @param organisationId
	 *            Set the identifier of the source
	 * @param limit
	 *            Set the maximum number of results
	 * @param start
	 *            Set the offset
	 * @param uiModel
	 *            Set the model
	 * @return the view name
	 */
	@RequestMapping(value = "/{organisationId}", method = RequestMethod.GET, params = {"!form", "!delete"}, produces = "text/html")
	public String show(@PathVariable String organisationId,
			Model uiModel) {
		uiModel.addAttribute(getService().find(organisationId,"source-with-jobs"));
		return "organisation/show";
	}

	/**
	 *
	 * @param model
	 *            Set the model
	 * @param organisationId
	 *            Set the identifier
	 * @return the name of the view
	 */
	@RequestMapping(value = "/{organisationId}", method = RequestMethod.GET, params = "form", produces = "text/html")
	public String update(@PathVariable String organisationId,
			Model model) {
		model.addAttribute(getService().load(organisationId));
		return "organisation/update";
	}

	/**
	 * @param organisationId
	 *            Set the identifier
	 * @param session
	 *            Set the session
	 * @param organisation
	 *            Set the source
	 * @param result
	 *            Set the binding results
	 * @return the model name
	 */
	@RequestMapping(value = "/{organisationId}", method = RequestMethod.POST, produces = "text/html")
	public String post(
			@PathVariable String organisationId,
			@Valid Organisation organisation, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "organisation/update";
		}
		Organisation persistedSource = getService().load(organisationId);
		persistedSource.setTitle(organisation.getTitle());
		persistedSource.setUri(organisation.getUri());
		persistedSource.setCreator(organisation.getCreator());
		persistedSource.setCreatorEmail(organisation.getCreatorEmail());
		persistedSource.setCreated(organisation.getCreated());
		persistedSource.setDescription(organisation.getDescription());
		persistedSource.setPublisherName(organisation.getPublisherName());
		persistedSource.setPublisherEmail(organisation.getPublisherEmail());
		persistedSource.setCommentsEmailedTo(organisation.getCommentsEmailedTo());
		persistedSource.setInsertCommentsIntoScratchpad(organisation.getInsertCommentsIntoScratchpad());
		persistedSource.setSubject(organisation.getSubject());
		persistedSource.setBibliographicCitation(organisation.getBibliographicCitation());
		persistedSource.setLogoUrl(organisation.getLogoUrl());
		persistedSource.setFooterLogoPosition(organisation.getFooterLogoPosition());
		getService().saveOrUpdate(persistedSource);
		String[] codes = new String[] { "organisation.updated" };
		Object[] args = new Object[] { organisation.getTitle() };
		DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
		redirectAttributes.addFlashAttribute("info", message);
		return "redirect:/organisation/" + organisationId;
	}

	@RequestMapping(value = "/{identifier}",  method = RequestMethod.GET, params = {"delete"}, produces = "text/html")
	public String delete(@PathVariable String identifier, RedirectAttributes redirectAttributes) {
		Organisation organisation = getService().find(identifier);
		getService().delete(identifier);
		String[] codes = new String[] { "organisation.deleted" };
		Object[] args = new Object[] { organisation.getTitle() };
		DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
		redirectAttributes.addFlashAttribute("info", message);
		return "redirect:/organisation";
	}
}
