package me.math;

/**
 * A vector normalizer using power normalization.
 *
 * This class is a modification of a class written by Elefterios
 * Spyromitros-Xioufis, please see <a href="https://goo.gl/p6wGSv">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class PowerNormalizer implements Normalizer {

    // Power normalization parameter
    private double a;

    /**
     * A constructor initiating the default a parameter.
     */
    public PowerNormalizer() {
        this.a = 0.5;
    }

    /**
     * A constructor initiating with the given a parameter.
     *
     * @param a the parameter of the power normalization.
     */
    public PowerNormalizer(double a) {
        this.a = a;
    }

    /**
     * A method normalizing a given vector.
     *
     * @param vector the components of the vector.
     */
    @Override
    public void normalize(double[] vector) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.signum(vector[i]) * Math.pow(Math.abs(vector[i]), a);
        }
    }
}
