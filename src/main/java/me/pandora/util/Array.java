package me.pandora.util;

/**
 * A utility to manage various array operations.
 *
 * @author Akis Papadopoulos
 */
public final class Array {

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
     * A method converts a 2-d array of primitive doubles to objects.
     *
     * @param array a 2-d array of primitive doubles.
     * @return a 2-d array of Double objects.
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
     * A method converts a 2-d array of object doubles to primitives.
     *
     * @param array a 2-d array of object doubles.
     * @return a 2-d array of double primitive.
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
}
