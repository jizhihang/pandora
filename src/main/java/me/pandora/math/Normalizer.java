package me.pandora.math;

import java.util.Arrays;

/**
 * A singleton implementing various normalization methods.
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/p6wGSv">more</a>.
 *
 * @author Akis Papadopoulos
 */
public final class Normalizer {

    /**
     * A method normalizing a given vector using the Euclidean method.
     *
     * @param vector the components of the vector.
     */
    public static void euclidean(double[] vector) {
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

    /**
     * A method normalizing a given vector using the power method.
     *
     * @param vector the components of the vector.
     * @param a the a parameter.
     */
    public static void power(double[] vector, double a) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.signum(vector[i]) * Math.pow(Math.abs(vector[i]), a);
        }
    }
}
