package me.detectors.image;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.Surf;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import me.detectors.Detector;

/**
 * A detector extracting SURF local descriptors given an image using the
 * JOpenSurf library.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
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
     * @param balance hessian balance value.
     * @param threshold hessian threshold.
     * @param octaves hessian octaves.
     * @param oriented points orientation option.
     */
    public OpenSurfDetector(float balance, float threshold, int octaves, boolean oriented) {
        this.balance = balance;
        this.threshold = threshold;
        this.octaves = octaves;
        this.oriented = oriented;
    }

    /**
     * A method takes an image file path and returns the detected local SURF
     * descriptors.
     *
     * @param path the image file path.
     * @return the list of the detected local descriptors.
     * @throws Exception throws unknown error exceptions.
     */
    @Override
    public double[][] detect(String path) throws Exception {
        // Reading the image
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);

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
            throw new Exception("No local SURF descriptors detected within, '" + path + "'.");
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
