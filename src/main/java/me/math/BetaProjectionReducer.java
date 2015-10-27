package me.math;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SingularOps;

/**
 * A projection component reducer based on principal component analysis using
 * singular value decomposition method to reduce vectors to the most principal
 * components regarding the eigenvalue space.
 * 
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/sdBCiC">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class BetaProjectionReducer implements BetaComponentReducer {

    // Principal component subspace, row based
    private DenseMatrix64F V_t;

    // Adjustment mean vector across each component
    private double[] mean;

    // Number of principal components to be used
    private int size;

    /**
     * A constructor creating a projection reducer given the original data and
     * the number of the most principal components to retain.
     *
     * @param data the original data.
     * @param size the number of the most principal components.
     */
    public BetaProjectionReducer(double[][] data, int size) {
        // Loading original data
        DenseMatrix64F A = new DenseMatrix64F(data);

        // Computing the adjustment mean vector
        mean = new double[A.numCols];

        for (int i = 0; i < A.numRows; i++) {
            for (int j = 0; j < A.numCols; j++) {
                mean[j] += A.get(i, j);
            }
        }

        for (int j = 0; j < mean.length; j++) {
            mean[j] /= A.getNumRows();
        }

        // Subtracting the mean from the original data
        for (int i = 0; i < A.numRows; i++) {
            for (int j = 0; j < A.numCols; j++) {
                A.set(i, j, A.get(i, j) - mean[j]);
            }
        }

        // Computing SVD and saving time by not computing U
        SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows, A.numCols, false, true, false);

        if (!svd.decompose(A)) {
            throw new RuntimeException("Singular value decomposition process failed");
        }

        V_t = svd.getV(null, true);

        DenseMatrix64F W = svd.getW(null);

        // Ordering singular values desceding, cause in an arbitrary order initially
        SingularOps.descendingOrder(null, false, W, V_t, true);

        // TODO: Move this statement into the recuce method
        // Striping off unneeded components finding the basis
        V_t.reshape(size, mean.length, true);
    }

    /**
     * A constructor creating a projection reducer given the principal component
     * subspace basis, the adjustment mean vector and the number of the most
     * principal components to retain.
     *
     * @param basis the principal component subspace.
     * @param mean the adjustment mean vector.
     * @param size the number of the most principal components.
     */
    public BetaProjectionReducer(double[][] basis, double[] mean, int size) {
        V_t = new DenseMatrix64F(basis);
        this.mean = mean;
        this.size = size;
    }

    /**
     * A method reducing the components of a given vector.
     *
     * @param vector the vector to be reduced.
     * @return the reduced vector.
     * @exception Exception throws unknown error exceptions.
     */
    @Override
    public double[] reduce(double[] vector) throws Exception {
        // Subtracting mean from the given vector
        DenseMatrix64F v = new DenseMatrix64F(vector.length, 1, true, vector);

        DenseMatrix64F m = DenseMatrix64F.wrap(mean.length, 1, mean);

        CommonOps.subtract(v, m, v);

        // Reducing given vector to the most principal components regarding subspace
        DenseMatrix64F r = new DenseMatrix64F(size, 1);

        CommonOps.mult(V_t, v, r);

        return r.data;
    }

    /**
     * A method returning the projection principal component subspace matrix.
     *
     * @return the basis principal component subspace matrix.
     */
    public double[][] getBasis() {
        double[][] basis = new double[V_t.numRows][V_t.numCols];

        for (int i = 0; i < V_t.numRows; i++) {
            for (int j = 0; j < V_t.numCols; j++) {
                basis[i][j] = V_t.get(i, j);
            }
        }

        return basis;
    }

    /**
     * A method returning the adjustment mean vector.
     *
     * @return the adjustment mean vector.
     */
    public double[] getMean() {
        return mean;
    }
}
