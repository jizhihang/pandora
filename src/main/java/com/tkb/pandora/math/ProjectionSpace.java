package com.tkb.pandora.math;

import java.io.IOException;
import com.tkb.pandora.io.Reader;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SingularOps;

/**
 * A projection space upon a vector data set using principal component analysis
 * based on singular value decomposition, where most dominant principal
 * components are ordered in descending eigenvalues order.
 *
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/sdBCiC">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class ProjectionSpace {

    // Principal component eigenvectors
    private DenseMatrix64F V_t;

    // Adjustment mean vector
    private DenseMatrix64F m;

    /**
     * A constructor creating a projection space given the original data, where
     * eigenvectors are stored in descending eigenvalues order. Order indicates
     * that the most dominant principal components come first. Using compact
     * form means the projection space will be stripped off to the first in
     * order eigenvectors, equal to the number of the original data items.
     *
     * @param data the original data.
     * @param whiten true to apply whitening.
     * @param compact true to save in compact form.
     * @throws Exception an unknown exception.
     */
    public ProjectionSpace(double[][] data, boolean whiten, boolean compact) throws Exception {
        // Applicable data must have size greater or equal to the vector size
        if (data.length < data[0].length && whiten) {
            throw new Exception("Data size must be greater or equal to vector size: " + data.length + " <> " + data[0].length);
        }
        
        // Loading original data
        DenseMatrix64F A = new DenseMatrix64F(data);

        // Computing the adjustment mean vector
        double[] mean = new double[A.numCols];

        for (int i = 0; i < A.numRows; i++) {
            for (int j = 0; j < A.numCols; j++) {
                mean[j] += A.get(i, j);
            }
        }

        for (int j = 0; j < mean.length; j++) {
            mean[j] /= A.getNumRows();
        }

        m = DenseMatrix64F.wrap(mean.length, 1, mean);

        // Subtracting the mean from the original data
        for (int i = 0; i < A.numRows; i++) {
            for (int j = 0; j < A.numCols; j++) {
                A.set(i, j, A.get(i, j) - mean[j]);
            }
        }

        // Computing SVD and saving time by not computing U
        SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows, A.numCols, false, true, compact);

        if (!svd.decompose(A)) {
            throw new RuntimeException("Singular value decomposition process failed");
        }

        // Computing the eigenvectors squared transposed matrix
        V_t = svd.getV(null, true);

        // Compunting the eigenvalues corresponding to the eigenvectors
        DenseMatrix64F W = svd.getW(null);

        // Ordering singular eigenvalues and vectors in desceding order
        SingularOps.descendingOrder(null, false, W, V_t, true);

        // Applying whitening
        if (whiten) {
            // Creating a transformed eigenvalues diagonal matrix
            DenseMatrix64F W_d = new DenseMatrix64F(W.numCols, W.numCols);

            for (int i = 0; i < W.numCols; i++) {
                double eigenvalue = W.get(i, i);

                W_d.set(i, i, Math.pow(eigenvalue, -0.5));
            }

            // Whiteting the eigenvectors matrix
            DenseMatrix64F V_t_w = new DenseMatrix64F(V_t.numRows, V_t.numCols);

            CommonOps.mult(W_d, V_t, V_t_w);
            V_t = V_t_w;
        }
    }

    /**
     * A constructor creating a projection space given the eigenvectors in
     * descending eigenvalues order and the adjustment mean vector.
     *
     * @param space the eigenvectors of the projections space.
     * @param mean the adjustment mean vector.
     */
    public ProjectionSpace(double[][] space, double[] mean) {
        V_t = new DenseMatrix64F(space);

        m = DenseMatrix64F.wrap(mean.length, 1, mean);
    }

    /**
     * A constructor creating a projection space given the file containing in
     * the first line the adjustment mean vector followed in the subsequent
     * lines by the eigenvectors in descending eigenvalues order.
     *
     * @param filepath the absolute path to the projection space file.
     * @throws IOException unknown IO exceptions.
     */
    public ProjectionSpace(String filepath) throws IOException {
        double[][] lines = Reader.read(filepath);

        // Loading the mean adjustment vector
        double[] mean = lines[0];

        m = DenseMatrix64F.wrap(mean.length, 1, mean);

        // Loading the eigenvectors matrix
        V_t = new DenseMatrix64F(lines.length - 1, lines[1].length);

        for (int i = 1; i < lines.length; i++) {
            for (int j = 0; j < lines[i].length; j++) {
                V_t.set(i - 1, j, lines[i][j]);
            }
        }
    }

    /**
     * A method returning the projection space constituted by the eigenvectors
     * in descending eigenvalues order.
     *
     * @return the projection eigenvectors matrix.
     */
    public double[][] getSpace() {
        double[][] space = new double[V_t.numRows][V_t.numCols];

        for (int i = 0; i < V_t.numRows; i++) {
            for (int j = 0; j < V_t.numCols; j++) {
                space[i][j] = V_t.get(i, j);
            }
        }

        return space;
    }

    /**
     * A method returning a sub-space of the projection space given the number
     * of the most dominant principal components to retain. The eigenvectors
     * matrix will be stripped off to the first in order eigenvectors evaluated
     * by the eigenvalues in descending order.
     *
     * @param size the number of the most dominant components to retain.
     * @return the eigenvectors matrix to the most dominant components.
     */
    public double[][] getBasis(int size) {
        // Striping off unneeded components retaining the most dominant
        double[][] basis = new double[size][V_t.numCols];

        for (int i = 0; i < size; i++) {
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
        return m.data;
    }
}
