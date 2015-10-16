package me.math;

import java.util.Arrays;

/**
 * A vector normalizater using the euclidean norm L2.
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/p6wGSv">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class EuclideanNormalizer implements Normalizer {

    /**
     * A method normalizing a given vector.
     *
     * @param vector the components of the vector.
     */
    @Override
    public void normalize(double[] vector) {
        // Computing vector 2-norm
        double norm2 = 0;

        for (int i = 0; i < vector.length; i++) {
            norm2 += vector[i] * vector[i];
        }

        norm2 = (double) Math.sqrt(norm2);

        // Normalizing the components of the vector
        if (norm2 == 0) {
            Arrays.fill(vector, 1);
        } else {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = vector[i] / norm2;
            }
        }
    }
}
