package me.exec;

import java.io.*;
import java.util.Properties;
import me.io.Reader;
import me.io.Writer;
import me.io.MultipleFilenameFilter;
import me.math.ComponentReducer;
import me.math.ProjectionReducer;
import me.math.ProjectionSpace;
import org.apache.log4j.Logger;

/**
 * An executable reducing the components of vectors to the most principal
 * components given the eigen value projection space.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.exec.Reducer" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Reducer {

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("vectors.input.path");
            String extension = props.getProperty("vectors.file.extension");
            String projectionFile = props.getProperty("projection.space.file.path");
            int size = Integer.parseInt(props.getProperty("most.dominant.components", "1"));
            String outpath = props.getProperty("reduced.vectors.output.path");
            String subspaceFile = props.getProperty("reduced.vectors.subspace.output.path");
            String logfile = props.getProperty("log.file.path");

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Reducer.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Vectors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Projection: " + projectionFile);
            logger.info("Components: " + size);

            // Loading the vectors
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter(extension));

            // Setting up the component reducer regarding the projection space
            ProjectionSpace ps = new ProjectionSpace(projectionFile);

            double[] mean = ps.getMean();
            double[][] subspace = ps.getBasis(size);

            // Saving the sub-space projection
            Writer.write(mean, subspaceFile, false);
            Writer.write(subspace, subspaceFile, true);

            ComponentReducer reducer = new ProjectionReducer(subspace, mean);

            logger.info("Process started...");

            for (int i = 0; i < filenames.length; i++) {
                // Reducing vector to the most dominant components
                double[] vector = Reader.read(dirin.getPath() + "/" + filenames[i], 1);

                double[] reduced = reducer.reduce(vector);

                // Saving reduced vector with an identical filename
                String filepath = outpath + "/" + filenames[i];

                Writer.write(reduced, filepath, false);

                if (i % 100 == 0) {
                    int progress = (i * 100) / filenames.length;
                    logger.info(progress + "%...");
                }
            }

            logger.info("Process completed successfuly");
            logger.info("Reduced Vectors: " + filenames.length);
            logger.info("Outpath: " + outpath);
            logger.info("Sub-space Projection: " + subspaceFile);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred projecting vectors", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
