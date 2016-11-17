package com.tkb.pandora.math;

/**
 * An abstract implementation to reduce the components of a given vector.
 *
 * @author Akis Papadopoulos
 */
public interface ComponentReducer {

    /**
     * A method reducing the components of a given vector.
     *
     * @param vector the vector to be reduced.
     * @return the components of the reduced vector.
     */
    public double[] reduce(double[] vector);
}
