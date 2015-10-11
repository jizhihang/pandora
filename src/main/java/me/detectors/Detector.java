package me.detectors;

/**
 * An interface to implement a detector in order to extract media content
 * descriptors.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public interface Detector {

    /**
     * A method takes a media file path and returns the detected descriptors.
     *
     * @param path the media file path.
     * @return the list of the detected descriptors.
     * @throws Exception throws unknown error exceptions.
     */
    public double[][] detect(String path) throws Exception;
}
