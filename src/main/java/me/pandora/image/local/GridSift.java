package me.pandora.image.local;

import java.awt.image.BufferedImage;
import me.pandora.image.Description;
import me.pandora.image.FeatureDetector;
import me.pandora.math.Normalizer;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.BasicGridSIFTEngine;
import org.openimaj.image.feature.local.engine.DoGSIFTEngineOptions;
import org.openimaj.image.feature.local.keypoints.Keypoint;

/**
 * A local detector extracting basic grid SIFT local descriptors given an image
 * using the OpenIMAJ library.
 *
 * @author Akis Papadopoulos
 */
public class GridSift implements FeatureDetector {

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

    // Oriented or upright option
    private boolean oriented;

    // Normalization option
    private boolean normalize;

    /**
     * A constructor initiating the default parameters.
     */
    public GridSift() {
        magnificationFactor = 3;
        numOriHistBins = 36;
        scaling = 1.5f;
        smoothingIterations = 6;
        samplingSize = 3.0f;
        numOriBins = 8;
        numSpatialBins = 4;
        valueThreshold = 0.2f;
        gaussianSigma = 1.0f;
        oriented = false;
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
     * @param oriented true for oriented features otherwise false for upright.
     * @param normalize true to normalize otherwise false.
     */
    public GridSift(float magnificationFactor, int numOriHistBins, float scaling,
            int smoothingIterations, float samplingSize, int numOriBins, int numSpatialBins,
            float valueThreshold, float gaussianSigma, boolean oriented, boolean normalize) {
        this.magnificationFactor = magnificationFactor;
        this.numOriHistBins = numOriHistBins;
        this.scaling = scaling;
        this.smoothingIterations = smoothingIterations;
        this.samplingSize = samplingSize;
        this.numOriBins = numOriBins;
        this.numSpatialBins = numSpatialBins;
        this.valueThreshold = valueThreshold;
        this.gaussianSigma = gaussianSigma;
        this.oriented = oriented;
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

        BasicGridSIFTEngine engine = engine = new BasicGridSIFTEngine(oriented, opts);

        LocalFeatureList<Keypoint> keypoints = engine.findFeatures(img.flatten());

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
}
