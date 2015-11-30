package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import net.semanticmetadata.lire.imageanalysis.Tamura;

/**
 * A global detector extracting the tamura directionality descriptor given an
 * image using the Lire library.
 *
 * @author Akis Papadopoulos
 */
public class TamuraOrientation implements FeatureDetector {

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

        double[] descriptor = detector.directionality();

        return new Description(descriptor);
    }

    @Override
    public String toString() {
        return "Tamura:16";
    }
}
