package me.exec;

import java.io.*;
import java.util.Properties;
import me.io.Writer;
import me.io.Reader;
import me.io.MultipleFilenameFilter;
import me.math.ComponentAnalyzer;
import me.math.PrincipalComponentAnalyzer;
import org.apache.log4j.Logger;

/**
 * A projection analyzer applies PCA on a list of vectors using singular value
 * decomposition.
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
            String outpath = props.getProperty("projection.matrix.output.path");
            String logfile = props.getProperty("log.file.path");

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Projector.class);

            System.out.print("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Vectors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Sample Ratio: " + ratio);
            logger.info("Seed: " + seed);

            // Loading the vectors
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter(extension));

            double[][] vectors = new double[filenames.length][];

            for (int i = 0; i < filenames.length; i++) {
                vectors[i] = Reader.read(dirin.getPath() + "/" + filenames[i], 1);
            }

            logger.info("Process started...");

            // Setting up the component analyzer
            ComponentAnalyzer analyzer = new PrincipalComponentAnalyzer(ratio, seed);

            // Applying principal component analysis to the given vector set
            double[][] matrix = analyzer.analyze(vectors);

            // Writing projection matrix line-by-line preceded by the adjustment vector
            Writer.write(matrix, outpath, false);

            logger.info("Process completed successfuly");
            logger.info("Vectors: " + vectors.length);
            logger.info("Vector Size: " + vectors[0].length);
            logger.info("Eigenvectors: " + matrix[1].length);
            logger.info("Order: desc");
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred applying projection analysis", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
