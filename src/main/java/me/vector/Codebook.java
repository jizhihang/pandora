package me.vector;

/**
 * A vocabulary codebook of centroids extracted after applying clustering on a
 * given dataset of local descriptors.
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/6wNxoM">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class Codebook {

    // Centroids
    private double[][] centroids;

    /**
     * A constructor initiating the codebook with the given set of centroids.
     *
     * @param centroids the set of centroids.
     */
    public Codebook(double[][] centroids) {
        this.centroids = centroids;
    }

    /**
     * A methods returns the size of the codebook as the number of the
     * centroids.
     *
     * @return the size of the codebook.
     */
    public int getSize() {
        return centroids.length;
    }

    /**
     * A method returns the width of the codebook as the size of the centroid
     * vectors.
     *
     * @return the size of the centroid vectors.
     */
    public int getWidth() {
        return centroids[0].length;
    }

    /**
     * A methods returns the set of centroids of the codebook.
     *
     * @return the set of centroids.
     */
    public double[][] getCentroids() {
        return centroids;
    }

    /**
     * A method returns the centroid located at the given index.
     *
     * @param index the index of the centroid.
     * @return the centroid vector at the given index.
     */
    public double[] getCentroid(int index) {
        return centroids[index];
    }

    /**
     * A method returns the j-th component of the i-th centroid.
     *
     * @param i the index of the centroid.
     * @param j the index of the component.
     * @return the component of the centroid.
     */
    public double getComponent(int i, int j) {
        return centroids[i][j];
    }

    /**
     * A method computes the index of the Euclidean nearest centroid to the
     * given descriptor.
     *
     * @param descriptor the descriptor.
     * @return the index of the nearest centroid.
     */
    public int getNearestCentroidIndex(double[] descriptor) {
        int index = -1;

        double min = Double.MAX_VALUE;

        // Calculating the euclidean distance per centroid
        for (int i = 0; i < centroids.length; i++) {
            double distance = 0;

            for (int j = 0; j < descriptor.length; j++) {
                distance += Math.pow(centroids[i][j] - descriptor[j], 2);

                // Breaking inner loop when distance exceeds minimum
                if (distance >= min) {
                    break;
                }
            }

            // Saving lowest distance and centroid index so far
            if (distance < min) {
                min = distance;

                index = i;
            }
        }

        return index;
    }

    /**
     * A method calculates the Euclidean distance between the given descriptor
     * and the indexed centroid.
     *
     * @param descriptor the descriptor.
     * @param index the indexed centroid.
     * @return the Euclidean distance.
     */
    public double getDistance(double[] descriptor, int index) {
        double distance = 0;

        for (int j = 0; j < descriptor.length; j++) {
            distance += Math.pow(centroids[index][j] - descriptor[j], 2);
        }

        return distance;
    }
}
