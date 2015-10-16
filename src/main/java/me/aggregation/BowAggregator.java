package me.aggregation;

import me.math.EuclideanNormalizer;
import me.math.Normalizer;
import me.math.PowerNormalizer;

/**
 * An aggregator implementing the bags of words method to produce a fixed size
 * normalized (power, l2) vector given a variant number of local descriptors
 * extracted from a media item.
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/gARWys">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class BowAggregator implements Aggregator {

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
     * @param normalize the option to normalize.
     */
    public BowAggregator(Codebook codebook, boolean normalize) {
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
        double[] bow = new double[codebook.getSize()];

        // Building a histogram of centroid word frequencies
        for (double[] descriptor : descriptors) {
            // Incresing the nearest centroid frequency given the descriptor
            int index = codebook.getNearestCentroidIndex(descriptor);

            bow[index]++;
        }

        // Normalize using Power and Euclidean l2 norms
        if (normalize) {
            power.normalize(bow);
            euclidean.normalize(bow);
        }

        return bow;
    }
}
