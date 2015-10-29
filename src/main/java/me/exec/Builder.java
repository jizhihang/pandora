package me.exec;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import me.vector.Aggregator;
import me.vector.BowAggregator;
import me.vector.Codebook;
import me.vector.VladAggregator;
import me.vector.VlatAggregator;
import me.io.Writer;
import me.io.Reader;
import me.io.MultipleFilenameFilter;
import me.util.SmartProperties;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

/**
 * A builder aggregates local descriptors per image into a fixed size vector
 * using single/multiple visual word vocabularies.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.exec.Builder" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Builder {

    // Statistics
    private static DescriptiveStatistics stats = new DescriptiveStatistics();

    // Formater
    private static DecimalFormat formater = new DecimalFormat("#.###");

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            SmartProperties props = new SmartProperties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("local.descriptors.input.path");
            String extension = props.getProperty("local.descriptors.file.extension");
            String method = props.getProperty("building.aggregation.method");
            boolean normalize = Boolean.parseBoolean(props.getProperty("building.vector.normalization", "true"));
            List<String> vocabs = props.matchProperties("building.vocabulary.\\d+");
            String outpath = props.getProperty("building.vectors.output.path");
            String logfile = props.getProperty("log.file.path");

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Clusterer.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Descriptors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Method: " + method);
            logger.info("Normalization: " + normalize);

            // Loading local descriptor files
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter(extension));

            // Loading up vocabularies given each file path in restricted order
            Codebook[] codebooks = new Codebook[vocabs.size()];

            // Be aware order matters
            for (int i = 0; i < vocabs.size(); i++) {
                String vocab = vocabs.get(i);

                double[][] centroids = Reader.read(vocab);

                Codebook codebook = new Codebook(centroids);

                codebooks[i] = codebook;

                logger.info("Vocabulary #" + (i + 1) + ": " + vocab);
            }

            // Setting up the aggregator
            Aggregator aggregator = null;

            if (method.equalsIgnoreCase("bow")) {
                aggregator = new BowAggregator(codebooks, normalize);
            } else if (method.equalsIgnoreCase("vlad")) {
                aggregator = new VladAggregator(codebooks, normalize);
            } else if (method.equalsIgnoreCase("vlat")) {
                aggregator = new VlatAggregator(codebooks, normalize);
            }

            logger.info("Process started");

            // Aggregating local descriptors per image
            int vectorSize = 0;

            for (int i = 0; i < filenames.length; i++) {
                // Loading local descriptor
                double[][] descriptors = Reader.read(dirin.getPath() + "/" + filenames[i]);

                stats.addValue(descriptors.length);

                // Vectorizing descriptors
                double[] vector = aggregator.aggregate(descriptors);

                vectorSize = vector.length;

                // Saving vector with an identical filename
                int pos = filenames[i].lastIndexOf(".");
                String filepath = outpath + "/" + filenames[i].substring(0, pos) + "." + method;

                Writer.write(vector, filepath, false);

                if (i % 100 == 0) {
                    int progress = (i * 100) / filenames.length;
                    logger.info(progress + "%...");
                }
            }

            logger.info("100%");
            logger.info("Process completed successfuly");
            logger.info("Images: " + stats.getN());
            logger.info("Descriptors: " + stats.getSum());
            logger.info("Mean: " + formater.format(stats.getMean()) + " (" + formater.format(stats.getGeometricMean()) + ")");
            logger.info("MinMax: [" + stats.getMin() + ", " + stats.getMax() + "]");
            logger.info("Vector Size: " + vectorSize);
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred building fixed size vectors", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
