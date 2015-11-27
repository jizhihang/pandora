package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import net.semanticmetadata.lire.imageanalysis.ColorLayout;

/**
 * A global detector extracting the MPEG-7 color layout descriptor given an
 * image using the Lire library.
 *
 * @author Akis Papadopoulos
 */
public class ColorLayoutHistogram implements FeatureDetector {

    /**
     * A method detecting a visual description given an image item.
     *
     * @param image the image item.
     * @return the visual description detected.
     * @throws Exception throws unknown error exceptions.
     */
    @Override
    public Description extract(BufferedImage image) throws Exception {
        ColorLayout detector = new ColorLayout();

        detector.extract(image);

        double[] descriptor = detector.getDoubleHistogram();

        return new Description(descriptor);
    }
    
    @Override
    public String toString() {
        return "ColorLayout:33";
    }
}
