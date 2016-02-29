package me.pandora.exec;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Properties;
import me.pandora.io.Writer;
import me.pandora.io.Reader;
import me.pandora.io.MultipleFileNameFilter;
import me.pandora.math.RandomPermutation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

/**
 * A sampler collecting randomly vectors using random permutations indices.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.pandora.exec.Sampler" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Sampler {

    // Statistics
    private static DescriptiveStatistics vectStats = new DescriptiveStatistics();
    private static DescriptiveStatistics sampleStats = new DescriptiveStatistics();

    // Formater
    private static DecimalFormat formater = new DecimalFormat("#.####");

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("vectors.input.file.path");
            String extension = props.getProperty("vectors.file.extension");
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
            logger.info("Vectors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Ratio: " + ratio);
            logger.info("Seed: " + seed);

            // Loading vectors files
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFileNameFilter(extension));

            logger.info("Process started");

            boolean append = false;

            // Sampling vectors
            RandomPermutation permutation = new RandomPermutation(ratio, seed);

            for (int i = 0; i < filenames.length; i++) {
                try {
                    // Loading next vectors file
                    double[][] vectors = Reader.read(dirin.getPath() + "/" + filenames[i]);

                    vectStats.addValue(vectors.length);

                    // Sampling vectors
                    double[][] sampled = permutation.sample(vectors);

                    // Writing down the sampled vectors indexed by permutations
                    if (sampled.length > 0) {
                        Writer.write(sampled, outpath, append);

                        sampleStats.addValue(sampled.length);

                        // Starting to append next vectors
                        if (!append) {
                            append = true;
                        }
                    }

                    if (i % 100 == 0) {
                        int progress = (i * 100) / filenames.length;
                        logger.info(progress + "%...");
                    }
                } catch (Exception exc) {
                    logger.error("An unknown error occurred sampling vectors", exc);
                }
            }

            logger.info("100%");
            logger.info("Process completed successfuly");
            logger.info("Images: " + vectStats.getN());
            logger.info(" Descriptors: " + vectStats.getSum());
            logger.info("  Mean: " + formater.format(vectStats.getMean()) + " (" + formater.format(vectStats.getGeometricMean()) + ")");
            logger.info("  MinMax: [" + vectStats.getMin() + ", " + vectStats.getMax() + "]");
            logger.info(" Sampled: " + sampleStats.getSum());
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred sampling vectors", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
