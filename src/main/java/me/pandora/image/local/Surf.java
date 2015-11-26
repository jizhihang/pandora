package me.pandora.image.local;

import boofcv.abst.feature.detect.extract.ConfigExtract;
import boofcv.abst.feature.detect.extract.NonMaxSuppression;
import boofcv.abst.feature.orientation.ConfigSlidingIntegral;
import boofcv.abst.feature.orientation.OrientationIntegral;
import boofcv.alg.feature.describe.DescribePointSurf;
import boofcv.alg.feature.detect.interest.FastHessianFeatureDetector;
import boofcv.alg.transform.ii.GIntegralImageOps;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.factory.feature.describe.FactoryDescribePointAlgs;
import boofcv.factory.feature.detect.extract.FactoryFeatureExtractor;
import boofcv.factory.feature.orientation.FactoryOrientationAlgs;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import java.awt.image.BufferedImage;
import java.util.List;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;

/**
 * A local detector extracting stable grayscale SURF local descriptors given an
 * image using the BoofCV library.
 *
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/dtrW7I">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class Surf implements FeatureDetector {

    // Radius of the non-maximum region
    private int radius = 1;

    // Minimum feature intensity
    private float threshold = 0F;

    // Maximum number of returned features per scale, le to 0 returns all features finds
    private int maxFeaturesPerScale = -1;

    // How often pixels are sampled in the first octave
    private int initialSampleRate = 2;

    // Width of the smallest feature/kernel in the lowest octave
    private int initialSize = 9;

    // How many different feature sizes are considered in a single octave
    private int numberScalesPerOctave = 4;

    // How many different octaves are considered
    private int numberOfOctaves = 4;

    // Sliding orientation estimator mode
    private boolean slided = false;

    /**
     * A constructor initiating the given parameters.
     *
     * @param radius radius of the non-maximum region.
     * @param threshold minimum feature intensity.
     * @param maxFeaturesPerScale the maximum number of returned features per
     * scale, less equal to 0 returns all features finds.
     * @param initialSampleRate how often pixels are sampled in the first
     * octave.
     * @param initialSize width of the smallest feature/kernel in the lowest
     * octave.
     * @param numberScalesPerOctave different feature sizes are considered in a
     * single octave.
     * @param numberOfOctaves how many different octaves are considered.
     * @param slided true to enable sliding orientation estimator otherwise
     * false.
     */
    public Surf(int radius, float threshold, int maxFeaturesPerScale, int initialSampleRate, int initialSize, int numberScalesPerOctave, int numberOfOctaves, boolean slided) {
        this.radius = radius;
        this.threshold = threshold;
        this.maxFeaturesPerScale = maxFeaturesPerScale;
        this.initialSampleRate = initialSampleRate;
        this.initialSize = initialSize;
        this.numberScalesPerOctave = numberScalesPerOctave;
        this.numberOfOctaves = numberOfOctaves;
        this.slided = slided;
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

        // Defining a fast hessian feature detection algorithm
        ConfigExtract ce = new ConfigExtract(radius, threshold, 5, true);
        NonMaxSuppression extractor = FactoryFeatureExtractor.nonmax(ce);

        FastHessianFeatureDetector<ImageSingleBand> detector = new FastHessianFeatureDetector<ImageSingleBand>(extractor, maxFeaturesPerScale, initialSampleRate, initialSize, numberScalesPerOctave, numberOfOctaves);

        // Working off of integral images
        Class<ImageSingleBand> integralType = GIntegralImageOps.getIntegralType(ImageFloat32.class);

        // Setting up a sliding ii estimator algorithm for orientation
        ConfigSlidingIntegral csi = null;

        if (slided) {
            csi = new ConfigSlidingIntegral(0.65, Math.PI / 3.0, 8, -1, 6);
        }

        OrientationIntegral<ImageSingleBand> orientator = FactoryOrientationAlgs.sliding_ii(null, integralType);

        // Setting up stability SURF feature describer algorithm
        DescribePointSurf<ImageSingleBand> describer = FactoryDescribePointAlgs.<ImageSingleBand>surfStability(null, integralType);

        // Computing the integral image of the image
        ImageSingleBand integral = GeneralizedImageOps.createSingleBand(integralType, grayscale.width, grayscale.height);

        // Transforming image into integral
        GIntegralImageOps.transform(grayscale, integral);

        // Detecting fast hessian features
        detector.detect(integral);

        // Telling algorithms which image to process
        orientator.setImage(integral);
        describer.setImage(integral);

        // Finding the interest points
        List<ScalePoint> points = detector.getFoundPoints();

        // Checking if no interest points detected within image
        if (points.isEmpty()) {
            throw new Exception("No local grayscale SURF descriptors detected for the given image");
        }

        // Extracting descriptors iterating through scale points
        double[][] descriptors = new double[points.size()][];

        for (int i = 0; i < points.size(); i++) {
            // Estimating orientation of the next point
            ScalePoint p = points.get(i);

            orientator.setScale(p.scale);
            double angle = orientator.compute(p.x, p.y);

            // Extracting next descriptor for this region
            SurfFeature descriptor = describer.createDescription();
            describer.describe(p.x, p.y, angle, p.scale, descriptor);

            descriptors[i] = descriptor.value;
        }

        return new Description(descriptors);
    }
}
