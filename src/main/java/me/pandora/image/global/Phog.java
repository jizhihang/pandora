package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import net.semanticmetadata.lire.imageanalysis.PHOG;

/**
 * A global detector extracting the phog histogram descriptor given an image
 * using the Lire library.
 *
 * @author Akis Papadopoulos
 */
public class Phog implements FeatureDetector {

    // Normalization
    private boolean normalize;

    /**
     * A constructor creating a PHOG feature detector.
     *
     * @param normalize true to normalize otherwise false.
     */
    public Phog(boolean normalize) {
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
        PHOG detector = new PHOG();

        detector.extract(image);

        double[] descriptor = detector.getDoubleHistogram();

        if (normalize) {
            Normalizer.euclidean(descriptor);
        }

        return new Description(descriptor);
    }

    @Override
    public String toString() {
        return "Phog:630";
    }
}
