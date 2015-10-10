package me.math;

/**
 * An interface to implement a method to reduce the size of a given vector.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public interface ComponentReducer {

    /**
     * A method reducing the components of a given vector.
     *
     * @param vector the vector to be reduced.
     * @return a reduced vector.
     * @exception Exception throws unknown error exceptions.
     */
    public double[] reduce(double[] vector) throws Exception;
}
