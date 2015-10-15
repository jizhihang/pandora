package me.aggregation;

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

    // Vocabulary codebook
    private Codebook codebook;

    /**
     * A constructor initiating the vocabulary codebook of centroid words.
     *
     * @param codebook the vocabulary codebook.
     */
    public VlatAggregator(Codebook codebook) {
        this.codebook = codebook;
    }

    /**
     * A method aggragates the given list of local descriptors extracted from a
     * media item into a fixed size vector.
     *
     * @param descriptors the list of local descriptors.
     * @return a fixed size vector.
     */
    @Override
    public double[] aggregate(double[][] descriptors) {
        // Getting the size of the codebook
        int k = codebook.getSize();

        // Getting the width of the codebook
        int d = codebook.getWidth();

        // Setting up the vlat descriptor size of kd+kd^2
        int size = (k * d) + (k * d * d);

        double[] vlat = new double[size];

        // Building a 2-term sized descriptor
        for (double[] descriptor : descriptors) {
            // Finding the nearest centroid index given the next descriptor
            int nnk = codebook.getNearestCentroidIndex(descriptor);

            for (int i = 0; i < descriptor.length; i++) {
                // Mapping index into row vector
                int index = (nnk * d) + (nnk * d * d) + i;

                double component_i = codebook.getComponent(nnk, i);

                // Setting the 1st term components size of 1d based on vlad method
                vlat[index] += descriptor[i] - component_i;

                for (int j = 0; j < descriptor.length; j++) {
                    // Mapping index into row vector
                    index = (nnk * d) + (nnk * d * d) + d + (i * d) + j;

                    double component_j = codebook.getComponent(nnk, j);

                    // Setting the 2nd term components size of dxd based on the self tensor product
                    vlat[index] += (descriptor[i] - component_i) * (descriptor[j] - component_j);
                }
            }
        }

        return vlat;
    }
}
