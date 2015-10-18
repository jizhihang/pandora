package me.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton file system manager.
 *
 * @author Akis Papadopoulos
 */
public final class FileManager {

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

    public static void writeVector(double[] vector, String filepath) throws IOException {
        System.out.println(filepath);

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(filepath));

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
}
