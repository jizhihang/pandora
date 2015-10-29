package me.math;

import java.io.IOException;
import me.io.Reader;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.SingularOps;

/**
 * A projection space upon a vector data set using principal component analysis
 * based on singular value decomposition, where most dominant principal
 * components are ordered in descending order.
 *
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/sdBCiC">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class ProjectionSpace {

    // Principal component eigen value space
    private DenseMatrix64F V_t;

    // Adjustment mean vector
    private DenseMatrix64F m;

    /**
     * A constructor creating a projection space of eigen values given the
     * original data. Eigen values are ordered descending indicating the most
     * dominant principal components come first. Using compact form means the
     * eigen values will be stripped off equal to the number of the data items.
     *
     * @param data the original data.
     * @param compact true to create space in compact form.
     */
    public ProjectionSpace(double[][] data, boolean compact) {
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

        // Computing the principal component eigen value space
        V_t = svd.getV(null, true);

        DenseMatrix64F W = svd.getW(null);

        // Ordering singular eigen values desceding
        SingularOps.descendingOrder(null, false, W, V_t, true);

        m = DenseMatrix64F.wrap(mean.length, 1, mean);
    }

    /**
     * A constructor creating a projection space given the eigen values and the
     * adjustment mean vector.
     *
     * @param space the projection eigen value space.
     * @param mean the adjustment mean vector.
     */
    public ProjectionSpace(double[][] space, double[] mean) {
        V_t = new DenseMatrix64F(space);

        m = DenseMatrix64F.wrap(mean.length, 1, mean);
    }

    /**
     * A constructor creating a projection space given the line based file
     * containing both the adjustment mean vector followed by the eigen values.
     *
     * @param filepath the absolute path to the projection space file.
     * @throws IOException unknown IO exceptions.
     */
    public ProjectionSpace(String filepath) throws IOException {
        double[][] lines = Reader.read(filepath);

        // Loading 1st line as the mean adjustment vector
        double[] mean = lines[0];

        m = DenseMatrix64F.wrap(mean.length, 1, mean);

        // Loading the eigen values starting from the 2nd line
        V_t = new DenseMatrix64F(lines.length - 1, lines[1].length);

        for (int i = 1; i < lines.length; i++) {
            for (int j = 0; j < lines[i].length; j++) {
                V_t.set(i - 1, j, lines[i][j]);
            }
        }
    }

    /**
     * A method returning the projection space of eigen values.
     *
     * @return the projection space of eigen values.
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
     * of the most dominant principal components to retain.
     *
     * @param size the number of the most dominant components to retain.
     * @return a projection sub-space to the most dominant components.
     */
    public double[][] getBasis(int size) {
        // TODO: Implement a more memory wised matrix copy
        DenseMatrix64F B_t = new DenseMatrix64F(V_t);

        // Striping off unneeded components retaining the most dominant
        B_t.reshape(size, m.numRows, true);

        double[][] basis = new double[B_t.numRows][B_t.numCols];

        for (int i = 0; i < B_t.numRows; i++) {
            for (int j = 0; j < B_t.numCols; j++) {
                basis[i][j] = B_t.get(i, j);
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
