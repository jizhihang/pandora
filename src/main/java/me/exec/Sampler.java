package me.exec;

import java.io.*;
import java.util.Properties;
import me.io.FileManager;
import me.math.RandomPermutation;
import org.apache.log4j.Logger;

/**
 * A sampler selecting randomly local descriptors using random permutations
 * indices.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.exec.Sampler" -Dexec.args="path/to/config.properties"
 * Log as: tail -f /tmp/pandora-box.log
 *
 * @author Akis Papadopoulos
 */
public class Sampler {

    // Logger
    private static final Logger logger = Logger.getLogger(Sampler.class);

    public static void main(String[] args) {
        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("local.descriptors.input.path");
            double ratio = Double.parseDouble(props.getProperty("random.permutation.ratio", "1.0"));
            int seed = Integer.parseInt(props.getProperty("random.permutation.seed", "1"));
            String outpath = props.getProperty("sample.file.absolute.path");

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Inpath: " + inpath);
            logger.info("Ratio: " + ratio);
            logger.info("Seed: " + seed);
            logger.info("Outpath: " + outpath);

            // Loading local descriptor files
            File dirin = new File(inpath);
            String[] filenames = dirin.list();

            logger.info("Process started");

            // Sampling local descriptors per image
            int total = 0;

            for (int i = 0; i < filenames.length; i++) {
                try {
                    // Loading the local descriptors of the next image
                    double[][] descriptors = FileManager.readMatrix(dirin.getPath() + "/" + filenames[i]);

                    // Creating random permutations regarding total number of descriptors
                    RandomPermutation permutations = new RandomPermutation(descriptors.length, seed);

                    // Sampling permutations
                    int[] indices = permutations.sample(ratio);

                    // Writing down the sampled descriptors indexed by permutations
                    for (int j = 0; j < indices.length; j++) {
                        int index = indices[j];

                        // Appending except the very first descriptor
                        boolean append = true;

                        if (i == 0 && j == 0) {
                            append = false;
                        }

                        FileManager.writeVector(descriptors[index], outpath, append);

                        total++;
                    }

                    // Writing indexed local descriptors in the sample file
                    if (i % 100 == 0) {
                        int progress = (i * 100) / filenames.length;
                        logger.info(progress + "%...");
                    }
                } catch (Exception exc) {
                    logger.error("An unknown error occurred sampling local descriptors.", exc);
                }
            }

            logger.info("100%");
            logger.info("Process completed successfuly");
            logger.info("Local Descriptors: " + filenames.length);
            logger.info("Sampled Descriptors: " + total);
        } catch (Exception exc) {
            logger.error("An unknown error occurred sampling local descriptors", exc);
        }
    }
}
