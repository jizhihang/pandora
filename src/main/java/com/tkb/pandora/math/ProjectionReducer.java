package com.tkb.pandora.math;

import java.io.IOException;
import com.tkb.pandora.io.Reader;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * A component reducer given the projection principal component sub-space
 * stripped off to the most dominant components.
 *
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/sdBCiC">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class ProjectionReducer implements ComponentReducer {

    // Most dominant principal component eigenvectors
    private DenseMatrix64F B_t;

    // Adjustment mean component vector
    private DenseMatrix64F m;

    // Projection space whitening applied
    private boolean whiten;

    /**
     * A constructor creating a projection reducer given the principal component
     * sub-space to the most dominant components and the adjustment mean vector.
     *
     * @param subspace the principal component sub-space.
     * @param mean the adjustment mean vector.
     * @param whiten true if projection whitening is applied.
     */
    public ProjectionReducer(double[][] subspace, double[] mean, boolean whiten) {
        B_t = new DenseMatrix64F(subspace);

        m = DenseMatrix64F.wrap(mean.length, 1, mean);

        this.whiten = whiten;
    }

    /**
     * A constructor creating a projection reducer given the file containing in
     * the first line the adjustment mean vector followed in the subsequent
     * lines by the most dominant principal eigenvectors in descending
     * eigenvalues order.
     *
     * @param filepath the absolute path to the projection sub-space file.
     * @param whiten true if projection whitening is applied.
     * @throws IOException unknown IO exceptions.
     */
    public ProjectionReducer(String filepath, boolean whiten) throws IOException {
        double[][] lines = Reader.read(filepath);

        // Loading the mean adjustment vector
        double[] mean = lines[0];

        m = DenseMatrix64F.wrap(mean.length, 1, mean);

        // Loading the most dominant eigenvectors
        B_t = new DenseMatrix64F(lines.length - 1, lines[1].length);

        for (int i = 1; i < lines.length; i++) {
            for (int j = 0; j < lines[i].length; j++) {
                B_t.set(i - 1, j, lines[i][j]);
            }
        }

        this.whiten = whiten;
    }

    /**
     * A method reducing the components of a given vector.
     *
     * @param vector the vector to be reduced.
     * @return the components of the reduced vector.
     */
    @Override
    public double[] reduce(double[] vector) {
        // Subtracting mean from the given vector
        DenseMatrix64F v = new DenseMatrix64F(vector.length, 1, true, vector);

        CommonOps.subtract(v, m, v);

        // Reducing components to the most principal regarding sub-space
        DenseMatrix64F r = new DenseMatrix64F(B_t.numRows, 1);

        CommonOps.mult(B_t, v, r);

        double[] reduced = r.data;

        // Normalizing if whitening is applied
        if (whiten) {
            Normalizer.euclidean(reduced);
        }

        return reduced;
    }
}
