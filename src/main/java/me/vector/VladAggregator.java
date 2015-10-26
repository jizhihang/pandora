package me.vector;

import me.math.Normalizer;

/**
 * An aggregator implementing the vlad method to produce a fixed size normalized
 * (power, l2) vector given a variant number of local descriptors extracted from
 * a media item, supporting single or multiple vocabulary vectorization. In case
 * of multiple vocabularies each sub vlad vector generated independently from
 * each vocabulary and then concatenated in a single vector.
 *
 * See more about vlad method:<br/>
 * <em>H. Jegou, F. Perronnin, M. Douze, J. Sanchez, P. Perez, and C. Schmid,
 * Aggregating local image descriptors into compact codes, IEEE Transactions on
 * Pattern Analysis and Machine Intelligence, 2012.</em>
 *
 * See more about multiple vocabularies:
 * <em>Jégou, H., & Chum, O. (2012). Negative evidences and co-occurences in
 * image retrieval: The benefit of PCA and whitening. In ECCV 2012.</em>
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/xiSHRJ">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class VladAggregator implements Aggregator {

    // Vocabulary codebooks
    private Codebook[] codebooks;

    // Normalization
    private boolean normalize = true;

    /**
     * A constructor initiating the vocabulary codebooks of centroid words plus
     * the normalization option. Be aware codebooks order matters.
     *
     * @param codebooks the vocabulary codebooks.
     * @param normalize the normalization option.
     */
    public VladAggregator(Codebook[] codebooks, boolean normalize) {
        this.codebooks = codebooks;

        this.normalize = normalize;
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
            size += codebook.getSize() * codebook.getWidth();
        }

        double[] vlad = new double[size];

        int offset = 0;

        // Regarding each codebook
        for (Codebook codebook : codebooks) {
            // Building the vlad subvector for the next codebook
            double[] subvector = new double[codebook.getSize() * codebook.getWidth()];

            // Accumulating the residues per descriptor
            for (double[] descriptor : descriptors) {
                // Finding the nearest centroid index of the descriptor
                int index = codebook.getNearestCentroidIndex(descriptor);

                // Accumulating the residues from the nearest centroid
                for (int i = 0; i < descriptor.length; i++) {
                    double component = codebook.getComponent(index, i);

                    subvector[index * descriptor.length + i] += descriptor[i] - component;
                }
            }

            // Normalize subvector using Power and Euclidean l2 norms
            if (normalize) {
                Normalizer.power(subvector, 0.5);
                Normalizer.euclidean(subvector);
            }

            // Concatenate the subvector
            System.arraycopy(subvector, 0, vlad, offset, subvector.length);
            offset += subvector.length;
        }

        // Normalizing final vector only in case of multiple vocabularies
        if (codebooks.length > 1 && normalize) {
            Normalizer.euclidean(vlad);
        }

        return vlad;
    }
}
