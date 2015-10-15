package me.aggregation;

/**
 * An aggregator implementing the bags of words method to produce a fixed size
 * vector given a variant number of local descriptors extracted from a media
 * item.
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/gARWys">more</a>.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public class BowAggregator implements Aggregator {

    // Vocabulary codebook
    private Codebook codebook;

    /**
     * A constructor initiating the vocabulary codebook of centroid words.
     *
     * @param codebook the vocabulary codebook.
     */
    public BowAggregator(Codebook codebook) {
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
        double[] bow = new double[codebook.getSize()];

        // Building a histogram of media centroid word frequencies
        for (double[] descriptor : descriptors) {
            // Incresing the nearest centroid frequency given the descriptor
            int index = codebook.getNearestCentroidIndex(descriptor);

            bow[index]++;
        }

        return bow;
    }
}
