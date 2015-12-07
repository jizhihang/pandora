package me.pandora.exec;

import java.io.*;
import java.util.Properties;
import me.pandora.io.Writer;
import me.pandora.io.Reader;
import me.pandora.io.MultipleFilenameFilter;
import me.pandora.math.ProjectionSpace;
import me.pandora.math.RandomPermutation;
import org.apache.log4j.Logger;

/**
 * An executable creating the projection principal component space given a list
 * of vectors using singular value decomposition.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.pandora.exec.Projector" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Projector {

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("vectors.input.file.path");
            String extension = props.getProperty("vectors.file.extension");
            double ratio = Double.parseDouble(props.getProperty("vectors.sample.ratio", "1.0"));
            long seed = Long.parseLong(props.getProperty("vectors.sample.seed.number", "1"));
            boolean whiten = Boolean.parseBoolean(props.getProperty("projection.space.whitening", "false"));
            boolean compact = Boolean.parseBoolean(props.getProperty("projection.space.compact.form", "false"));
            String outpath = props.getProperty("projection.space.output.file.path");
            String logfile = outpath + "/project.log";

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Projector.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Vectors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Ratio: " + ratio);
            logger.info("Seed: " + seed);
            logger.info("Whitening: " + whiten);
            logger.info("Compact: " + compact);

            logger.info("Process started...");

            // Loading vectors
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter(extension));

            double[][] vectors = new double[filenames.length][];

            for (int i = 0; i < filenames.length; i++) {
                vectors[i] = Reader.read(dirin.getPath() + "/" + filenames[i], 1);
            }

            // Sampling vectors using random permutation indices
            RandomPermutation permutation = new RandomPermutation(ratio, seed);

            double[][] sample = permutation.sample(vectors);

            // Creating the projection space upon the sampled vectors
            ProjectionSpace projection = new ProjectionSpace(sample, whiten, compact);

            // Saving the projection space into a file
            double[] mean = projection.getMean();
            double[][] space = projection.getSpace();

            // Writing line-by-line where adjustment vector comes first
            Writer.write(mean, outpath, false);
            Writer.write(space, outpath, true);

            logger.info("Process completed successfuly");
            logger.info("Vectors: " + vectors.length);
            logger.info(" Size: " + vectors[0].length);
            logger.info("Eigenvectors: " + space.length);
            logger.info(" Eigenvalues: " + space.length);
            logger.info(" Order: desc");
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred creating projection space", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
