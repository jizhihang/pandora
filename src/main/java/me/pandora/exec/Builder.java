package me.pandora.exec;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import me.pandora.vector.Aggregator;
import me.pandora.vector.BowAggregator;
import me.pandora.vector.Codebook;
import me.pandora.vector.VladAggregator;
import me.pandora.vector.VlatAggregator;
import me.pandora.io.Writer;
import me.pandora.io.Reader;
import me.pandora.io.MultipleFileNameFilter;
import me.pandora.util.SmartProperties;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

/**
 * A builder aggregates local descriptors per image into a fixed size vector
 * using single/multiple visual word vocabularies.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.pandora.exec.Builder" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Builder {

    // Statistics
    private static DescriptiveStatistics descStats = new DescriptiveStatistics();
    private static DescriptiveStatistics aggStats = new DescriptiveStatistics();

    // Formater
    private static DecimalFormat formater = new DecimalFormat("#.###");

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            SmartProperties props = new SmartProperties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("local.descriptors.input.file.path");
            String extension = props.getProperty("local.descriptors.file.extension");
            String method = props.getProperty("building.aggregation.method");
            boolean normalize = Boolean.parseBoolean(props.getProperty("building.vector.normalization", "true"));
            List<String> vocabs = props.matchProperties("building.vocab.\\d+");
            String outpath = props.getProperty("descriptors.output.file.path");
            String logfile = outpath + "/build.log";

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Builder.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Descriptors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Method: " + method);
            logger.info("Normalization: " + normalize);

            // Loading local descriptor files
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFileNameFilter(extension));

            // Loading up vocabularies given each file path in restricted order
            Codebook[] codebooks = new Codebook[vocabs.size()];

            // Be aware order matters
            for (int i = 0; i < vocabs.size(); i++) {
                String vocab = vocabs.get(i);

                double[][] centroids = Reader.read(vocab);

                Codebook codebook = new Codebook(centroids);

                codebooks[i] = codebook;

                logger.info("Vocab " + (i + 1) + ": " + vocab);
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
            for (int i = 0; i < filenames.length; i++) {
                // Loading local descriptor
                double[][] descriptors = Reader.read(dirin.getPath() + "/" + filenames[i]);

                descStats.addValue(descriptors.length);

                // Vectorizing descriptors
                double[] vector = aggregator.aggregate(descriptors);

                aggStats.addValue(vector.length);

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
            logger.info("Images: " + descStats.getN());
            logger.info(" Descriptors: " + descStats.getSum());
            logger.info(" Mean: " + formater.format(descStats.getMean()) + " (" + formater.format(descStats.getGeometricMean()) + ")");
            logger.info(" MinMax: [" + descStats.getMin() + ", " + descStats.getMax() + "]");
            logger.info("Aggregated: " + aggStats.getN());
            logger.info(" Vector Size: " + aggStats.getMean());
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred building fixed size descriptors", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
