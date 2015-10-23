package me.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An indices random permutation engine.
 *
 * @author Akis Papadopoulos
 */
public class RandomPermutation {

    // Permutation indices
    private final int[] indices;

    /**
     * A constructor creating randomly permutations of indices given the size.
     *
     * @param size the size of indices to permute.
     * @param seed the seed number.
     */
    public RandomPermutation(int size, int seed) {
        indices = new int[size];

        // Listing indices
        List<Integer> list = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        // Shuffling indices
        Collections.shuffle(list, new Random(seed));

        for (int i = 0; i < size; i++) {
            indices[i] = list.get(i);
        }
    }

    /**
     * A method sampling a portion of the permutation indices.
     *
     * @param ratio the portion of indices to retain.
     * @return a sample of indices.
     */
    public int[] sample(double ratio) {
        int size = (int) (indices.length * ratio);

        if (size < indices.length) {
            int[] sampled = new int[size];

            for (int i = 0; i < size; i++) {
                sampled[i] = indices[i];
            }

            return sampled;
        } else {
            return indices;
        }
    }
}
