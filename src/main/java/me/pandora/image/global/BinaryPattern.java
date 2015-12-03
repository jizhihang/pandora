package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import me.pandora.util.ArrayOps;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.feature.dense.binarypattern.BasicLocalBinaryPattern;

/**
 * A global detector extracting the Local Binary Pattern (Texture) descriptor
 * given an image using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class BinaryPattern implements FeatureDetector {

    // Normalization
    private boolean normalize = true;

    /**
     * A constructor creating a Binary Pattern feature detector given the
     * parameters.
     *
     * @param normalize true for normalization otherwise false.
     */
    public BinaryPattern(boolean normalize) {
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
        FImage img = ImageUtilities.createFImage(image);

        BasicLocalBinaryPattern detector = new BasicLocalBinaryPattern();

        detector.analyseImage(img);

        int[][] pattern = detector.getPattern();

        double[] descriptor = ArrayOps.flatten(pattern);

        if (normalize) {
            Normalizer.euclidean(descriptor);
        }

        return new Description(descriptor);
    }
}
