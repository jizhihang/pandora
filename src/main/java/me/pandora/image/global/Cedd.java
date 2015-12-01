package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import net.semanticmetadata.lire.imageanalysis.CEDD;

/**
 * A global detector extracting the color and edge directivity descriptor given
 * an image using the Lire library.
 *
 * @author Akis Papadopoulos
 */
public class Cedd implements FeatureDetector {

    // Threshold parameters
    private double t0 = 14d;

    private double t1 = 0.68d;

    private double t2 = 0.98d;

    private double t3 = 0.98d;

    // Compact form
    private boolean compact = false;

    // Normalization
    private boolean normalize;

    /**
     * A constructor creating a CEDD feature detector given the threshold
     * parameters.
     *
     * @param t0 the first threshold.
     * @param t1 the second threshold.
     * @param t2 the third threshold.
     * @param t3 the fourth threshold.
     * @param compact true for compact form otherwise false.
     * @param normalize true to normalize otherwise false.
     */
    public Cedd(double t0, double t1, double t2, double t3, boolean compact, boolean normalize) {
        this.t0 = t0;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.compact = compact;
        this.normalize = normalize;
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
        CEDD detector = new CEDD(t0, t1, t2, t3, compact);

        detector.extract(image);

        double[] descriptor = detector.getDoubleHistogram();

        if (normalize) {
            Normalizer.euclidean(descriptor);
        }

        return new Description(descriptor);
    }

    @Override
    public String toString() {
        return "Cedd:144";
    }
}
