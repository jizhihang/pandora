package me.math;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.SingularOps;

/**
 * A principal component analyzer for dimensionality reduction on datasets with
 * massive dimensionality, sorting the most significant components across the
 * full set of dataset attributes.
 *
 * This class is a modification of a class written by Peter Abeles, please see
 * <a href="https://goo.gl/sdBCiC">more</a>.
 *
 * @author Akis Papadopoulos
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
        // Building random permutations regarding total number of data items
        RandomPermutation permutations = new RandomPermutation(data.length, seed);

        // Sampling permutation indexed items regarding ratio
        int[] indices = permutations.sample(ratio);

        double[][] sample = new double[indices.length][];

        for (int j = 0; j < indices.length; j++) {
            int index = indices[j];

            sample[j] = data[index];
        }

        DenseMatrix64F A = new DenseMatrix64F(data);

        // Calculating the adjustment mean vector
        double[] mean = new double[A.numCols];

        for (int i = 0; i < A.getNumRows(); i++) {
            for (int j = 0; j < mean.length; j++) {
                mean[j] += A.get(i, j);
            }
        }

        for (int j = 0; j < mean.length; j++) {
            mean[j] /= A.getNumRows();
        }

        // Adjusting by subtracting each component by component's mean
        for (int i = 0; i < A.numRows; i++) {
            for (int j = 0; j < A.numCols; j++) {
                A.set(i, j, A.get(i, j) - mean[j]);
            }
        }

        // Applying PCA on adjusted data using SVD
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
        map[0] = mean;

        // Adding the projection matrix, rest rows
        for (int i = 1; i < map.length; i++) {
            map[i] = projection[i - 1];
        }

        return map;
    }
}
