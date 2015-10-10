package me.math;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * A projection reducer for dimensionality reduction on datasets with massive
 * dimensionality, using a projection map produced by a principal component
 * analysis.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public class ProjectionReducer implements ComponentReducer {

    // Adjustment vector
    private DenseMatrix64F mean;
    
    // Projection matrix, eigenvectors stored column based
    private DenseMatrix64F V;
    
    // Number of dimensions to retain
    private int dimensions;

    /**
     * A constructor initiating the adjustment vector and projection matrix of
     * the reducer.
     *
     * @param adjustment the adjustment vector.
     * @param projection the projection matrix.
     * @param dimensions the number of dimension to retain.
     */
    public ProjectionReducer(double[] adjustment, double[][] projection, int dimensions) {
        // Setting adjustment vector
        double[][] adj = new double[1][];
        adj[0] = adjustment;
        this.mean = new DenseMatrix64F(adj);

        // Setting the projection matrix
        this.V = new DenseMatrix64F(projection);
        
        this.dimensions = dimensions;
    }

    /**
     * A method reduces the length of a vector by retaining only the first
     * number of components requested, first comes the most dominant
     * eigenvectors, those with the highest singular value.
     *
     * @param vector the vector to be reduced.
     * @return a reduced vector.
     * @exception Exception throws unknown error exceptions.
     */
    @Override
    public double[] reduce(double[] vector) throws Exception {
        // Checking the compatibility of the vector
        if (vector.length != V.numRows) {
            throw new IllegalArgumentException("Incompatible vector length '" + vector.length + "', projection matrix supports vectors of length '" + V.numRows + "'.");
        }

        // Checking the compatibility of the projection matrix
        if (dimensions > V.numCols) {
            throw new IllegalArgumentException("Incompatible number of components '" + dimensions + "' requested, projection matrix supports only first '" + V.numCols + "' dominant components.");
        }

        // Setting up the origin vector holder
        DenseMatrix64F orig = new DenseMatrix64F(1, vector.length, true, vector);

        // Adjusting the origin vector, subtracted by the mean vector
        CommonOps.subtract(orig, mean, orig);

        // Eliminating in the most dominant eigenvectors
        DenseMatrix64F Vred = CommonOps.extract(V, 0, vector.length, 0, dimensions);

        // Setting up the reduced vector
        DenseMatrix64F red = new DenseMatrix64F(1, dimensions);

        // Computing the reduced vector
        CommonOps.mult(orig, Vred, red);
        
        // TODO: Normilize with l2 in case of whitening

        return red.data;
    }
}
