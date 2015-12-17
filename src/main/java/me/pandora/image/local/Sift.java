package me.pandora.image.local;

import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigSiftDetector;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.image.ImageFloat32;
import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;

/**
 * A local detector extracting SIFT grayscale local descriptors given an image
 * using the BoofCV library.
 *
 * This class is a modification of a class written by Eleftherios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/c4vwMo">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class Sift implements FeatureDetector {

    // Feature size used to detect the corners
    private int extractRadius;

    // Minimum corner intensity required
    private float detectThreshold;

    // Max detected features per scale
    private int maxFeaturesPerScale;

    // Edge filtering threshold
    private double edgeThreshold;

    // Normalization option
    private boolean normalize;
    
    /**
     * A constructor initiating the default parameters.
     */
    public Sift() {
        extractRadius = 2;
        detectThreshold = 1;
        maxFeaturesPerScale = -1;
        edgeThreshold = 5;
        normalize = false;
    }

    /**
     * A constructor initiating the given parameters.
     *
     * @param extractRadius the feature size used to detect the corners.
     * @param detectThreshold the minimum corner intensity required.
     * @param maxFeaturesPerScale the maximum detected features per scale.
     * @param edgeThreshold the edge filtering threshold.
     * @param normalize the normalization option.
     */
    public Sift(int extractRadius, float detectThreshold, int maxFeaturesPerScale, double edgeThreshold, boolean normalize) {
        this.extractRadius = extractRadius;
        this.detectThreshold = detectThreshold;
        this.maxFeaturesPerScale = maxFeaturesPerScale;
        this.edgeThreshold = edgeThreshold;
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
        // Setting up image representation
        ImageFloat32 grayscale = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);

        // Creating the SIFT detector
        ConfigSiftDetector csd = new ConfigSiftDetector(extractRadius, detectThreshold, maxFeaturesPerScale, edgeThreshold);
        DetectDescribePoint<ImageFloat32, SurfFeature> describer = FactoryDetectDescribe.sift(null, csd, null, null);

        // Detecting local descriptors
        describer.detect(grayscale);

        int numPoints = describer.getNumberOfFeatures();

        // Checking if no interest points detected within image
        if (numPoints <= 0) {
            throw new Exception("No local SIFT descriptors detected for the given image");
        }

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

        return new Description(descriptors);
    }
}
