package me.math;

/**
 * A singleton providing various calculation methods.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com
 */
public class Calculator {

    /**
     * A method computes the index of the euclidean nearest centroid a given
     * descriptor belongs to.
     *
     * @param descriptor the descriptor.
     * @param centroids the list of centroids.
     * @return the index of the nearest centroid.
     */
    public static int computeNearestCentroidIndex(double[] descriptor, double[][] centroids) {
        int index = -1;

        double min = Double.MAX_VALUE;

        // Calculating the euclidean distance per centroid
        for (int i = 0; i < centroids.length; i++) {
            double distance = 0;

            for (int j = 0; j < centroids[0].length; j++) {
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
}
