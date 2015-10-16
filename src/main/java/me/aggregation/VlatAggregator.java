package me.aggregation;

import me.math.EuclideanNormalizer;
import me.math.Normalizer;
import me.math.PowerNormalizer;

/**
 * An aggregator implementing the vlat method to produce a fixed size normalized
 * (power, l2) vector given a variant number of local descriptors extracted from
 * a media item, supporting single or multiple vocabulary vectorization. In case
 * of multiple vocabularies each sub bow vector generated independently from
 * each vocabulary and then concatenated in a single vector.
 *
 * See more about vlat method:<br/>
 * <em>R. Negrel, D. Picard and P.H. Gosselin, Compact tensor based image
 * representation for similarity search, 19th IEEE International Conference on
 * Image Processing (ICIP), 2012.</em>
 *
 * See more about multiple vocabularies:
 * <em>Jégou, H., & Chum, O. (2012). Negative evidences and co-occurences in
 * image retrieval: The benefit of PCA and whitening. In ECCV 2012.</em>
 *
 * @author Akis Papadopoulos
 */
public class VlatAggregator implements Aggregator {

    // Vocabulary codebooks
    private Codebook[] codebooks;

    // Normalization
    private boolean normalize = true;

    // Power normalizer
    Normalizer power;

    // Euclidean normalizer
    Normalizer euclidean;

    /**
     * A constructor initiating the vocabulary codebooks of centroid words plus
     * the normalization option.
     *
     * @param codebooks the vocabulary codebooks.
     * @param normalize the option to normalize.
     */
    public VlatAggregator(Codebook[] codebooks, boolean normalize) {
        this.codebooks = codebooks;

        this.normalize = normalize;

        power = new PowerNormalizer();
        euclidean = new EuclideanNormalizer();
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
        // Calculating the final vector size
        int size = 0;

        // Regarding the number of codebooks their size and width
        for (Codebook codebook : codebooks) {
            int k = codebook.getSize();
            int d = codebook.getWidth();

            size += (k * d) + (k * d * d);
        }

        double[] vlat = new double[size];

        int offset = 0;

        // Regarding each codebook
        for (Codebook codebook : codebooks) {
            // Building the vlat subvector for the next codebook
            int k = codebook.getSize();
            int d = codebook.getWidth();

            double[] subvector = new double[(k * d) + (k * d * d)];

            // Building a 2-term sized descriptor
            for (double[] descriptor : descriptors) {
                // Finding the nearest centroid index given the next descriptor
                int nnk = codebook.getNearestCentroidIndex(descriptor);

                for (int i = 0; i < descriptor.length; i++) {
                    // Mapping index into row vector
                    int index = (nnk * d) + (nnk * d * d) + i;

                    double component_i = codebook.getComponent(nnk, i);

                    // Setting the 1st term components size of 1d based on vlad method
                    subvector[index] += descriptor[i] - component_i;

                    for (int j = 0; j < descriptor.length; j++) {
                        // Mapping index into row vector
                        index = (nnk * d) + (nnk * d * d) + d + (i * d) + j;

                        double component_j = codebook.getComponent(nnk, j);

                        // Setting the 2nd term components size of dxd based on the self tensor product
                        subvector[index] += (descriptor[i] - component_i) * (descriptor[j] - component_j);
                    }
                }
            }

            // Normalize using Power and Euclidean l2 norms
            if (normalize) {
                power.normalize(vlat);
                euclidean.normalize(vlat);
            }

            // Concatenate the subvector
            System.arraycopy(subvector, 0, vlat, offset, subvector.length);
            offset += subvector.length;
        }

        // Normalizing final vector only in case of multiple vocabularies
        if (codebooks.length > 1 && normalize) {
            euclidean.normalize(vlat);
        }

        return vlat;
    }
}
