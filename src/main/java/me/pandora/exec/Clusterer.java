package me.pandora.exec;

import java.io.*;
import java.util.Properties;
import me.pandora.io.Writer;
import org.apache.log4j.Logger;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.CSVLoader;

/**
 * A k-means clusterer building centroids given data vector instances.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.pandora.exec.Clusterer" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Clusterer {

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("instances.input.file.path");
            int k = Integer.parseInt(props.getProperty("clustering.clusters.number", "64"));
            int iterations = Integer.parseInt(props.getProperty("clustering.max.iterations", "100"));
            int seed = Integer.parseInt(props.getProperty("clustering.seed.number", "1"));
            boolean initialize = Boolean.parseBoolean(props.getProperty("clustering.centroids.initialization", "false"));
            boolean fast = Boolean.parseBoolean(props.getProperty("clustering.fast.distance", "false"));
            int slots = Integer.parseInt(props.getProperty("clustering.parallel.slots", "1"));
            String outpath = props.getProperty("centroids.output.file.path");
            String logfile = outpath + ".log";

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Clusterer.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Instances: " + inpath);
            logger.info("Initialization: " + initialize);
            logger.info("Clusters: " + k);
            logger.info("Iterations: " + iterations);
            logger.info("Fast Distance: " + fast);
            logger.info("Seed: " + seed);
            logger.info("Slots: " + slots);

            logger.info("Process started...");

            // Loading sample data
            CSVLoader loader = new CSVLoader();

            loader.setNoHeaderRowPresent(true);
            loader.setSource(new File(inpath));

            Instances data = loader.getDataSet();

            // Setting up k-means clusterer
            SimpleKMeans clusterer = new SimpleKMeans();

            if (initialize) {
                SelectedTag tag = new SelectedTag(SimpleKMeans.KMEANS_PLUS_PLUS, SimpleKMeans.TAGS_SELECTION);

                clusterer.setInitializationMethod(tag);
            }

            clusterer.setDebug(true);
            clusterer.setSeed(seed);
            clusterer.setNumClusters(k);
            clusterer.setMaxIterations(iterations);
            clusterer.setNumExecutionSlots(slots);
            clusterer.setFastDistanceCalc(fast);

            // Building clusters
            clusterer.buildClusterer(data);

            // Writing clusters in output file
            Instances centroids = clusterer.getClusterCentroids();

            Writer.write(centroids, outpath, false);

            logger.info("Process completed successfuly");
            logger.info("Instances: " + data.numInstances());
            logger.info("Centroids: " + centroids.numInstances());
            logger.info("Centroid Size: " + data.numAttributes());
            logger.info("Squared Error: " + clusterer.getSquaredError());
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred building clusters", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
