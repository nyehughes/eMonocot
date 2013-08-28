package org.emonocot.harvest.media;

import java.io.File;

import org.emonocot.harvest.common.GetResourceClient;
import org.emonocot.model.Image;
import org.emonocot.model.constants.AnnotationCode;
import org.emonocot.model.constants.AnnotationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 * @author ben
 *
 */
public class ImageFileProcessorImpl implements ItemProcessor<Image, Image>, ImageFileProcessor {

   /**
    *
    */
    private Logger logger = LoggerFactory.getLogger(ImageFileProcessorImpl.class);

    /**
     *
     */
    private final Integer IMAGE_DIMENSION = 1000;

    /**
     *
     */
    private String imageDirectory;
    
    /**
     * 
     */
    private ImageAnnotator imageAnnotator;

    /**
     *
     */
    private GetResourceClient getResourceClient;

    /**
     *
     * @param newImageDirectory Set the image directory
     */
    public final void setImageDirectory(
            final String newImageDirectory) {
        this.imageDirectory = newImageDirectory;
    }

    /**
     * @param imageAnnotator the imageAnnotator to set
     */
    public void setImageAnnotator(ImageAnnotator imageAnnotator) {
        this.imageAnnotator = imageAnnotator;
    }

    /**
     *
     * @param newGetResourceClient set the get resource client
     */
    public final void setGetResourceClient(
            final GetResourceClient newGetResourceClient) {
        this.getResourceClient = newGetResourceClient;
    }

    /* (non-Javadoc)
	 * @see org.emonocot.harvest.media.ImageFileProcessor#process(org.emonocot.model.Image)
	 */
    @Override
	public final Image process(final Image image) throws Exception {
        String imageFileName = imageDirectory + File.separatorChar  + image.getId() + '.' + image.getFormat();
        File file = new File(imageFileName);
        logger.debug("Image File " + imageFileName);
        if (file.exists()) {
            logger.info("File exists in image directory, issuing a conditional GET");
            ExitStatus status = getResourceClient.getBinaryResource(image.getIdentifier(), Long.toString(file.lastModified()), imageFileName);
            if(status == null || ExitStatus.FAILED.equals(status)) {
                logger.error("There was a problem trying to get " + image.getIdentifier());
                imageAnnotator.annotate(image, AnnotationType.Error, AnnotationCode.BadIdentifier, "The source image wasn't available from " + image.getIdentifier());
                return null;
            } else if ("NOT_MODIFIED".equals(status.getExitCode())) {
                logger.info("The image is already up to date");
            }
        } else {
            try {
                getResourceClient.getBinaryResource(image.getIdentifier(), "1", imageFileName);
                file = new File(imageFileName);
                if (!file.exists()) {
                    logger.error("File does not exist in image directory, skipping");
                    imageAnnotator.annotate(image, AnnotationType.Error, AnnotationCode.BadIdentifier, "The image couldn't be downloaded from " + image.getIdentifier());
                    return null;
                }
            } catch (Exception e) {
                logger.error("Error trying to retrieve " + image.getIdentifier(), e);
                imageAnnotator.annotate(image,AnnotationType.Error, AnnotationCode.BadIdentifier, "There was an error getting the image at " + image.getIdentifier());
                return null;
            }
        }
        return image;
    }

}
