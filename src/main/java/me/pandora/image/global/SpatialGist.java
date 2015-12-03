package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.global.Gist;

/**
 * A global detector extracting the Gist or Spatial Envelop descriptor given an
 * image using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class SpatialGist implements FeatureDetector {

    // Normalization
    private boolean normalize;

    /**
     * A constructor creating a spatial gist feature detector.
     *
     * @param normalize true to normalize otherwise false.
     */
    public SpatialGist(boolean normalize) {
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
        MBFImage img = ImageUtilities.createMBFImage(image, false);

        Gist detector = new Gist(image.getWidth(), image.getHeight());

        detector.analyseImage(img);

        double[] descriptor = detector.getResponse().asDoubleVector();

        if (normalize) {
            Normalizer.euclidean(descriptor);
        }

        return new Description(descriptor);
    }
}
