package me.builders;

import me.math.Calculator;

/**
 * An aggregator implementing the VLAD method to produce a fixed size vector
 * given a variant number of local descriptors extracted from a media item.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public class VladAggregator implements Aggregator {

    // A dictionary of media centroid words
    private double[][] codebook;

    /**
     * A constructor initiating the codebook consisting of a number of media
     * centroid words given after clustering upon the dataset.
     *
     * @param codebook the list of media centroid words.
     */
    public VladAggregator(double[][] codebook) {
        this.codebook = codebook;
    }

    /**
     * A method aggragates the given list of local descriptors extracted from a
     * media item into a fixed size vector using the vlad method.
     *
     * @param descriptors the list of local descriptors.
     * @return a fixed size vector.
     */
    @Override
    public double[] aggregate(double[][] descriptors) {
        double[] vlad = new double[codebook.length * codebook[0].length];

        // Accumulating the distances per descriptor
        for (double[] descriptor : descriptors) {
            // Finding the nearest centroid index of the descriptor
            int index = Calculator.findNearestCentroidIndex(descriptor, codebook);

            // Accumulating the distances from the nearest media centroid word
            for (int i = 0; i < descriptor.length; i++) {
                vlad[index * descriptor.length + i] += descriptor[i] - codebook[index][i];
            }
        }

        return vlad;
    }
}
