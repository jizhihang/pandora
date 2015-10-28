package me.math;

import java.io.IOException;
import me.io.Reader;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * A component reducer given the projection principal component eigen values
 * sub-space stripped off to the most dominant.
 *
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/sdBCiC">more</a>.
 *
 * @author Akis Papadopoulos
 */
public class ProjectionReducer implements ComponentReducer {

    // Most dominant principal component eigen value sub-space
    private DenseMatrix64F B_t;

    // Adjustment mean component vector
    private DenseMatrix64F m;

    /**
     * A constructor creating a projection reducer given the principal component
     * eigen values sub-space to the most dominant components and the adjustment
     * mean vector.
     *
     * @param subspace the principal component eigen values sub-space.
     * @param mean the adjustment mean vector.
     */
    public ProjectionReducer(double[][] subspace, double[] mean) {
        B_t = new DenseMatrix64F(subspace);

        m = DenseMatrix64F.wrap(mean.length, 1, mean);
    }

    /**
     * A constructor creating a projection reducer given the line based file
     * containing both the adjustment mean vector followed by the eigen values
     * sub-space.
     *
     * @param filepath the absolute path to the projection sub-space file.
     * @throws IOException unknown IO exceptions.
     */
    public ProjectionReducer(String filepath) throws IOException {
        double[][] lines = Reader.read(filepath);

        // Loading 1st line as the mean adjustment vector
        double[] mean = lines[0];

        m = DenseMatrix64F.wrap(mean.length, 1, mean);

        // Loading the eigen values starting from the 2nd line
        B_t = new DenseMatrix64F(lines.length - 1, lines[1].length);

        for (int i = 1; i < lines.length; i++) {
            for (int j = 0; j < lines[i].length; j++) {
                B_t.set(i - 1, j, lines[i][j]);
            }
        }
    }

    /**
     * A method reducing the components of a given vector.
     *
     * @param vector the vector to be reduced.
     * @return the reduced vector.
     */
    @Override
    public double[] reduce(double[] vector) {
        // Subtracting mean from the given vector
        DenseMatrix64F v = new DenseMatrix64F(vector.length, 1, true, vector);

        CommonOps.subtract(v, m, v);

        // Reducing components to the most principal regarding sub-space
        DenseMatrix64F r = new DenseMatrix64F(B_t.numRows, 1);

        CommonOps.mult(B_t, v, r);

        return r.data;
    }
}
