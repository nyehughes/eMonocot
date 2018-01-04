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
package org.emonocot.portal.driver;

import java.util.ArrayList;
import java.util.List;

import org.emonocot.portal.remoting.ImageDaoImpl;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ben
 *
 */
public class Search extends PageObject {

	/**
	 *
	 */
	@FindBy(how = How.ID, using = "results")
	private WebElement results;

	/**
	 *
	 */
	@FindBy(how = How.ID, using = "entryRank")
	private WebElement rank;

	/**
	 *
	 */
	@FindBy(how = How.ID, using = "entryStatus")
	private WebElement resultStatus;

	/**
	 *
	 */
	@FindBy(how = How.CLASS_NAME, using = "ui-autocomplete")
	private WebElement autocomplete;

	/**
	 *
	 */
	@FindBy(how = How.ID, using = "query")
	private WebElement query;

	/**
	 *
	 */
	@FindBy(how = How.ID, using = "pages")
	private WebElement message;

	/**
	 *
	 */
	@FindBy(how = How.ID, using = "facets")
	private WebElement facets;

	/**
	 *
	 */
	@FindBy(how = How.ID, using = "sorting")
	private WebElement sorting;

	@FindBy(how = How.CLASS_NAME, using = "pagination")
	private WebElement pagination;


	/**
	 *
	 */
	@FindBy(how = How.ID, using = "viewIcons")
	private WebElement viewIcons;

	/**
	 *
	 * @return the number of results
	 */
	public final Integer getResultNumber() {
		return results.findElements(By.className("result")).size();
	}

	/**
	 *
	 * @return the rank of results
	 */
	public final String getResultEntryRank() {
		return rank.getText();
	}


	/**
	 *
	 * @return the status of results
	 */
	public final String getResultEntryStatus() {
		return resultStatus.getText();
	}

	/**
	 * @param facetName
	 *            set the facet name
	 * @return the number of class facets
	 */
	public final Integer getFacetNumber(final String facetName) {
		return facets.findElements(
				By.xpath("li[@class = '" + facetName + "']/a")).size();
	}

	/**
	 *
	 * @param sort
	 *            The string to sort by
	 * @return a search results page
	 */
	public final Search sort(final String sort) {
		WebElement classFacet = sorting.findElement(By
				.xpath("li/a[text() = \'" + sort
						+ "\']"));
		return openAs(classFacet.getAttribute("href"), Search.class);
	}

	/**
	 *
	 * @param grid
	 *            Go to the grid view
	 * @return a search results page
	 */
	public final Search view(final String grid) {
		WebElement idViewIcon = viewIcons.findElement(By
				.xpath("div/a[@title = \'" + grid + "\']"));
		return openAs(idViewIcon.getAttribute("href"), Search.class);
	}

	/**
	 * @param facetName
	 *            Set the facet name
	 * @return an array of the facet labels
	 */
	public final String[] getFacets(final String facetName) {
		List<WebElement> facetOptions = facets.findElements(By
				.xpath("div/div/div/ul/li[@class = '" + facetName + "']"));
		String[] result = new String[facetOptions.size()];
		for (int i = 0; i < result.length; i++) {
			WebElement classFacetOption = facetOptions.get(i);

			try {
				result[i] = classFacetOption.findElement(By.className("facetValue"))
						.getText();
			} catch (NoSuchElementException nsee) {
				result[i] = classFacetOption.getText();
			}
		}
		return result;
	}

	/**
	 * @return an array of the autocomplete options
	 */
	public final String[] getAutocompleteOptions() {
		List<WebElement> autocompleteOptions = autocomplete.findElements(By
				.xpath("li/a"));
		String[] result = new String[autocompleteOptions.size()];
		for (int i = 0; i < result.length; i++) {
			WebElement autocompleteOption = autocompleteOptions.get(i);
			result[i] = autocompleteOption.getText();
		}
		return result;
	}

	/**
	 * @param facetName
	 *            the name of the facet
	 * @param facetValue
	 *            the name of the facet value to select
	 * @return the corresponding search results page
	 */
	public final Search selectFacet(final String facetName, final String facetValue) {
		WebElement classFacet = facets.findElement(By.xpath("div/div/div/ul/li[@class = '" + facetName + "']/a[span/text() = \'" + facetValue + "\']"));
		return openAs(classFacet.getAttribute("href"), Search.class);
	}

	/**
	 *
	 * @return an array of results
	 */
	public final List<String[]> getResults() {

		List<WebElement> links = results.findElements(By.className("result"));
		List<String[]> linksList = new ArrayList<String[]>();
		for (WebElement webElement : links) {
			String[] link = new String[2];
			String href = webElement.getAttribute("href");
			if(this.imageDao.containsPageLocation(href)) {
				href = this.imageDao.getIdentifier(href);
			} else {
				String dataLink = webElement.getAttribute("data-link");
				if(getPort() == 80) {
					dataLink = getHost() + dataLink;
				} else {
					dataLink = getHost() + ":" + getPort() + dataLink;
				}
				if(this.imageDao.containsPageLocation(dataLink)) {
					href = this.imageDao.getIdentifier(dataLink);
				}
			}
			if(this.keyDao.containsPageLocation(href)) {
				href = this.keyDao.getIdentifier(href);
			}
			link[0] = href.substring(href.lastIndexOf("/") + 1);
			if (webElement.getAttribute("data-original-title") == null){
				link[1] = webElement.getAttribute("title");
			} else {
				link[1] = webElement.getAttribute("data-original-title");
			}
			linksList.add(link);
		}
		return linksList;
	}


	/**
	 *
	 * @return the message from the search results page
	 */
	public final String getMessage() {
		return message.getText();
	}

	/**
	 *
	 * @param queryString
	 *            Set the query on the search results page
	 */
	public final void setQuery(final String queryString) {
		query.sendKeys(queryString);
	}

	/**
	 *
	 * @return true if the icons exist, false otherwise
	 */
	public final Boolean viewIconDisplay() {
		try {
			WebElement element = viewIcons.findElement(By.tagName("div"));
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	/**
	 *
	 * @return true if the results are displayed in a grid
	 */
	public final boolean resultsAreDisplayedInGrid() {
		try {
			WebElement element = results.findElement(By.xpath("ul/li"));
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;

	}

	public String getPaginationLabel() {
		WebElement paginationLabel = pagination.findElement(By.xpath("ul/li[@class='active']"));
		return paginationLabel.getText();
	}

	public PageObject selectNextPage() {
		WebElement nextLink = pagination.findElement(By.xpath("ul/li[@class='next']/a"));
		return openAs(nextLink.getAttribute("href"), Search.class);
	}


}
