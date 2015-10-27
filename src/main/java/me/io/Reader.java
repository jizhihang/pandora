package me.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton system file manager implementing read input stream methods.
 *
 * @author Akis Papadopoulos
 */
public final class Reader {

    /**
     * A method returning a vector which is indexed to a line identified by the
     * line number in the given file. Each line corresponds to a comma separated
     * vector.
     *
     * @param filepath the absolute path to the file.
     * @param lineNumber the number of the line to read.
     * @return the vector.
     * @throws IOException an unknown exception.
     */
    public static double[] read(String filepath, int lineNumber) throws IOException {
        BufferedReader reader = null;

        try {
            // Opening an input stream to read
            reader = new BufferedReader(new FileReader(filepath));

            // Reading line by line
            String line;
            int index = 1;

            // Iterating file line-by-line
            while ((line = reader.readLine()) != null) {
                // Extracting line indexed by the given line number
                if (index == lineNumber) {
                    String[] tokens = line.split(",");

                    // Parsing tokens to vector
                    double[] vector = new double[tokens.length];

                    for (int j = 0; j < tokens.length; j++) {
                        vector[j] = Double.parseDouble(tokens[j]);
                    }

                    return vector;
                } else if (index < lineNumber) {
                    index++;
                } else {
                    return null;
                }
            }

            return null;
        } catch (IOException exc) {
            throw exc;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * A method returning a matrix each row indexed to a line in comma separated
     * form in the given file where each line corresponds to a vector.
     *
     * @param filepath the absolute path to the file.
     * @return the matrix.
     * @throws IOException an unknown exception.
     */
    public static double[][] read(String filepath) throws IOException {
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
}
