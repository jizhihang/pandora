package me.pandora.exec;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Properties;
import me.pandora.io.Writer;
import me.pandora.io.Reader;
import me.pandora.io.MultipleFilenameFilter;
import me.pandora.math.RandomPermutation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

/**
 * A sampler selecting randomly description using random permutations indices.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.pandora.exec.Sampler" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Sampler {

    // Statistics
    private static DescriptiveStatistics descStats = new DescriptiveStatistics();
    private static DescriptiveStatistics sampleStats = new DescriptiveStatistics();

    // Formater
    private static DecimalFormat formater = new DecimalFormat("#.####");

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("descriptions.input.file.path");
            String extension = props.getProperty("descriptions.file.extension");
            double ratio = Double.parseDouble(props.getProperty("sampler.permutations.ratio", "0.1"));
            long seed = Long.parseLong(props.getProperty("sampler.permutations.seed", "1"));
            String outpath = props.getProperty("sample.output.file.path");
            String logfile = outpath + ".log";

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Sampler.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Descriptions: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Ratio: " + ratio);
            logger.info("Seed: " + seed);

            // Loading description files
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter(extension));

            logger.info("Process started");

            boolean append = false;

            // Sampling descriptors
            RandomPermutation permutation = new RandomPermutation(ratio, seed);

            for (int i = 0; i < filenames.length; i++) {
                try {
                    // Loading next description regarding descriptors
                    double[][] descriptors = Reader.read(dirin.getPath() + "/" + filenames[i]);

                    descStats.addValue(descriptors.length);

                    // Sampling descriptors
                    double[][] sampled = permutation.sample(descriptors);

                    // Writing down the sampled descriptors indexed by permutations
                    if (sampled.length > 0) {
                        Writer.write(sampled, outpath, append);

                        sampleStats.addValue(sampled.length);

                        // Starting to append next descriptors
                        if (!append) {
                            append = true;
                        }
                    }

                    if (i % 100 == 0) {
                        int progress = (i * 100) / filenames.length;
                        logger.info(progress + "%...");
                    }
                } catch (Exception exc) {
                    logger.error("An unknown error occurred sampling descriptors.", exc);
                }
            }

            logger.info("100%");
            logger.info("Process completed successfuly");
            logger.info("Images: " + descStats.getN());
            logger.info(" Descriptors: " + descStats.getSum());
            logger.info("  Mean: " + formater.format(descStats.getMean()) + " (" + formater.format(descStats.getGeometricMean()) + ")");
            logger.info("  MinMax: [" + descStats.getMin() + ", " + descStats.getMax() + "]");
            logger.info(" Sampled: " + sampleStats.getSum());
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred sampling descriptors", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
