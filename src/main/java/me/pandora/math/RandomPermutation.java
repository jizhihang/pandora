package me.pandora.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A sampling engine using indices of random permutation.
 *
 * @author Akis Papadopoulos
 */
public class RandomPermutation {

    // Permutation indices
    private List<Integer> indices;

    // Sampling ratio
    private double ratio;

    // Random generator
    private Random rand;

    /**
     * A constructor creating a sampling engine using random permutation indices
     * given the sampling ratio and the seed number.
     *
     * @param ratio the sample ratio.
     * @param seed the seed number.
     */
    public RandomPermutation(double ratio, long seed) {
        this.ratio = ratio;
        rand = new Random(seed);
    }

    /**
     * A method permuting the indices given the items and sampling regarding the
     * ratio with respect to the seed value.
     *
     * @param items the items to sample from.
     * @return a sample of the given items in arbitrary order.
     */
    public double[][] sample(double[][] items) {
        // Permuting indices with respect to the items size
        indices = new ArrayList<Integer>();

        for (int i = 0; i < items.length; i++) {
            indices.add(i);
        }

        // Shuffling indices
        if (indices.size() > 1) {
            Collections.shuffle(indices, rand);
        }

        // Sampling items regarding ratio and permutation order
        List<double[]> list = new ArrayList<double[]>();

        for (Integer index : indices) {
            if (rand.nextDouble() <= ratio) {
                list.add(items[index]);
            }
        }

        double[][] sampled = new double[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            sampled[i] = list.get(i);
        }

        return sampled;
    }
}
