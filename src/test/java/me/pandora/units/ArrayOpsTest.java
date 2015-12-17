package me.pandora.units;

import me.pandora.util.ArrayOps;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * A test case for the ArrayOps class.
 *
 * @author Akis Papadopoulos
 */
public class ArrayOpsTest {

    @Test
    public void testCopy() {
        double[][] a = {{1, 2}, {3, 4}, {5, 6}, {7, 8}};

        double[][] b = {{1, 2}, {3, 4}, {5, 6}, {7, 8}};

        double[][] c = ArrayOps.copy(a, 0);

        assertTrue(ArrayOps.equal(b, c));

        double[][] d = {{3, 4}, {5, 6}, {7, 8}};

        double[][] e = ArrayOps.copy(a, 1);

        assertTrue(ArrayOps.equal(d, e));

        double[][] f = {{7, 8}};

        double[][] g = ArrayOps.copy(a, a.length - 1);

        assertTrue(ArrayOps.equal(f, g));

        double[][] h = {{1, 2}, {3, 4}, {5, 6}, {7, 8}};

        double[][] i = ArrayOps.copy(a, 0, a.length - 1);

        assertTrue(ArrayOps.equal(h, i));

        double[][] j = {{3, 4}, {5, 6}};

        double[][] k = ArrayOps.copy(a, 1, 2);

        assertTrue(ArrayOps.equal(j, k));

        double[][] l = {{5, 6}};

        double[][] m = ArrayOps.copy(a, 2, 2);

        assertTrue(ArrayOps.equal(l, m));
    }

    @Test
    public void testEqual() {
        double[] a = {1, 2, 3};
        double[] b = {1, 2, 3};

        boolean result = ArrayOps.equal(a, b);

        assertTrue(result);

        double[] c = {1, 2, 3};
        double[] d = {1, 2, 4};

        result = ArrayOps.equal(c, d);

        assertFalse(result);

        double[] e = {1, 2, 3};
        double[] f = {4};

        result = ArrayOps.equal(e, f);

        assertFalse(result);

        double[][] g = {{1, 2, 3}, {4, 5, 6}};
        double[][] h = {{1, 2, 3}, {4, 5, 6}};

        result = ArrayOps.equal(g, h);

        assertTrue(result);

        double[][] i = {{1, 2, 3}, {4, 5, 6}};
        double[][] j = {{1, 2, 3}, {4, 5, 7}};

        result = ArrayOps.equal(i, j);

        assertFalse(result);

        double[][] k = {{1, 2, 3}, {5, 6}};
        double[][] l = {{1, 2, 3}, {4, 5, 7}};

        result = ArrayOps.equal(k, l);

        assertFalse(result);
    }

    @Test
    public void testFlatten() {
        int[][] a = {{1, 2, 3}, {4, 5}, {8, 8, 99, 5}};

        double[] b = ArrayOps.flatten(a);

        assertEquals(b.length, 9);

        double[][] c = {{1, 2, 3}, {4, 5}, {8, 8, 99, 5}};

        double[] d = ArrayOps.flatten(c);

        assertEquals(d.length, 9);

        double[][] e = {{1}};

        double[] f = ArrayOps.flatten(e);

        assertEquals(e.length, 1);
    }

    @Test
    public void testFloatToDouble() {
        float[] a = {1.2f, 2.3f, 3.5f};

        double[] g = {(double) 1.2f, (double) 2.3f, (double) 3.5f};

        double[] b = ArrayOps.toDouble(a);

        assertTrue(ArrayOps.equal(b, g));

        float[][] a2 = {{1.1f, 2.3f, 3.2f}, {4.4f, 5f}, {8f, 8.9f, 99f, 5.8f}};

        double[][] g2 = {{(double) 1.1f, (double) 2.3f, (double) 3.2f}, {(double) 4.4f, (double) 5f}, {(double) 8f, (double) 8.9f, (double) 99f, (double) 5.8f}};

        double[][] b2 = ArrayOps.toDouble(a2);

        assertTrue(ArrayOps.equal(b2, g2));
    }
}
