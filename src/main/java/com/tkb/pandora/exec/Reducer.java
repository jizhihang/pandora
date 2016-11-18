package com.tkb.pandora.exec;

import java.io.*;
import java.util.Properties;
import com.tkb.pandora.io.Reader;
import com.tkb.pandora.io.Writer;
import com.tkb.pandora.io.MultipleFileNameFilter;
import com.tkb.pandora.math.ComponentReducer;
import com.tkb.pandora.math.ProjectionReducer;
import com.tkb.pandora.math.ProjectionSpace;
import org.apache.log4j.Logger;

/**
 * An executable reducing the components of vectors to the most principal
 * components given the eigen value projection space.
 *
 * Run as: mvn exec:java -Dexec.mainClass="com.tkb.pandora.exec.Reducer" -Dexec.args="path/to/config.properties"
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

            String inpath = props.getProperty("vectors.input.file.path");
            String extension = props.getProperty("vectors.file.extension");
            String projectionFile = props.getProperty("projection.space.file.path");
            boolean whiten = Boolean.parseBoolean(props.getProperty("projection.space.whitening", "false"));
            int size = Integer.parseInt(props.getProperty("most.dominant.components", "1"));
            String outpath = props.getProperty("reduced.vectors.output.path");
            String subspaceFile = props.getProperty("vectors.subspace.output.file.path");
            String logfile = outpath + "/reduce.log";

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Reducer.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Vectors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Projection: " + projectionFile);
            logger.info("Whitening: " + whiten);
            logger.info("Components: " + size);

            // Loading the vectors
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFileNameFilter(extension));

            // Setting up the component reducer regarding the projection space
            double[][] lines = Reader.read(projectionFile);

            // Loading the mean adjustment vector, first line is the mean vector
            double[] mean = lines[0];

            // Loading the eigenvectors matrix, rest of the lines is the space matrix
            double[][] space = new double[lines.length - 1][lines[1].length];

            for (int i = 1; i < lines.length; i++) {
                for (int j = 0; j < lines[i].length; j++) {
                    space[i - 1][j] = lines[i][j];
                }
            }

            ProjectionSpace ps = new ProjectionSpace(space, mean);

            // Geeting the sub-space basis regarding the requested number of components
            double[][] subspace = ps.getBasis(size);

            // Saving the sub-space projection
            Writer.write(mean, subspaceFile, false);
            Writer.write(subspace, subspaceFile, true);

            ComponentReducer reducer = new ProjectionReducer(subspace, mean, whiten);

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

            logger.info("100%");
            logger.info("Process completed successfuly");
            logger.info("Reduced Vectors: " + filenames.length);
            logger.info(" Outpath: " + outpath);
            logger.info(" Projection: " + subspaceFile);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred projecting vectors", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
