package me.pandora.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A singleton system file manager implementing write output stream methods.
 *
 * @author Akis Papadopoulos
 */
public final class Writer {

    /**
     * A method writing a given vector to a binary file in comma separated form.
     *
     * @param vector the vector.
     * @param filepath the absolute path to the file.
     * @param append if true the vector will be appended in the file.
     * @throws IOException an unknown exception.
     */
    public static void write(double[] vector, String filepath, boolean append) throws IOException {
        BufferedWriter writer = null;

        try {
            // Opening a write output stream
            writer = new BufferedWriter(new FileWriter(filepath, append));

            if (append) {
                writer.newLine();
            }

            // Writing components in comma separated form
            for (int j = 0; j < vector.length; j++) {
                writer.write(String.valueOf(vector[j]));

                if (j < vector.length - 1) {
                    writer.write(",");
                }
            }
        } catch (IOException exc) {
            throw exc;
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    /**
     * A method writing a given matrix in a binary file in line-by-line form.
     *
     * @param matrix the matrix.
     * @param filename the absolute path to the file.
     * @param append if true the matrix will be appended in the file.
     * @throws IOException an unknown exception.
     */
    public static void write(double[][] matrix, String filename, boolean append) throws IOException {
        BufferedWriter writer = null;

        try {
            // Opening a file output stream
            writer = new BufferedWriter(new FileWriter(filename, append));

            if (append) {
                writer.newLine();
            }

            // Writing line-byline each row
            for (int i = 0; i < matrix.length; i++) {
                double[] row = matrix[i];

                // Building each row in a comma separated line
                StringBuilder line = new StringBuilder();

                for (int j = 0; j < row.length; j++) {
                    line.append(row[j]);

                    if (j < row.length - 1) {
                        line.append(",");
                    }
                }

                writer.write(line.toString());

                if (i < matrix.length - 1) {
                    writer.newLine();
                }
            }
        } catch (IOException exc) {
            throw exc;
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    /**
     * A method writing a given content in a binary file.
     *
     * @param content the content to be written.
     * @param filename the absolute path to the file.
     * @param append if true the matrix will be appended in the file.
     * @throws IOException an unknown exception.
     */
    public static void write(String content, String filename, boolean append) throws IOException {
        BufferedWriter writer = null;

        try {
            // Opening a file output stream
            writer = new BufferedWriter(new FileWriter(filename, append));

            if (append) {
                writer.newLine();
            }

            writer.write(content);
        } catch (IOException exc) {
            throw exc;
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    /**
     * A method writing the given vector instances into a binary file.
     *
     * @param instances the instances to be written.
     * @param filename the absolute path to the file.
     * @param append if true the matrix will be appended in the file.
     * @throws IOException an unknown exception.
     */
    public static void write(Instances instances, String filename, boolean append) throws IOException {
        BufferedWriter writer = null;

        try {
            // Opening a file output stream
            writer = new BufferedWriter(new FileWriter(filename, append));

            if (append) {
                writer.newLine();
            }

            // Writing each instance vector line by line
            for (int i = 0; i < instances.numInstances(); i++) {
                Instance instance = instances.instance(i);

                // Building each row in a comma separated line
                StringBuilder line = new StringBuilder();

                for (int j = 0; j < instance.numAttributes(); j++) {
                    line.append(instance.value(j));

                    if (j < instance.numAttributes() - 1) {
                        line.append(",");
                    }
                }

                writer.write(line.toString());

                if (i < instances.numInstances() - 1) {
                    writer.newLine();
                }
            }
        } catch (IOException exc) {
            throw exc;
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}
