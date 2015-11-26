package me.pandora.image;

import java.awt.image.BufferedImage;

/**
 * An interface to implement a detector in order to extract a visual description
 * from a given image.
 *
 * @author Akis Papadopoulos
 */
public interface FeatureDetector {

    /**
     * A method detecting a visual description given an image item.
     *
     * @param image the given image.
     * @return the visual description detected.
     * @throws Exception throws unknown error exceptions.
     */
    public Description extract(BufferedImage image) throws Exception;
}
