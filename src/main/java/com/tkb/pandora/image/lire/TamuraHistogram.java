package com.tkb.pandora.image.lire;

import java.awt.image.BufferedImage;
import com.tkb.pandora.image.Description;
import com.tkb.pandora.image.FeatureDetector;
import com.tkb.pandora.math.Normalizer;
import net.semanticmetadata.lire.imageanalysis.features.global.Tamura;

/**
 * A global detector extracting the tamura histogram descriptor given an image
 * using the Lire library.
 *
 * @author Akis Papadopoulos
 */
public class TamuraHistogram implements FeatureDetector {

    // Normalization
    private boolean normalize;

    /**
     * A constructor initiating the default parameters.
     */
    public TamuraHistogram() {
        normalize = false;
    }

    /**
     * A constructor creating a tamura histogram feature detector.
     *
     * @param normalize true to normalize otherwise false.
     */
    public TamuraHistogram(boolean normalize) {
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
        Tamura detector = new Tamura();

        detector.extract(image);

        double[] descriptor = detector.getFeatureVector();

        if (normalize) {
            Normalizer.euclidean(descriptor);
        }

        return new Description(descriptor);
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }
}
