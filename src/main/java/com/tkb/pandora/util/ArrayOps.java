package com.tkb.pandora.util;

import java.math.BigDecimal;

/**
 * A utility to manage various array operations.
 *
 * @author Akis Papadopoulos
 */
public final class ArrayOps {

    /**
     * A method converts an array of primitive doubles to objects.
     *
     * @param array an array of primitive doubles.
     * @return an array of Double objects.
     */
    public static Double[] toObject(final double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new Double[0];
        }

        final Double[] result = new Double[array.length];

        for (int i = 0; i < array.length; i++) {
            result[i] = Double.valueOf(array[i]);
        }

        return result;
    }

    /**
     * A method converts a 2d array of primitive doubles to objects.
     *
     * @param array an array of primitive doubles.
     * @return an array of Double objects.
     */
    public static Double[][] toObject(final double[][] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new Double[0][];
        }

        final Double[][] result = new Double[array.length][];

        for (int i = 0; i < array.length; i++) {
            result[i] = new Double[array[i].length];

            for (int j = 0; j < array[i].length; j++) {
                result[i][j] = Double.valueOf(array[i][j]);
            }
        }

        return result;
    }

    /**
     * A method converts an array of object doubles to primitives.
     *
     * @param array an array of object doubles.
     * @return an array of double primitives.
     */
    public static double[] toPrimitive(final Double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0];
        }

        final double[] result = new double[array.length];

        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }

        return result;
    }

    /**
     * A method converts a 2d array of object doubles to primitives.
     *
     * @param array an array of object doubles.
     * @return an array of double primitive.
     */
    public static double[][] toPrimitive(final Double[][] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0][];
        }

        final double[][] result = new double[array.length][];

        for (int i = 0; i < array.length; i++) {
            result[i] = new double[array[i].length];

            for (int j = 0; j < array[i].length; j++) {
                result[i][j] = array[i][j].doubleValue();
            }
        }

        return result;
    }

    /**
     * A method converts an array of big decimal objects to primitive doubles.
     * Note that even when the converted value is finite, this conversion can
     * lose information about the precision of the BigDecimal value.
     *
     * @param array an array of big decimal objects.
     * @return an array of primitive doubles.
     */
    public static double[] toPrimitive(final BigDecimal[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0];
        }

        final double[] result = new double[array.length];

        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }

        return result;
    }

    /**
     * A method converts a 2d array of big decimal objects to primitive doubles.
     * Note that even when the converted value is finite, this conversion can
     * lose information about the precision of the BigDecimal value.
     *
     * @param array an array of big decimal objects.
     * @return an array of primitive doubles.
     */
    public static double[][] toPrimitive(final BigDecimal[][] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0][];
        }

        final double[][] result = new double[array.length][];

        for (int i = 0; i < array.length; i++) {
            result[i] = new double[array[i].length];

            for (int j = 0; j < array[i].length; j++) {
                result[i][j] = array[i][j].doubleValue();
            }
        }

        return result;
    }

    /**
     * A method copying the given array starting from the given row index up to
     * the end.
     *
     * @param array the array to be copied.
     * @param row the row index where the copy starts.
     * @return an array of double primitives.
     */
    public static double[][] copy(final double[][] array, int row) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0][];
        }

        double[][] result = new double[array.length - row][];

        for (int i = 0; i < result.length; i++) {
            result[i] = new double[array[row + i].length];

            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = array[row + i][j];
            }
        }

        return result;
    }

    /**
     * A method copying the given array starting from the given row up to the
     * given end row index, end row inclusive.
     *
     * @param array the array to be copied.
     * @param start the row index where the copy starts.
     * @param end the row index where the copy ends.
     * @return an array of double primitives.
     */
    public static double[][] copy(final double[][] array, int start, int end) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0][];
        }

        double[][] result = new double[end - start + 1][];

        for (int i = 0; i < result.length; i++) {
            result[i] = new double[array[start + i].length];

            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = array[start + i][j];
            }
        }

        return result;
    }

    /**
     * A method checking if the given arrays are equal.
     *
     * @param a an array of doubles.
     * @param b an array of doubles.
     * @return true if the arrays are equal otherwise false.
     */
    public static boolean equal(final double[] a, final double[] b) {
        if (a.length != b.length) {
            return false;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * A method checking if the given 2d arrays are equal.
     *
     * @param a an array of doubles.
     * @param b an array of doubles.
     * @return true if the arrays are equal otherwise false.
     */
    public static boolean equal(final double[][] a, final double[][] b) {
        if (a.length != b.length) {
            return false;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (a[i].length != b[i].length) {
                    return false;
                } else {
                    for (int j = 0; j < a[i].length; j++) {
                        if (a[i][j] != b[i][j]) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    /**
     * A method to flatten a given 2d array to a single array.
     *
     * @param array the array to be flatten.
     * @return a single array of doubles.
     */
    public static double[] flatten(final double[][] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0];
        }

        int size = 0;

        for (int i = 0; i < array.length; i++) {
            size += array[i].length;
        }

        double[] result = new double[size];

        int index = 0;

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                result[index] = array[i][j];

                index++;
            }
        }

        return result;
    }

    /**
     * A method to flatten and convert a given 2d integer array to a single
     * array of doubles.
     *
     * @param array the array to be flatten.
     * @return a single array of doubles.
     */
    public static double[] flatten(final int[][] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0];
        }

        int size = 0;

        for (int i = 0; i < array.length; i++) {
            size += array[i].length;
        }

        double[] result = new double[size];

        int index = 0;

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                result[index] = array[i][j];

                index++;
            }
        }

        return result;
    }

    /**
     * A method up casts an array of floats to doubles.
     *
     * @param array an array of floats.
     * @return an array of doubles.
     */
    public static double[] toDouble(final float[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0];
        }

        final double[] result = new double[array.length];

        for (int i = 0; i < array.length; i++) {
            result[i] = (double) array[i];
        }

        return result;
    }

    /**
     * A method up casts a 2d array of floats to doubles.
     *
     * @param array an array of floats.
     * @return an array of doubles.
     */
    public static double[][] toDouble(final float[][] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[0][];
        }

        final double[][] result = new double[array.length][];

        for (int i = 0; i < array.length; i++) {
            result[i] = new double[array[i].length];

            for (int j = 0; j < array[i].length; j++) {
                result[i][j] = (double) array[i][j];
            }
        }

        return result;
    }
}
