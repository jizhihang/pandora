package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import net.semanticmetadata.lire.imageanalysis.ScalableColor;

/**
 * A global detector extracting the MPEG-7 scalable color descriptor given an
 * image using the Lire library.
 *
 * @author Akis Papadopoulos
 */
public class ColorScale implements FeatureDetector {

    // Normalization
    private boolean normalize;

    /**
     * A constructor creating a scalable color feature detector.
     *
     * @param normalize true to normalize otherwise false.
     */
    public ColorScale(boolean normalize) {
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
        ScalableColor detector = new ScalableColor();

        detector.extract(image);

        double[] descriptor = detector.getDoubleHistogram();

        if (normalize) {
            Normalizer.euclidean(descriptor);
        }

        return new Description(descriptor);
    }
}
