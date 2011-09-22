package org.emonocot.portal.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.emonocot.model.media.Image;
import org.emonocot.portal.driver.Portal;
import org.emonocot.portal.driver.SearchResultsPage;
import org.emonocot.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.annotation.After;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;

/**
 *
 * @author ben
 *
 */
public class FacetOnObjectTypeFeature {
    /**
     *
     */
    private String imageId;

    /**
     *
     */
    private SearchResultsPage searchResultsPage;

    /**
     *
     */
    @Autowired
    private ImageService imageService;

    /**
    *
    */
    @Autowired
    private Portal portal;

    /**
     *
     * @param id
     *            Set the image identifier
     * @param caption
     *            Set the image caption
     */
    @Given("^there is an image with id \"([^\"]+)\" and caption \"([^\"]+)\"$")
    public final void thereIsAnImageWithIdAndCaption(final String id,
            final String caption) {
        this.imageId = id;
        Image i = new Image();
        i.setCaption(caption);
        i.setIdentifier(id);
        imageService.save(i);
    }

    /**
     *
     * @param query
     *            Set the search query
     */
    @When("^I search for \"([^\"]+)\"$")
    public final void whenISearchFor(final String query) {
        searchResultsPage = portal.search(query);
    }

    /**
    *
    * @param clazz
    *            Set the class to restrict the search results to
    */
   @When("^I select \"([^\"]+)\"$")
   public final void iSelect(final String clazz) {
       searchResultsPage = searchResultsPage.selectClassFacet(clazz);
   }

    /**
     *
     * @param results
     *            Set the number of results
     */
    @Then("^there should be (\\d) result[s]?$")
    public final void thereShouldBeResults(final Integer results) {
        assertEquals(results, searchResultsPage.getResultNumber());
    }

    /**
     *
     * @param options
     *            Set the options
     *
     */
    @Then("^there should be the following options$")
    public final void thereShouldBeOptions(List<String> options) {
        assertEquals((Integer) options.size(),
                searchResultsPage.getClassFacetNumber());
        assertArrayEquals(
                options.toArray(new String[options.size()]),
                searchResultsPage.getClassFacets());

    }

    /**
   *
   */
    @After
    public final void tearDown() {
        if (imageId != null) {
            imageService.delete(imageId);
        }
    }

}
