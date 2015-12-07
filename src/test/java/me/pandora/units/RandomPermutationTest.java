package me.pandora.units;

import me.pandora.math.RandomPermutation;
import me.pandora.util.ArrayOps;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * A test case for the RandomPermutation class.
 *
 * @author Akis Papadopoulos
 */
public class RandomPermutationTest {

    private static double[][] a1 = {{5.0, 8.0}, {7.0, 3.0}, {4.0, 4.0}, {4.0, 6.0}, {8.0, 8.0}, {9.0, 3.0}, {7.0, 3.0}, {2.0, 4.0}, {2.0, 2.0}, {6.0, 9.0}};

    private static double[][] a2 = new double[0][];

    private static double[][] a3 = {{5.0, 8.0}};

    @Test
    public void testSample() {
        RandomPermutation permutation = new RandomPermutation(1.0, 2L);

        double[][] s = permutation.sample(a1);

        String msg = "Sampled array should not be null flavored";

        assertNotNull(msg, s);

        msg = "Sampling with ratio equal to '1.0' must return the whole items in arbitrary order";

        assertEquals(msg, s.length, a1.length);

        double[][] c = {{4.0, 4.0}, {7.0, 3.0}, {6.0, 9.0}, {9.0, 3.0}, {5.0, 8.0}, {4.0, 6.0}, {8.0, 8.0}, {2.0, 4.0}, {7.0, 3.0}, {2.0, 2.0}};

        msg = "Sampling arrays do not match, cross checking failed for seed value '2L'";

        assertTrue(msg, ArrayOps.equal(s, c));
    }

    @Test
    public void testHalfSample() {
        RandomPermutation permutation = new RandomPermutation(0.5, 11L);

        double[][] s = permutation.sample(a1);

        String msg = "Sampled array should not be null flavored";

        assertNotNull(msg, s);

        msg = "Sampling with ratio equal to '0.5' must return almost the half portion of items";

        assertTrue(msg, s.length <= a1.length * 0.5 + 2);

        double[][] c = {{7.0, 3.0}, {8.0, 8.0}, {7.0, 3.0}, {2.0, 4.0}, {4.0, 4.0}, {2.0, 2.0}};

        msg = "Sampling arrays do not match, cross checking failed for seed value '11L'";

        assertTrue(msg, ArrayOps.equal(s, c));
    }

    @Test
    public void testZeroSample() {
        RandomPermutation permutation = new RandomPermutation(0.5, 9L);

        double[][] s = permutation.sample(a2);

        String msg = "Sampled array should not be null flavored";

        assertNotNull(msg, s);

        msg = "Sampling an empty array should return an empty array as well";

        assertEquals(msg, s.length, 0);
    }

    @Test
    public void testSingleSample() {
        RandomPermutation permutation = new RandomPermutation(0.5, 1455488L);

        double[][] s = permutation.sample(a3);

        String msg = "Sampled array should not be null flavored";

        assertNotNull(msg, s);

        msg = "Sampling with ratio equal to '0.5' must return almost the half portion of items";

        assertTrue(msg, s.length <= a3.length * 0.5 + 2);

        double[][] c = {{5.0, 8.0}};

        msg = "Sampling arrays do not match, cross checking failed for seed value '1455488L'";

        assertTrue(msg, ArrayOps.equal(s, c));
    }
}
