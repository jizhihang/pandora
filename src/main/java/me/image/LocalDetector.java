package me.image;

import java.awt.image.BufferedImage;

/**
 * An interface to implement a detector in order to extract local descriptors
 * from a given image.
 *
 * @author Akis Papadopoulos
 */
public interface LocalDetector {

    /**
     * A method detecting local descriptors given an image item.
     *
     * @param image the given image.
     * @return the list of local descriptors detected.
     * @throws Exception throws unknown error exceptions.
     */
    public double[][] detect(BufferedImage image) throws Exception;
}
