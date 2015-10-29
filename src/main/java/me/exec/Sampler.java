package me.exec;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Properties;
import me.io.Writer;
import me.io.Reader;
import me.io.MultipleFilenameFilter;
import me.math.RandomPermutation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

/**
 * A sampler selecting randomly local descriptors using random permutations
 * indices.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.exec.Sampler" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Sampler {
    
    // Statistics
    private static DescriptiveStatistics stats = new DescriptiveStatistics();
    
    // Formater
    private static DecimalFormat formater = new DecimalFormat("#.###");

    public static void main(String[] args) {
        Logger logger = null;
        
        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("local.descriptors.input.path");
            String extension = props.getProperty("local.descriptors.file.extension");
            double ratio = Double.parseDouble(props.getProperty("random.permutation.ratio", "1.0"));
            long seed = Long.parseLong(props.getProperty("random.permutation.seed", "1"));
            String outpath = props.getProperty("sample.file.absolute.path");
            String logfile = props.getProperty("log.file.path");
            
            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Sampler.class);
            
            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Descriptors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Ratio: " + ratio);
            logger.info("Seed: " + seed);

            // Loading local descriptor files
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter(extension));

            logger.info("Process started");

            // Sampling local descriptors per image
            int sampled = 0;

            for (int i = 0; i < filenames.length; i++) {
                try {
                    // Loading the local descriptors of the next image
                    double[][] descriptors = Reader.read(dirin.getPath() + "/" + filenames[i]);
                    
                    stats.addValue(descriptors.length);

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

                        Writer.write(descriptors[index], outpath, append);

                        sampled++;
                    }
                    
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
            logger.info("Images: " + stats.getN());
            logger.info("Descriptors: " + stats.getSum());
            logger.info("Mean: " + formater.format(stats.getMean()) + " (" + formater.format(stats.getGeometricMean()) + ")");
            logger.info("MinMax: [" + stats.getMin() + ", " + stats.getMax() + "]");
            logger.info("Sampled: " + sampled);
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred sampling local descriptors", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
