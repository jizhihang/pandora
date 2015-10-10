package me.builders;

import me.math.Calculator;

/**
 * An aggregator implementing the bags of words (bow) method to produce a fixed
 * size vector given a variant number of local descriptors extracted from a
 * media item.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public class BowAggregator implements Aggregator {

    // A dictionary of media centroid words
    private double[][] codebook;

    /**
     * A constructor initiating the codebook consisting of a number of media
     * centroid words given after clustering upon the dataset.
     *
     * @param codebook the list of media centroid words.
     */
    public BowAggregator(double[][] codebook) {
        this.codebook = codebook;
    }

    /**
     * A method aggragates the given list of local descriptors extracted from a
     * media item into a fixed size vector using the bow method.
     *
     * @param descriptors the list of local descriptors.
     * @return a fixed size vector.
     */
    @Override
    public double[] aggregate(double[][] descriptors) {
        double[] bow = new double[codebook.length];

        // Building a histogram of media centroid word frequencies
        for (double[] descriptor : descriptors) {
            // Incresing the nearest centroid frequency given the descriptor
            int index = Calculator.computeNearestCentroidIndex(descriptor, codebook);
            
            bow[index]++;
        }

        return bow;
    }
}
