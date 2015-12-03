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
    private int xBlocks;

    // Number of vertical segmentation blocks
    private int yBlocks;

    /**
     * A constructor creating a HOG feature detector.
     *
     * @param xBlocks the number of the horizontal segmentation blocks.
     * @param yBlocks the number of the vertical segmentation blocks.
     */
    public Hog(int xBlocks, int yBlocks) {
        this.xBlocks = xBlocks;
        this.yBlocks = yBlocks;
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

        int w = img.getWidth() / xBlocks;
        int h = img.getHeight() / yBlocks;

        BlockNormalisation norm = FixedHOGStrategy.BlockNormalisation.L2;

        FixedHOGStrategy strategy = new FixedHOGStrategy(w, h, xBlocks, yBlocks, 1, 1, norm);

        HOG detector = new HOG(strategy);

        detector.analyseImage(img);

        Rectangle window = new Rectangle(0, 0, img.getWidth(), img.getHeight());

        double[] descriptor = detector.getFeatureVector(window).values;

        return new Description(descriptor);
    }
}
