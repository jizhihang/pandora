package me.pandora.image.global;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.pixel.statistics.HistogramModel;

/**
 * A global detector extracting the color histogram descriptor given an image
 * using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class ColorHistogram implements FeatureDetector {

    // Number of bins per color
    private int bins;

    // Normalization
    private boolean normalize;

    /**
     * A constructor creating a color histogram feature detector.
     *
     * @param bins the number of bins per RGB color.
     * @param normalize true to normalize otherwise false.
     */
    public ColorHistogram(int bins, boolean normalize) {
        this.bins = bins;
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
        MBFImage img = ImageUtilities.createMBFImage(image, true);

        HistogramModel model = new HistogramModel(bins, bins, bins);

        model.estimateModel(img);

        double[] descriptor = model.histogram.values;

        if (normalize) {
            Normalizer.euclidean(descriptor);
        }

        return new Description(descriptor);
    }

    @Override
    public String toString() {
        return "ColorHistogram:" + (bins * bins * bins);
    }
}
