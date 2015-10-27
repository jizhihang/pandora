package me.math;

/**
 * An abstract implementation to reduce the components of a given vector.
 *
 * @author Akis Papadopoulos
 */
public interface BetaComponentReducer {

    /**
     * A method reducing the components of a given vector.
     *
     * @param vector the vector to be reduced.
     * @return the reduced vector.
     * @exception Exception throws unknown error exceptions.
     */
    public double[] reduce(double[] vector) throws Exception;
}
