package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.global.LuoSimplicity;

/**
 * A global detector extracting the Luo Simplicity descriptor as the color
 * distribution of the background given an image using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class Luo implements FeatureDetector {

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

        LuoSimplicity detector = new LuoSimplicity();

        detector.analyseImage(img);

        double[] descriptor = detector.getFeatureVector().values;

        return new Description(descriptor);
    }

    @Override
    public String toString() {
        return "Luo:1";
    }
}
