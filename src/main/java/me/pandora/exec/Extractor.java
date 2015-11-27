package me.pandora.exec;

import boofcv.io.image.UtilImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Properties;
import me.pandora.image.FeatureDetector;
import me.pandora.image.global.Cedd;
import me.pandora.image.global.ColorLayoutHistogram;
import me.pandora.image.global.ScalableColorHistogram;
import me.pandora.image.local.ColorSurf;
import me.pandora.image.local.Sift;
import me.pandora.image.local.Surf;
import me.pandora.io.Writer;
import me.pandora.io.MultipleFilenameFilter;
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
    private static DescriptiveStatistics stats = new DescriptiveStatistics();

    // Formater
    private static DecimalFormat formater = new DecimalFormat("#.###");

    public static void main(String[] args) {
        Logger logger = null;

        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("dataset.images.input.path");
            String extension = props.getProperty("dataset.image.file.extension");
            String method = props.getProperty("descriptions.detection.method");
            String outpath = props.getProperty("descriptions.output.path");
            String logfile = props.getProperty("log.file.path");

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Extractor.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Images: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Detector: " + method);

            // Loading image files
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter(extension));

            // Setting up the detector
            FeatureDetector detector = null;

            if (method.equalsIgnoreCase("surf")) {
                int radius = Integer.parseInt(props.getProperty("detector.surf.radius", "1"));
                float threshold = Float.parseFloat(props.getProperty("detector.surf.threshold", "0F"));
                int maxFeaturesPerScale = Integer.parseInt(props.getProperty("detector.surf.max.features.per.scale", "-1"));
                int initialSampleRate = Integer.parseInt(props.getProperty("detector.surf.initial.sample.rate", "2"));
                int initialSize = Integer.parseInt(props.getProperty("detector.surf.initial.size", "9"));
                int numberScalesPerOctave = Integer.parseInt(props.getProperty("detector.surf.number.scales.per.octave", "4"));
                int numberOfOctaves = Integer.parseInt(props.getProperty("detector.surf.number.of.octaves", "4"));
                boolean slided = Boolean.parseBoolean(props.getProperty("detector.surf.slided.orientation", "false"));

                logger.info("Radius: " + radius);
                logger.info("Threshold: " + threshold);
                logger.info("Max Features Per Scale: " + maxFeaturesPerScale);
                logger.info("Initial Sample Rate: " + initialSampleRate);
                logger.info("Initial Size: " + initialSize);
                logger.info("Number Scales Per Octave: " + numberScalesPerOctave);
                logger.info("Number Of Octaves: " + numberOfOctaves);
                logger.info("Sliding Orientation: " + slided);

                detector = new Surf(radius, threshold, maxFeaturesPerScale, initialSampleRate, initialSize, numberScalesPerOctave, numberOfOctaves, slided);
            } else if (method.equalsIgnoreCase("csurf")) {
                int radius = Integer.parseInt(props.getProperty("detector.csurf.radius", "1"));
                float threshold = Float.parseFloat(props.getProperty("detector.csurf.threshold", "0F"));
                int maxFeaturesPerScale = Integer.parseInt(props.getProperty("detector.csurf.max.features.per.scale", "-1"));
                int initialSampleRate = Integer.parseInt(props.getProperty("detector.csurf.initial.sample.rate", "2"));
                int initialSize = Integer.parseInt(props.getProperty("detector.csurf.initial.size", "9"));
                int numberScalesPerOctave = Integer.parseInt(props.getProperty("detector.csurf.number.scales.per.octave", "4"));
                int numberOfOctaves = Integer.parseInt(props.getProperty("detector.csurf.number.of.octaves", "4"));
                boolean slided = Boolean.parseBoolean(props.getProperty("detector.csurf.slided.orientation", "false"));
                boolean normalize = Boolean.parseBoolean(props.getProperty("detector.csurf.normalize", "false"));

                logger.info("Radius: " + radius);
                logger.info("Threshold: " + threshold);
                logger.info("Max Features Per Scale: " + maxFeaturesPerScale);
                logger.info("Initial Sample Rate: " + initialSampleRate);
                logger.info("Initial Size: " + initialSize);
                logger.info("Number Scales Per Octave: " + numberScalesPerOctave);
                logger.info("Number Of Octaves: " + numberOfOctaves);
                logger.info("Sliding Orientation: " + slided);
                logger.info("Normalize: " + normalize);

                detector = new ColorSurf(radius, threshold, maxFeaturesPerScale, initialSampleRate, initialSize, numberScalesPerOctave, numberOfOctaves, slided, normalize);
            } else if (method.equalsIgnoreCase("sift")) {
                int extractRadius = Integer.parseInt(props.getProperty("detector.sift.extract.radius", "2"));
                float detectThreshold = Float.parseFloat(props.getProperty("detector.sift.detect.threshold", "1"));
                int maxFeaturesPerScale = Integer.parseInt(props.getProperty("detector.sift.max.features.per.scale", "-1"));
                double edgeThreshold = Double.parseDouble(props.getProperty("detector.sift.edge.threshold", "5"));
                boolean normalize = Boolean.parseBoolean(props.getProperty("detector.sift.normalize", "false"));

                logger.info("Extract Radius: " + extractRadius);
                logger.info("Detect Threshold: " + detectThreshold);
                logger.info("Max Features Per Scale: " + maxFeaturesPerScale);
                logger.info("Edge Threshold: " + edgeThreshold);
                logger.info("Normalize: " + normalize);

                detector = new Sift(extractRadius, detectThreshold, maxFeaturesPerScale, edgeThreshold, normalize);
            } else if (method.equalsIgnoreCase("cedd")) {
                double t0 = Double.parseDouble(props.getProperty("detector.cedd.threshold.0", "14d"));
                double t1 = Double.parseDouble(props.getProperty("detector.cedd.threshold.1", "0.68d"));
                double t2 = Double.parseDouble(props.getProperty("detector.cedd.threshold.2", "0.98d"));
                double t3 = Double.parseDouble(props.getProperty("detector.cedd.threshold.3", "0.98d"));
                boolean compact = Boolean.parseBoolean(props.getProperty("detector.cedd.compact.form", "false"));

                logger.info("Threshold 0: " + t0);
                logger.info("Threshold 1: " + t1);
                logger.info("Threshold 2: " + t2);
                logger.info("Threshold 3: " + t3);
                logger.info("Compact: " + compact);

                detector = new Cedd(t0, t1, t2, t3, compact);
            } else if (method.equalsIgnoreCase("clh")) {
                detector = new ColorLayoutHistogram();
            } else if (method.equalsIgnoreCase("sch")) {
                detector = new ScalableColorHistogram();
            }

            logger.info("Process started");

            // Extracting descriptors per image
            for (int i = 0; i < filenames.length; i++) {
                try {
                    BufferedImage image = UtilImageIO.loadImage(dirin.getPath() + "/" + filenames[i]);

                    double[][] descriptors = detector.extract(image).getDescriptors();

                    stats.addValue(descriptors.length);

                    // Saving descriptor with an identical name
                    int pos = filenames[i].lastIndexOf(".");
                    String filepath = outpath + "/" + filenames[i].substring(0, pos) + "." + method.toLowerCase();

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
            logger.info("Images: " + stats.getN());
            logger.info("Descriptors: " + stats.getSum());
            logger.info("Mean: " + formater.format(stats.getMean()) + " (" + formater.format(stats.getGeometricMean()) + ")");
            logger.info("MinMax: [" + stats.getMin() + ", " + stats.getMax() + "]");
            logger.info("Outpath: " + outpath);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred extracting visual descriptions", exc);
            } else {
                exc.printStackTrace();
            }
        }
    }
}
