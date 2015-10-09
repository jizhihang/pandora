package me.builders;

/**
 * An interface to aggregate local descriptors extracted from a single media item
 * into a fixed size vector.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public interface Aggregator {

    /**
     * A method aggregates local descriptors into a fixed size vector.
     *
     * @param descriptors the list of local descriptors.
     * @return a fixed size vector.
     */
    public double[] aggregate(double[][] descriptors);
}
