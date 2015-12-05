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

    private static double[][] a = {{5.0, 8.0}, {7.0, 3.0}, {4.0, 4.0}, {4.0, 6.0}, {8.0, 8.0}, {9.0, 3.0}, {7.0, 3.0}, {2.0, 4.0}, {2.0, 2.0}, {6.0, 9.0}};

    private static double[][] b = new double[0][];

    @Test
    public void testSample() {
        RandomPermutation permutation = new RandomPermutation(1.0, 2L);

        double[][] s = permutation.sample(a);

        String msg = "Sampled array should not be null flavored";

        assertNotNull(msg, s);

        msg = "Sampling with ratio equal to '1.0' must return the whole items in arbitrary order";
        
        assertEquals(msg, s.length, a.length);

        double[][] c = {{4.0, 4.0}, {7.0, 3.0}, {6.0, 9.0}, {9.0, 3.0}, {5.0, 8.0}, {4.0, 6.0}, {8.0, 8.0}, {2.0, 4.0}, {7.0, 3.0}, {2.0, 2.0}};

        msg = "Sampling arrays do not match, cross checking failed for seed value '2L'";
        
        assertTrue(msg, ArrayOps.equal(s, c));
    }

    @Test
    public void testHalfSample() {
        RandomPermutation permutation = new RandomPermutation(0.5, 11L);

        double[][] s = permutation.sample(a);

        String msg = "Sampled array should not be null flavored";

        assertNotNull(msg, s);

        msg = "Sampling with ratio equal to '0.5' must return almost the half portion of items";

        assertTrue(msg, s.length <= a.length * 0.5 + 2);

        double[][] c = {{7.0, 3.0}, {8.0, 8.0}, {7.0, 3.0}, {2.0, 4.0}, {4.0, 4.0}, {2.0, 2.0}};

        msg = "Sampling arrays do not match, cross checking failed for seed value '11L'";
        
        assertTrue(msg, ArrayOps.equal(s, c));
    }

    @Test
    public void testZeroSample() {
        RandomPermutation permutation = new RandomPermutation(0.5, 9L);

        double[][] s = permutation.sample(b);

        String msg = "Sampled array should not be null flavored";

        assertNotNull(msg, s);

        msg = "Sampling an empty array should return an empty array as well";

        assertEquals(msg, s.length, 0);
    }
}
