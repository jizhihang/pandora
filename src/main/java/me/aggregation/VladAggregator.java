package me.aggregation;

import me.math.EuclideanNormalizer;
import me.math.Normalizer;
import me.math.PowerNormalizer;

/**
 * An aggregator implementing the VLAD method to produce a fixed size normalized
 * (power, l2) vector given a variant number of local descriptors extracted from
 * a media item.
 *
 * See more:<br/>
 * <em>H. Jegou, F. Perronnin, M. Douze, J. Sanchez, P. Perez, and C. Schmid,
 * Aggregating local image descriptors into compact codes, IEEE Transactions on
 * Pattern Analysis and Machine Intelligence, 2012.</em>
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/xiSHRJ">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class VladAggregator implements Aggregator {

    // Vocabulary codebook
    private Codebook codebook;

    // Normalization
    private boolean normalize = true;

    // Power normalizer
    Normalizer power;

    // Euclidean normalizer
    Normalizer euclidean;

    /**
     * A constructor initiating the vocabulary codebook of centroid words plus
     * the normalization option.
     *
     * @param codebook the vocabulary codebook.
     * @param normalize the normalization option.
     */
    public VladAggregator(Codebook codebook, boolean normalize) {
        this.codebook = codebook;

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
        // Calculating the size of the vlad vector
        int size = codebook.getSize() * codebook.getWidth();

        double[] vlad = new double[size];

        // Accumulating the residues per descriptor
        for (double[] descriptor : descriptors) {
            // Finding the nearest centroid index of the descriptor
            int index = codebook.getNearestCentroidIndex(descriptor);

            // Accumulating the residues from the nearest centroid
            for (int i = 0; i < descriptor.length; i++) {
                double component = codebook.getComponent(index, i);

                vlad[index * descriptor.length + i] += descriptor[i] - component;
            }
        }

        // Normalize using Power and Euclidean l2 norms
        if (normalize) {
            power.normalize(vlad);
            euclidean.normalize(vlad);
        }

        return vlad;
    }
}
