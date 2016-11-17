package com.tkb.pandora.math;

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
