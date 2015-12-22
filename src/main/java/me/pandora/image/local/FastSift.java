package me.pandora.image.local;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.feature.dense.gradient.dsift.ApproximateDenseSIFT;
import org.openimaj.image.feature.dense.gradient.dsift.FloatDSIFTKeypoint;

/**
 * A local detector extracting approximate upright dense SIFT local descriptors
 * given an image using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class FastSift implements FeatureDetector {

    // Step size of sampling window in x-direction in pixels
    private int stepX;

    // Step size of sampling window in y-direction in pixels
    private int stepY;

    // Width of a single bin of the sampling window in pixels
    private int binWidth;

    // Height of a single bin of the sampling window in pixels
    private int binHeight;

    // Number of spatial bins in the x-direction
    private int numBinsX;

    // Number of spatial bins in the y-direction
    private int numBinsY;

    // Number of orientation bins
    private int numOriBins;

    // Size of the Gaussian window
    private float gaussianWindowSize;

    // Threshold for clipping the features
    private float valueThreshold;

    // Normalization option
    private boolean normalize;

    /**
     * A constructor initiating the default parameters.
     */
    public FastSift() {
        stepX = 5;
        stepY = 5;
        binWidth = 5;
        binHeight = 5;
        numBinsX = 4;
        numBinsY = 4;
        numOriBins = 8;
        gaussianWindowSize = 2f;
        valueThreshold = 0.2f;
        normalize = false;
    }

    /**
     * A constructor initiating given the configuration parameters.
     *
     * @param stepX step size in x direction.
     * @param stepY step size in y direction.
     * @param binWidth width of spatial bins.
     * @param binHeight height of spatial bins.
     * @param numBinsX number of bins in x direction for each descriptor.
     * @param numBinsY number of bins in y direction for each descriptor.
     * @param numOriBins number of orientation bins for each descriptor.
     * @param gaussianWindowSize the size of the Gaussian weighting window.
     * @param valueThreshold the threshold for clipping features.
     * @param normalize true to normalize otherwise false.
     */
    public FastSift(int stepX, int stepY, int binWidth, int binHeight, int numBinsX,
            int numBinsY, int numOriBins, float gaussianWindowSize, float valueThreshold, boolean normalize) {
        this.stepX = stepX;
        this.stepY = stepY;
        this.binWidth = binWidth;
        this.binHeight = binHeight;
        this.numBinsX = numBinsX;
        this.numBinsY = numBinsY;
        this.numOriBins = numOriBins;
        this.gaussianWindowSize = gaussianWindowSize;
        this.valueThreshold = valueThreshold;
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

        ApproximateDenseSIFT detector = new ApproximateDenseSIFT(stepX, stepY, binWidth, binHeight, numBinsX,
                numBinsY, numOriBins, gaussianWindowSize, valueThreshold);

        detector.analyseImage(img);

        LocalFeatureList<FloatDSIFTKeypoint> keypoints = detector.getFloatKeypoints();

        // Checking if no interest points detected within image
        if (keypoints.size() <= 0) {
            throw new Exception("No local SIFT descriptors detected for the given image");
        }

        double[][] descriptors = new double[keypoints.size()][];

        for (int i = 0; i < keypoints.size(); i++) {
            double[] descriptor = keypoints.get(i).getFeatureVector().asDoubleVector();

            // Normalizing the local descriptor
            if (normalize) {
                Normalizer.euclidean(descriptor);
            }

            descriptors[i] = descriptor;
        }

        return new Description(descriptors);
    }

    public int getStepX() {
        return stepX;
    }

    public void setStepX(int stepX) {
        this.stepX = stepX;
    }

    public int getStepY() {
        return stepY;
    }

    public void setStepY(int stepY) {
        this.stepY = stepY;
    }

    public int getBinWidth() {
        return binWidth;
    }

    public void setBinWidth(int binWidth) {
        this.binWidth = binWidth;
    }

    public int getBinHeight() {
        return binHeight;
    }

    public void setBinHeight(int binHeight) {
        this.binHeight = binHeight;
    }

    public int getNumBinsX() {
        return numBinsX;
    }

    public void setNumBinsX(int numBinsX) {
        this.numBinsX = numBinsX;
    }

    public int getNumBinsY() {
        return numBinsY;
    }

    public void setNumBinsY(int numBinsY) {
        this.numBinsY = numBinsY;
    }

    public int getNumOriBins() {
        return numOriBins;
    }

    public void setNumOriBins(int numOriBins) {
        this.numOriBins = numOriBins;
    }

    public float getGaussianWindowSize() {
        return gaussianWindowSize;
    }

    public void setGaussianWindowSize(float gaussianWindowSize) {
        this.gaussianWindowSize = gaussianWindowSize;
    }

    public float getValueThreshold() {
        return valueThreshold;
    }

    public void setValueThreshold(float valueThreshold) {
        this.valueThreshold = valueThreshold;
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }
}
