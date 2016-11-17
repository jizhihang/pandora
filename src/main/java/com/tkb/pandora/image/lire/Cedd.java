package com.tkb.pandora.image.lire;

import java.awt.image.BufferedImage;
import com.tkb.pandora.image.Description;
import com.tkb.pandora.image.FeatureDetector;
import com.tkb.pandora.math.Normalizer;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;

/**
 * A global detector extracting the color and edge directivity descriptor given
 * an image using the Lire library.
 *
 * @author Akis Papadopoulos
 */
public class Cedd implements FeatureDetector {

    // Threshold parameters
    private double t0;

    private double t1;

    private double t2;

    private double t3;

    // Compact form
    private boolean compact;

    // Normalization
    private boolean normalize;

    /**
     * A constructor initiating the default parameters.
     */
    public Cedd() {
        t0 = 14d;
        t1 = 0.68d;
        t2 = 0.98d;
        t3 = 0.98d;
        compact = false;
        normalize = false;
    }

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

        double[] descriptor = detector.getFeatureVector();

        if (normalize) {
            Normalizer.euclidean(descriptor);
        }

        return new Description(descriptor);
    }

    public double getT0() {
        return t0;
    }

    public void setT0(double t0) {
        this.t0 = t0;
    }

    public double getT1() {
        return t1;
    }

    public void setT1(double t1) {
        this.t1 = t1;
    }

    public double getT2() {
        return t2;
    }

    public void setT2(double t2) {
        this.t2 = t2;
    }

    public double getT3() {
        return t3;
    }

    public void setT3(double t3) {
        this.t3 = t3;
    }

    public boolean isCompact() {
        return compact;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }
}
