package me.detectors.image;

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
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import java.util.List;
import me.detectors.Detector;

/**
 * A detector extracting stable SURF local descriptors given an image using the
 * BoofCV library.
 *
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/dtrW7I">more</a>.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public class SurfDetector implements Detector {

    // Radius of the non-maximum region
    private int radius;

    // Minimum feature intensity
    private float threshold;

    // Number of features, 0 returns all features finds
    private int features;

    // How often pixels are sampled in the first octave
    private int rate;

    // Width of the smallest feature/kernel in the lowest octave
    private int size;

    // How many different feature sizes are considered in a single octave
    private int scales;

    // How many different octaves are considered
    private int octaves;

    /**
     * A constructor initiating the default parameters.
     */
    public SurfDetector() {
        this.radius = 2;
        this.threshold = 0F;
        this.features = 200;
        this.rate = 2;
        this.size = 9;
        this.scales = 4;
        this.octaves = 4;
    }

    /**
     * A constructor initiating the given parameters.
     *
     * @param radius radius of the non-maximum region.
     * @param threshold minimum feature intensity.
     * @param features number of features.
     * @param rate how often pixels are sampled in the first octave.
     * @param size width of the smallest feature/kernel in the lowest octave.
     * @param scales different feature sizes are considered in a single octave.
     * @param octaves how many different octaves are considered.
     */
    public SurfDetector(int radius, float threshold, int features, int rate, int size, int scales, int octaves) {
        this.radius = radius;
        this.threshold = threshold;
        this.features = features;
        this.rate = rate;
        this.size = size;
        this.scales = scales;
        this.octaves = octaves;
    }

    /**
     * A method takes an image file path and returns the detected local
     * descriptors.
     *
     * @param path the image file path.
     * @return a list of local descriptors.
     * @throws Exception throws unknown error exceptions.
     */
    @Override
    public double[][] detect(String path) throws Exception {
        // Setting up image representation
        ImageFloat32 image = UtilImageIO.loadImage(path, ImageFloat32.class);

        // Working off of integral images
        Class<ImageSingleBand> integralType = GIntegralImageOps.getIntegralType(ImageFloat32.class);

        // Defining a fast hessian feature detection algorithm
        ConfigExtract ce = new ConfigExtract(radius, threshold, 5, true);
        NonMaxSuppression extractor = FactoryFeatureExtractor.nonmax(ce);

        FastHessianFeatureDetector<ImageSingleBand> detector = new FastHessianFeatureDetector<ImageSingleBand>(extractor, features, rate, size, scales, octaves);

        // Setting up a sliding ii estimator algorithm for orientation
        ConfigSlidingIntegral csi = new ConfigSlidingIntegral(0.65, Math.PI / 3.0, 8, -1, 6);
        OrientationIntegral<ImageSingleBand> orientator = FactoryOrientationAlgs.sliding_ii(csi, integralType);

        // Setting up stability SURF feature describer algorithm
        DescribePointSurf<ImageSingleBand> describer = FactoryDescribePointAlgs.<ImageSingleBand>surfStability(null, integralType);

        // Computing the integral image of the image
        ImageSingleBand integral = GeneralizedImageOps.createSingleBand(integralType, image.width, image.height);

        // Transforming image into integral
        GIntegralImageOps.transform(image, integral);

        // Detecting fast hessian features
        detector.detect(integral);

        // Telling algorithms which image to process
        orientator.setImage(integral);
        describer.setImage(integral);

        // Finding the interest points
        List<ScalePoint> points = detector.getFoundPoints();

        // Checking if no interest points detected within image
        if (points.isEmpty()) {
            throw new Exception("No local surfm descriptors detected within, '" + path + "'.");
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

        return descriptors;
    }
}
