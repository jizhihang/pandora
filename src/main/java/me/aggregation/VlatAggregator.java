package me.aggregation;

import me.math.Calculator;

/**
 * An aggregator implementing the vlat method to produce a fixed size vector
 * given a variant number of local descriptors extracted from a media item.
 *
 * See more:<br/>
 * <em>R. Negrel, D. Picard and P.H. Gosselin, Compact tensor based image
 * representation for similarity search, 19th IEEE International Conference on
 * Image Processing (ICIP), 2012.</em>
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public class VlatAggregator implements Aggregator {

    // A dictionary of media centroid words
    private double[][] codebook;

    /**
     * A constructor initiating the codebook consisting of a number of media
     * centroid words given after clustering upon the dataset.
     *
     * @param codebook the list of media centroid words.
     */
    public VlatAggregator(double[][] codebook) {
        this.codebook = codebook;
    }

    /**
     * A method aggragates the given list of local descriptors extracted from a
     * media item into a fixed size vector using the vlat method.
     *
     * @param descriptors the list of local descriptors.
     * @return a fixed size vector.
     */
    @Override
    public double[] aggregate(double[][] descriptors) {
        // Reading the number of media centroid words, clusters
        int k = codebook.length;

        // Reading the size of the descriptor
        int d = codebook[0].length;

        // Setting up the vlat descriptor size of kd+kd^2
        int size = (k * d) + (k * d * d);

        double[] vlat = new double[size];

        // Building a 2-term sized descriptor
        for (double[] descriptor : descriptors) {
            // Finding the nearest centroid index given the next descriptor
            int nnk = Calculator.computeNearestCentroidIndex(descriptor, codebook);

            for (int i = 0; i < descriptor.length; i++) {
                // Mapping index into row vector
                int index = (nnk * d) + (nnk * d * d) + i;

                // Setting the 1st term components size of 1d based on vlad method
                vlat[index] += descriptor[i] - codebook[nnk][i];

                for (int j = 0; j < descriptor.length; j++) {
                    // Mapping index into row vector
                    index = (nnk * d) + (nnk * d * d) + d + (i * d) + j;

                    // Setting the 2nd term components size of dxd based on the self tensor product
                    vlat[index] += (descriptor[i] - codebook[nnk][i]) * (descriptor[j] - codebook[nnk][j]);
                }
            }
        }

        return vlat;
    }
}
