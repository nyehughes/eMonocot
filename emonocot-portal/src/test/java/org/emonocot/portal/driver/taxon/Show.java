package org.emonocot.portal.driver.taxon;

import java.util.List;

import org.emonocot.portal.driver.IllustratedPage;
import org.emonocot.portal.driver.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

/**
 *
 * @author ben
 *
 */
public class Show extends PageObject implements IllustratedPage {

    /**
     *
     */
    @FindBy(how = How.ID, using = "page-title")
    private WebElement title;
    
    /**
    *
    */
    @FindBy(how = How.ID, using = "distribution-list")
    private WebElement textualDistribution;

    /**
     *
     */
    @FindBy(how = How.ID, using = "description")
    private WebElement description;
    
    /**
     *
     */
    @FindBy(how = How.ID, using = "taxonomicStatus")
    private WebElement taxonomicStatus;
    
    /**
    *
    */
    @FindBy(how = How.ID, using = "sectionHeader")
    private WebElement sectionHeader;
    

    /**
     *
     */
    @FindBy(how = How.ID, using = "protologue")
    private WebElement protologue;
    
    /**
     *
     */
    @FindBy(how = How.CLASS_NAME, using = "ad-image-wrapper")
    private WebElement mainImage;

    /**
     *
     */
    @FindBy(how = How.CLASS_NAME, using = "thumbnails")
    private WebElement thumbnailContainer;

    /**
    *
    */
   @FindBy(how = How.CLASS_NAME, using = "ancestorsList")
   private List<WebElement> ancestors;

   /**
   *
   */
   @FindBy(how = How.CLASS_NAME, using = "childrenList")
   private WebElement children;

   /**
   *
   */
   @FindBy(how = How.CLASS_NAME, using = "childrenList")
   private List<WebElement> subordinateNumber;

   /**
    *
    */
   @FindBy(how = How.ID, using = "alternative-map")
   private WebElement map;

    /**
     *
     * @return the page title
     */
    public String getTaxonName() {
        return title.findElement(By.xpath("em")).getText();
    }

    /**
     *
     * @param attribute Set the CSS attribute you're interested in
     * @return the class of the page title
     */
    public String getTaxonNameStyle(String attribute) {
        return title.findElement(By.xpath("em")).getCssValue(attribute);
    }

    /**
     *
     * @param heading Set the heading
     * @return a paragraph with that title
     */
    public String getParagraph(String heading) {    	
        WebElement element = description.findElement(By.xpath("div/div[h2/span='" + heading + "']/p"));
        return element.getText();
    }
    


    /**
     *
     * @param heading Set the heading
     * @return true if paragraph exists, false otherwise
     */
    public boolean doesParagraphExist(String heading) {
        try {
            WebElement element = description.findElement(By
                .xpath("div/div[h2/span='" + heading + "']/p"));
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return the protologue
     */
    public String getProtologue() {
        return protologue.getText();
    }

    /**
     * @param property Set the property to get
     * @return the caption of the main image
     */
    public String getMainImageProperty(String property) {
        return mainImage.findElement(By.className("ad-image-description"))
                .getText();
    }

    /**
     *
     * @return the url of the main image
     */
    public String getMainImage() {
    	String src = mainImage.findElement(By.tagName("img")).getAttribute("src");
    	return src.substring(src.lastIndexOf("/") + 1); 
    }

    /**
     *
     * @return the number of thumbnails
     */
    public int getThumbnails() {
        return thumbnailContainer.findElements(By.tagName("li")).size();
    }

    /**
     *
     * @return the url of the distribution map
     */
    public String getDistributionMap() {
        return map.getAttribute("src");
    }

    /**
     *
     * @param thumbnail Set the thumbnail
     */
    public void selectThumbnail(Integer thumbnail) {
        List<WebElement> thumbnails = thumbnailContainer.findElements(By
                .xpath("li/a"));
        thumbnails.get(thumbnail - 1).click();
    }

    /**
     *
     * @return the image page
     */
    public PageObject selectMainImage() {
        String link = mainImage.findElement(
                By.xpath("div[@class='ad-image']/a")).getAttribute("href");
        return openAs(link, org.emonocot.portal.driver.image.Show.class);
    }

    /**
     *
     * @param thumbnail Set the thumbnail number
     * @return the caption
     */
    public String getThumbnailCaption(int thumbnail) {
        List<WebElement> thumbnails = thumbnailContainer.findElements(By
                .xpath("li/a/img"));
        return thumbnails.get(thumbnail - 1).getAttribute("title");
    }

   /**
    *
    * @param thumbnail Set the thumbnail number
    * @return the image
    */
    public String getThumbnailImage(int thumbnail) {
        List<WebElement> thumbnails = thumbnailContainer.findElements(By
                .xpath("li/a"));
        return thumbnails.get(thumbnail - 1).getAttribute("href");
    }

    /**
     *
     * @return the number of ancestors
     */
    public Integer getAncestorsNumber() {
        return ancestors.size();
    }

    /**
     *
     * @return the subordinate taxa
     */
    public String getSubordinateTaxa() {
        return children.getText();
    }

    /**
     *
     * @return the number of children
     */
    public Integer getChildrenNumber() {
        return subordinateNumber.size();
    }

    /**
     *
     * @param citeKey The citation key of the bibliography entry of interest
     * @return the bibliography entry
     */
    public String getBibliographyEntry(String citeKey) {
        WebElement bibliography = webDriver.findElement(By.id("bibliography"));
        WebElement element = bibliography.findElement(By
                .xpath("div/div/ul/li[a = '" + citeKey + "']"));
        return element.getText();
    }
    
    /**
    *
    * @param provenanceKey The key of the provenance entry of interest
    * @return the provenance entry
    */
   public String getProvenanceEntry(String provenanceKey) {
       WebElement provenance = webDriver.findElement(By.id("sources"));
       WebElement provenanceElement = provenance.findElement(By
               .xpath("div/div/dl/dd/ul/li[a/@id = '" + provenanceKey + "']"));
       return provenanceElement.getText();
   }
    

    /**
     *
     * @param topic The text topic
     * @return the citations for that topic
     */
    public String getCitations(String topic) {
        WebElement element = description.findElement(By
                .xpath("div/div[h2/span = '" + topic + "']/ul[@class='citations']"));
        return element.getText();
    }

    /**
     *
     * @return the protolog link
     */
    public String getProtologueLink() {
        WebElement link = protologue.findElement(By.tagName("a"));
        return link.getAttribute("href");
    }

	public String getTextualDistribution() {
		return textualDistribution.getText();
	}

	public String getTaxonomicStatus() {
		return taxonomicStatus.getText();
	}

	
}
