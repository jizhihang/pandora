package me.pandora.exec;

import boofcv.io.image.UtilImageIO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Properties;
import me.pandora.image.FeatureDetector;
import me.pandora.io.Writer;
import me.pandora.io.MultipleFileNameFilter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

/**
 * A detector extracting visual descriptions given the dataset of images plus a
 * detection configuration file.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.pandora.exec.Extractor" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Extractor {

    // Statistics
    private static DescriptiveStatistics imagStats = new DescriptiveStatistics();
    private static DescriptiveStatistics descStats = new DescriptiveStatistics();
    private static DescriptiveStatistics extrStats = new DescriptiveStatistics();

    // Formater
    private static DecimalFormat formater = new DecimalFormat("#.####");

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String imagesPath = props.getProperty("dataset.images.file.path");
            String detectorClassPath = props.getProperty("detector.class.path");
            String detectorSettings = props.getProperty(detectorClassPath);
            String outputPath = props.getProperty("descriptions.output.file.path");
            String logfile = outputPath + "/extract.log";

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Extractor.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Images: " + imagesPath);

            // Loading image files
            File dirin = new File(imagesPath);
            String[] filenames = dirin.list(new MultipleFileNameFilter("jpg", "jpeg", "png"));

            // Setting up the detector
            ClassLoader classLoader = FeatureDetector.class.getClassLoader();

            Class<FeatureDetector> detectorClass = (Class<FeatureDetector>) classLoader.loadClass(detectorClassPath);

            ObjectMapper mapper = new ObjectMapper();

            FeatureDetector detector = mapper.readValue(detectorSettings, detectorClass);

            logger.info("Detector: " + detector.getClass().getName());

            ObjectNode root = (ObjectNode) mapper.readTree(mapper.writeValueAsString(detector));
            Iterator<String> fields = root.fieldNames();

            while (fields.hasNext()) {
                String field = fields.next();
                JsonNode value = root.findValue(field);

                logger.info(" " + field + ": '" + value.asText() + "'");
            }

            logger.info("Process started");

            // Extracting descriptors per image
            for (int i = 0; i < filenames.length; i++) {
                try {
                    BufferedImage image = UtilImageIO.loadImage(dirin.getPath() + "/" + filenames[i]);

                    long start = System.currentTimeMillis();

                    double[][] descriptors = detector.extract(image).getDescriptors();

                    long end = System.currentTimeMillis();

                    double extractionTime = (end - start) / 1000.0;

                    // Collecting various statistics
                    imagStats.addValue(descriptors.length);
                    descStats.addValue(descriptors[0].length);
                    extrStats.addValue(extractionTime);

                    // Saving descriptor with an identical name
                    int pos = filenames[i].lastIndexOf(".");
                    String filepath = outputPath + "/" + filenames[i].substring(0, pos) + ".desc";

                    Writer.write(descriptors, filepath, false);

                    if (i % 100 == 0) {
                        int progress = (i * 100) / filenames.length;
                        logger.info(progress + "%...");
                    }
                } catch (Exception exc) {
                    logger.error("An unknown error occurred extracting visual description for image " + filenames[i], exc);
                }
            }

            logger.info("100%");
            logger.info("Process completed successfuly");
            logger.info("Images: " + imagStats.getN());
            logger.info(" Descriptors: " + imagStats.getSum());
            logger.info("  Mean: " + formater.format(imagStats.getMean()) + " (" + formater.format(imagStats.getGeometricMean()) + ")");
            logger.info("  MinMax: [" + imagStats.getMin() + ", " + imagStats.getMax() + "]");
            logger.info(" Components: " + imagStats.getSum() * (int) descStats.getMean());
            logger.info("  Mean: " + formater.format(descStats.getMean()) + " (" + formater.format(descStats.getGeometricMean()) + ")");
            logger.info("  MinMax: [" + descStats.getMin() + ", " + descStats.getMax() + "]");
            logger.info("Extraction: " + formater.format(extrStats.getSum()) + " secs (" + (formater.format(extrStats.getSum() / 60.0)) + " mins)");
            logger.info("  Mean: " + formater.format(extrStats.getMean()) + " (" + formater.format(extrStats.getGeometricMean()) + ")");
            logger.info("  MinMax: [" + extrStats.getMin() + ", " + extrStats.getMax() + "]");
            logger.info("Outpath: " + outputPath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred extracting visual descriptions", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
