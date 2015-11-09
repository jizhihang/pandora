package me.util;

/**
 * A simple tokenizer for vectors written in a delimiter separated form.
 *
 * @author Akis Papadopoulos
 */
public class VectorTokenizer {

    // Delimiter special character
    private String delimiter;

    /**
     * A constructor initializes the tokenizer given a special character as
     * delimiter.
     *
     * @param delimiter the delimiter special character.
     */
    public VectorTokenizer(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * A method tokenizes a delimiter separated vector into an array of doubles
     * tokens.
     *
     * @param vector the vector to be tokenized.
     * @return the components of the vector.
     */
    public double[] tokenize(String vector) {
        String[] tokens = vector.split(delimiter);

        double[] components = new double[tokens.length];

        for (int j = 0; j < tokens.length; j++) {
            components[j] = Double.parseDouble(tokens[j]);
        }

        return components;
    }

    /**
     * A method vectorizing the given tokens into a delimiter separated vector.
     *
     * @param tokens the tokens to be vectorized.
     * @return a vector in a delimiter separated form.
     */
    public String vectorize(double[] tokens) {
        StringBuilder vector = new StringBuilder();

        for (int j = 0; j < tokens.length; j++) {
            vector.append(tokens[j]);

            if (j < tokens.length - 1) {
                vector.append(",");
            }
        }

        return vector.toString();
    }
}
