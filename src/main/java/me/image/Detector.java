package me.image;

import java.awt.image.BufferedImage;

/**
 * An interface to implement a detector in order to extract visual content
 * descriptors from a given image.
 *
 * @author Akis Papadopoulos
 */
public interface Detector {

    /**
     * A method detecting visual descriptors given an image item.
     *
     * @param image the image item.
     * @return the list of visual descriptors detected.
     * @throws Exception throws unknown error exceptions.
     */
    public double[][] detect(BufferedImage image) throws Exception;
}
