package me.pandora.image.local;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGColourSIFTEngine;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.engine.DoGSIFTEngineOptions;
import org.openimaj.image.feature.local.keypoints.Keypoint;

/**
 * A local detector extracting difference of Gaussian SIFT local descriptors
 * given an image using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class GaussianSift implements FeatureDetector {

    // Spatial bin size magnification factor
    private float magnificationFactor;

    // Number of orientation histogram bins
    private int numOriHistBins;

    // Gausian scaling weight of the orientation histogram
    private float scaling;

    // Smoothing filter iterations
    private int smoothingIterations;

    // Sampling window size
    private float samplingSize;

    // Orientation bins
    private int numOriBins;

    // Spatial bins in each direction
    private int numSpatialBins;

    // Maximum histogram value threshold
    private float valueThreshold;

    // Width of the Gaussian used for weighting samples
    private float gaussianSigma;

    // Color wise option
    private boolean colored;

    // Normalization option
    private boolean normalize;

    /**
     * A constructor initiating the default parameters.
     */
    public GaussianSift() {
        magnificationFactor = 3;
        numOriHistBins = 36;
        scaling = 1.5f;
        smoothingIterations = 6;
        samplingSize = 3.0f;
        numOriBins = 8;
        numSpatialBins = 4;
        valueThreshold = 0.2f;
        gaussianSigma = 1.0f;
        colored = false;
        normalize = false;
    }

    /**
     * A constructor initiating the given parameters.
     *
     * @param magnificationFactor the magnification factor.
     * @param numOriHistBins the number of orientation histogram bins.
     * @param scaling the Gaussian scaling weight.
     * @param smoothingIterations the smoothing filter iterations.
     * @param samplingSize the sampling window size.
     * @param numOriBins the number of orientation bins.
     * @param numSpatialBins the number of spatial bins.
     * @param valueThreshold the maximum histogram value threshold.
     * @param gaussianSigma the Gaussian width.
     * @param colored true for color wise features otherwise false.
     * @param normalize true to normalize otherwise false.
     */
    public GaussianSift(float magnificationFactor, int numOriHistBins, float scaling,
            int smoothingIterations, float samplingSize, int numOriBins, int numSpatialBins,
            float valueThreshold, float gaussianSigma, boolean colored, boolean normalize) {
        this.magnificationFactor = magnificationFactor;
        this.numOriHistBins = numOriHistBins;
        this.scaling = scaling;
        this.smoothingIterations = smoothingIterations;
        this.samplingSize = samplingSize;
        this.numOriBins = numOriBins;
        this.numSpatialBins = numSpatialBins;
        this.valueThreshold = valueThreshold;
        this.gaussianSigma = gaussianSigma;
        this.colored = colored;
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
        MBFImage img = ImageUtilities.createMBFImage(image, false);

        DoGSIFTEngineOptions opts = new DoGSIFTEngineOptions();
        opts.setMagnificationFactor(magnificationFactor);
        opts.setNumOriHistBins(numOriHistBins);
        opts.setScaling(scaling);
        opts.setSmoothingIterations(smoothingIterations);
        opts.setSamplingSize(samplingSize);
        opts.setNumOriBins(numOriBins);
        opts.setNumSpatialBins(numSpatialBins);
        opts.setValueThreshold(valueThreshold);
        opts.setGaussianSigma(gaussianSigma);

        LocalFeatureList<Keypoint> keypoints = null;

        if (colored) {
            DoGColourSIFTEngine engine = new DoGColourSIFTEngine(opts);

            keypoints = engine.findFeatures(img);
        } else {
            DoGSIFTEngine engine = new DoGSIFTEngine(opts);

            keypoints = engine.findFeatures(img.flatten());
        }

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

    public float getMagnificationFactor() {
        return magnificationFactor;
    }

    public void setMagnificationFactor(float magnificationFactor) {
        this.magnificationFactor = magnificationFactor;
    }

    public int getNumOriHistBins() {
        return numOriHistBins;
    }

    public void setNumOriHistBins(int numOriHistBins) {
        this.numOriHistBins = numOriHistBins;
    }

    public float getScaling() {
        return scaling;
    }

    public void setScaling(float scaling) {
        this.scaling = scaling;
    }

    public int getSmoothingIterations() {
        return smoothingIterations;
    }

    public void setSmoothingIterations(int smoothingIterations) {
        this.smoothingIterations = smoothingIterations;
    }

    public float getSamplingSize() {
        return samplingSize;
    }

    public void setSamplingSize(float samplingSize) {
        this.samplingSize = samplingSize;
    }

    public int getNumOriBins() {
        return numOriBins;
    }

    public void setNumOriBins(int numOriBins) {
        this.numOriBins = numOriBins;
    }

    public int getNumSpatialBins() {
        return numSpatialBins;
    }

    public void setNumSpatialBins(int numSpatialBins) {
        this.numSpatialBins = numSpatialBins;
    }

    public float getValueThreshold() {
        return valueThreshold;
    }

    public void setValueThreshold(float valueThreshold) {
        this.valueThreshold = valueThreshold;
    }

    public float getGaussianSigma() {
        return gaussianSigma;
    }

    public void setGaussianSigma(float gaussianSigma) {
        this.gaussianSigma = gaussianSigma;
    }

    public boolean isColored() {
        return colored;
    }

    public void setColored(boolean colored) {
        this.colored = colored;
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }
}
