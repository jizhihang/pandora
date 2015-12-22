package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.feature.dense.gradient.HOG;
import org.openimaj.image.feature.dense.gradient.binning.FixedHOGStrategy;
import org.openimaj.image.feature.dense.gradient.binning.FixedHOGStrategy.BlockNormalisation;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 * A global detector extracting the Histogram of Oriented Gradients (HOG)
 * descriptor given an image using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class Hog implements FeatureDetector {

    // Number of horizontal segmentation blocks
    private int widthBlocks;

    // Number of vertical segmentation blocks
    private int heightBlocks;

    /**
     * A constructor initiating the default parameters.
     */
    public Hog() {
        widthBlocks = 1;
        heightBlocks = 1;
    }

    /**
     * A constructor creating a HOG feature detector.
     *
     * @param widthBlocks the number of the horizontal segmentation blocks.
     * @param heightBlocks the number of the vertical segmentation blocks.
     */
    public Hog(int widthBlocks, int heightBlocks) {
        this.widthBlocks = widthBlocks;
        this.heightBlocks = heightBlocks;
    }

    /**
     * A method detecting a visual description given an image item.
     *
     * @param image the image item.
     * @return the visual description detected.
     * @throws Exception throws unknown error exceptions.
     */
    @Override
    public Description extract(BufferedImage image) throws Exception {
        FImage img = ImageUtilities.createFImage(image);

        int w = img.getWidth() / widthBlocks;
        int h = img.getHeight() / heightBlocks;

        BlockNormalisation norm = FixedHOGStrategy.BlockNormalisation.L2;

        FixedHOGStrategy strategy = new FixedHOGStrategy(w, h, widthBlocks, heightBlocks, 1, 1, norm);

        HOG detector = new HOG(strategy);

        detector.analyseImage(img);

        Rectangle window = new Rectangle(0, 0, img.getWidth(), img.getHeight());

        double[] descriptor = detector.getFeatureVector(window).values;

        return new Description(descriptor);
    }

    public int getWidthBlocks() {
        return widthBlocks;
    }

    public void setWidthBlocks(int widthBlocks) {
        this.widthBlocks = widthBlocks;
    }

    public int getHeightBlocks() {
        return heightBlocks;
    }

    public void setHeightBlocks(int heightBlocks) {
        this.heightBlocks = heightBlocks;
    }
}
