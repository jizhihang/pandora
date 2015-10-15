package me.aggregation;

/**
 * An aggregator implementing the VLAD method to produce a fixed size vector
 * given a variant number of local descriptors extracted from a media item.
 *
 * See more:<br/>
 * <em>H. Jegou, F. Perronnin, M. Douze, J. Sanchez, P. Perez, and C. Schmid,
 * Aggregating local image descriptors into compact codes, IEEE Transactions on
 * Pattern Analysis and Machine Intelligence, 2012.</em>
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/xiSHRJ">more</a>.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public class VladAggregator implements Aggregator {

    // Vocabulary codebook
    private Codebook codebook;

    /**
     * A constructor initiating the vocabulary codebook of centroid words.
     *
     * @param codebook the vocabulary codebook.
     */
    public VladAggregator(Codebook codebook) {
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

        return vlad;
    }
}
