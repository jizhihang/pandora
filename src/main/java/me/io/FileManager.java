package me.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton system file manager.
 *
 * @author Akis Papadopoulos
 */
public final class FileManager {

    /**
     * A method reading a vector written in comma separated form in a binary
     * file.
     *
     * @param filepath the absolute path to the file.
     * @return the vector.
     * @throws IOException an unknown exception.
     */
    public static double[] readVector(String filepath) throws IOException {
        BufferedReader reader = null;

        try {
            // Opening an input stream to read
            reader = new BufferedReader(new FileReader(filepath));

            // Reading line by line
            String line = reader.readLine();

            String[] tokens = line.split(",");

            // Parsing tokens to doubles
            double[] vector = new double[tokens.length];

            for (int j = 0; j < tokens.length; j++) {
                vector[j] = Double.parseDouble(tokens[j]);
            }

            return vector;
        } catch (IOException exc) {
            throw exc;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * A method reading a matrix written in line-by-line form to a binary file.
     *
     * @param filepath the absolute path to the file.
     * @return the matrix.
     * @throws IOException an unknown exception.
     */
    public static double[][] readMatrix(String filepath) throws IOException {
        BufferedReader reader = null;

        try {
            // Opening an input stream to read
            reader = new BufferedReader(new FileReader(filepath));

            List<double[]> list = new ArrayList<double[]>();

            // Reading line by line
            String line;

            // Extracting each line into comma separated tokens
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");

                // Parsing tokens to doubles
                double[] components = new double[tokens.length];

                for (int j = 0; j < tokens.length; j++) {
                    components[j] = Double.parseDouble(tokens[j]);
                }

                list.add(components);
            }

            // Converting list to array
            double[][] matrix = new double[list.size()][];

            for (int i = 0; i < list.size(); i++) {
                matrix[i] = list.get(i);
            }

            return matrix;
        } catch (IOException exc) {
            throw exc;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * A method writing a given vector to a binary file in comma separated form.
     *
     * @param vector the vector.
     * @param filepath the absolute path to the file.
     * @param append if true the vector will be appended in the file.
     * @throws IOException an unknown exception.
     */
    public static void writeVector(double[] vector, String filepath, boolean append) throws IOException {
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
    public static void writeMatrix(double[][] matrix, String filename, boolean append) throws IOException {
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
}
