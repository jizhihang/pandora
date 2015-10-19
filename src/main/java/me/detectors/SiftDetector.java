package me.detectors;

import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigSiftDetector;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.image.ImageFloat32;
import java.awt.image.BufferedImage;
import me.math.Normalizer;

/**
 * A detector extracting SIFT grayscale local descriptors given an image using
 * the BoofCV library.
 *
 * This class is a modification of a class written by Eleftherios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/c4vwMo">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class SiftDetector implements Detector {

    // Feature size used to detect the corners
    private int extractRadius = 2;

    // Minimum corner intensity required
    private float detectThreshold = 1;

    // Max detected features per scale
    private int maxFeaturesPerScale = -1;

    // Edge filtering threshold
    private double edgeThreshold = 5;

    // Normalization option
    private boolean normalize = false;

    /**
     * A constructor initiating the given parameters.
     *
     * @param extractRadius the feature size used to detect the corners.
     * @param detectThreshold the minimum corner intensity required.
     * @param maxFeaturesPerScale the maximum detected features per scale.
     * @param edgeThreshold the edge filtering threshold.
     * @param normalize the normalization option.
     */
    public SiftDetector(int extractRadius, float detectThreshold, int maxFeaturesPerScale, double edgeThreshold, boolean normalize) {
        this.extractRadius = extractRadius;
        this.detectThreshold = detectThreshold;
        this.maxFeaturesPerScale = maxFeaturesPerScale;
        this.edgeThreshold = edgeThreshold;
        this.normalize = normalize;
    }

    /**
     * A method detecting visual descriptors given an image item.
     *
     * @param image the image item.
     * @return the list of visual descriptors detected.
     * @throws Exception throws unknown error exceptions.
     */
    @Override
    public double[][] detect(BufferedImage image) throws Exception {
        // Setting up image representation
        ImageFloat32 grayscale = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);

        // Creating the SIFT detector
        ConfigSiftDetector csd = new ConfigSiftDetector(extractRadius, detectThreshold, maxFeaturesPerScale, edgeThreshold);
        DetectDescribePoint<ImageFloat32, SurfFeature> describer = FactoryDetectDescribe.sift(null, csd, null, null);

        // Detecting local descriptors
        describer.detect(grayscale);

        int numPoints = describer.getNumberOfFeatures();

        double[][] descriptors = new double[numPoints][];

        for (int i = 0; i < numPoints; i++) {
            double[] descriptor = describer.getDescription(i).getValue();

            // Normalizing the local descriptor
            if (normalize) {
                Normalizer.power(descriptor, 0.5);
                Normalizer.euclidean(descriptor);
            }

            descriptors[i] = descriptor;
        }

        return descriptors;
    }
}
