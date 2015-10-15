package me.math;

/**
 * An interface to implement vector based data analysis like,
 * principal component analysis with singular value decomposition.
 *
 * @author Akis Papadopoulos
 */
public interface ComponentAnalyzer {

    /**
     * A method applies a component analysis on a dataset of vectors
     * and returns a projection map.
     *
     * @param data the vector list of the dataset.
     * @return a projection map produced by the analysis.
     * @exception Exception throws unknown error exceptions.
     */
    public double[][] analyze(double[][] data) throws Exception;
}
