package me.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SingularOps;

/**
 * A principal component analyzer for dimensionality reduction on datasets with
 * massive dimensionality, sorting the most significant components across the
 * full set of dataset attributes.
 *
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/sdBCiC">more</a>.
 *
 * @author Akis Papadopoulos, iakopap@gmail.com.
 */
public class PrincipalComponentAnalyzer implements ComponentAnalyzer {

    // Sampling ratio value
    private double ratio;

    // Sampling seed value
    private long seed;

    /**
     * A constructor initiating the sampling options of the analyzer.
     *
     * @param ratio the sampling ratio on the data to consider.
     * @param seed the sampling seed value.
     */
    public PrincipalComponentAnalyzer(double ratio, long seed) {
        this.ratio = ratio;
        this.seed = seed;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    /**
     * A method applies the principal component analysis on a sample of vectors,
     * adjusting the sample by subtracting with the component's mean values and
     * returns a projection matrix extended by the adjustment vector.
     *
     * @param data the vector list of the dataset.
     * @return a projection map produced by the analysis.
     * @exception Exception throws unknown error exceptions.
     */
    @Override
    public double[][] analyze(double[][] data) throws Exception {
        if (data == null || data.length < 1) {
            throw new IllegalArgumentException("Invalid data values null or empty, analysis failed.");
        }

        // Sampling on the dataset using seed
        Random gen = new Random(seed);

        List<double[]> list = new ArrayList<double[]>();

        for (int i = 0; i < data.length; i++) {
            if (gen.nextDouble() <= ratio) {
                list.add(data[i]);
            }
        }

        // Converting list to 2d double array
        double[][] sample = new double[list.size()][];

        for (int i = 0; i < sample.length; i++) {
            sample[i] = list.get(i);
        }

        // Setting up and feeding the raw data holder with the sample
        DenseMatrix64F A = new DenseMatrix64F(sample);

        // Compute the component's mean across all the samples
        DenseMatrix64F mean = new DenseMatrix64F(1, A.numCols);
        CommonOps.sumCols(A, mean);
        CommonOps.divide(A.numRows, mean);

        // Adjusting raw data, subtracted by each component's mean
        for (int i = 0; i < A.numRows; i++) {
            for (int j = 0; j < A.numCols; j++) {
                A.set(i, j, A.get(i, j) - mean.get(0, j));
            }
        }

        // Copying mean vector to 1d double array, adjustment vector
        double[] adjustment = new double[mean.numCols];

        for (int i = 0; i < mean.numCols; i++) {
            adjustment[i] = mean.get(0, i);
        }

        // Applying PCA on adjusted raw data using SVD
        SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows, A.numCols, false, true, true);

        // Decomposing the original data matrix
        boolean success = svd.decompose(A);

        // Checking if the decomposition process failed
        if (!success) {
            throw new RuntimeException("Unable to decompose the original matrix, analysis failed.");
        }

        // Getting the singular values, square roots of the eigenvalues
        DenseMatrix64F S = svd.getW(null);

        // Getting all the eigenvectors, eigen space
        DenseMatrix64F V = svd.getV(null, false);

        // Ordering singular values and eigenvectors in a descending order
        SingularOps.descendingOrder(null, false, S, V, false);

        // Copying the projection matrix to 2d double array
        double[][] projection = new double[V.numRows][V.numCols];

        for (int i = 0; i < V.numRows; i++) {
            for (int j = 0; j < V.numCols; j++) {
                projection[i][j] = V.get(i, j);
            }
        }

        // Creating a 2d array as the projection map
        double[][] map = new double[projection.length + 1][];

        // Adding the adjustment vector, first row
        map[0] = adjustment;

        // Adding the projection matrix, rest rows
        for (int i = 1; i < map.length; i++) {
            map[i] = projection[i - 1];
        }

        return map;
    }
}
