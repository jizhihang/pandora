package me.math;

/**
 * An interface to implement vector normalization methods.
 *
 * @author Akis Papadopoulos
 */
public interface Normalizer {

    /**
     * A method normalizing a given vector.
     *
     * @param vector the components of the vector.
     * @return a normalized vector.
     */
    public double[] normalize(double[] vector);
}
