package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.feature.dense.gradient.PHOG;
import org.openimaj.image.processing.convolution.FImageGradients;
import org.openimaj.image.processing.convolution.FImageGradients.Mode;

/**
 * A global detector extracting the PHOG histogram descriptor given an image
 * using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class Phog implements FeatureDetector {

    // Levels
    private int levels;

    // Histogram bins
    private int bins;

    // Orientation signed mode
    private boolean signed;
    
    /**
     * A constructor initiating the default parameters.
     */
    public Phog() {
        levels = 3;
        bins = 8;
        signed = true;
    }

    /**
     * A constructor creating a PHOG feature detector given the parameters.
     *
     * @param levels the number of levels.
     * @param bins the number of bins.
     * @param signed true for signed orientation otherwise false.
     */
    public Phog(int levels, int bins, boolean signed) {
        this.levels = levels;
        this.bins = bins;
        this.signed = signed;
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

        Mode orientation = signed ? FImageGradients.Mode.Signed : FImageGradients.Mode.Unsigned;

        PHOG detector = new PHOG(levels, bins, orientation);

        detector.analyseImage(img);

        double[] descriptor = detector.getFeatureVector().values;

        return new Description(descriptor);
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public int getBins() {
        return bins;
    }

    public void setBins(int bins) {
        this.bins = bins;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }
}
