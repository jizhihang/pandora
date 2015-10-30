package me.exec;

import java.io.*;
import java.util.Properties;
import me.io.Writer;
import me.io.Reader;
import me.io.MultipleFilenameFilter;
import me.math.ProjectionSpace;
import me.math.RandomPermutation;
import org.apache.log4j.Logger;

/**
 * An executable creating the projection principal component space given a list
 * of vectors using singular value decomposition.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.exec.Projector" -Dexec.args="path/to/config.properties"
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

            String inpath = props.getProperty("vectors.input.path");
            String extension = props.getProperty("vectors.file.extension");
            double ratio = Double.parseDouble(props.getProperty("vectors.sample.ratio", "1.0"));
            long seed = Long.parseLong(props.getProperty("sample.seed.number", "1"));
            boolean compact = Boolean.parseBoolean(props.getProperty("projection.space.compact.form", "false"));
            String outpath = props.getProperty("projection.space.output.path");
            String logfile = props.getProperty("log.file.path");

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Projector.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Vectors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Sample Ratio: " + ratio);
            logger.info("Seed: " + seed);
            logger.info("Compact: " + compact);

            // Loading vectors
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter(extension));

            logger.info("Process started...");

            // Sampling vectors using random permutation indices
            RandomPermutation permutation = new RandomPermutation(filenames.length, seed);

            int[] indices = permutation.sample(ratio);

            // Collecting only premutation indexed vectors
            double[][] vectors = new double[indices.length][];

            for (int i = 0; i < indices.length; i++) {
                int index = indices[i];

                vectors[i] = Reader.read(dirin.getPath() + "/" + filenames[index], 1);
            }

            // Creating the projection space upon the sampled vectors
            ProjectionSpace projection = new ProjectionSpace(vectors, compact);

            // Saving the projection space into a file
            double[] mean = projection.getMean();
            double[] eigenvalues = projection.getEigenvalues();
            double[][] space = projection.getSpace();

            // Writing line-by-line where adjustment vector comes first
            Writer.write(mean, outpath, false);
            Writer.write(eigenvalues, outpath, true);
            Writer.write(space, outpath, true);

            logger.info("Process completed successfuly");
            logger.info("Vectors: " + vectors.length);
            logger.info("Vector Size: " + vectors[0].length);
            logger.info("Eigenvectors: " + space.length);
            logger.info("Eigenvalues: " + eigenvalues.length);
            logger.info("Order: desc");
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
