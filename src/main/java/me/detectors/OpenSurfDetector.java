package me.detectors;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.Surf;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A detector extracting SURF local descriptors given an image using the
 * JOpenSurf library.
 *
 * @author Akis Papadopoulos
 */
public class OpenSurfDetector implements Detector {

    // Hessian balance value 
    private float balance;

    // Hessian threshold
    private float threshold;

    // Hessian octaves
    private int octaves;

    // Points orientation option
    private boolean oriented;

    /**
     * A constructor initiating the default parameters.
     */
    public OpenSurfDetector() {
        this.balance = 0.81F;
        this.threshold = 0.0004F;
        this.octaves = 5;
        this.oriented = false;
    }

    /**
     * A constructor initiating the given parameters.
     *
     * @param balance the hessian balance value.
     * @param threshold the hessian threshold.
     * @param octaves the hessian octaves.
     * @param oriented the points orientation option.
     */
    public OpenSurfDetector(float balance, float threshold, int octaves, boolean oriented) {
        this.balance = balance;
        this.threshold = threshold;
        this.octaves = octaves;
        this.oriented = oriented;
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
        // Detecting local descriptors
        Surf detector = new Surf(image, balance, threshold, octaves);

        List<SURFInterestPoint> points = null;

        // Getting upright interest points or orientation free
        if (oriented) {
            points = detector.getUprightInterestPoints();
        } else {
            points = detector.getFreeOrientedInterestPoints();
        }

        // Checking if no descriptors detected
        if (points.isEmpty()) {
            throw new Exception("No local SURF descriptors detected for the given image.");
        }

        // Setting up the descriptors list, row based
        double[][] descriptors = new double[points.size()][];

        // Iterating through interest points detected
        for (int i = 0; i < points.size(); i++) {
            SURFInterestPoint point = points.get(i);

            // Adding next descriptor to the list
            float[] components = point.getDescriptor();

            double[] descriptor = new double[components.length];
            for (int j = 0; j < components.length; j++) {
                descriptor[j] = components[j];
            }

            descriptors[i] = descriptor;
        }

        return descriptors;
    }
}
