package me.exec;

import boofcv.io.image.UtilImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;
import me.image.ColorSurfDetector;
import me.image.Detector;
import me.image.SiftDetector;
import me.image.SurfDetector;
import me.io.FileManager;
import me.io.MultipleFilenameFilter;
import org.apache.log4j.Logger;

/**
 * A detector extracting local descriptors given the dataset of images plus a
 * detection configuration file.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.exec.Extractor" -Dexec.args="path/to/config.properties"
 * Log as: tail -f /tmp/pandora-box.log
 *
 * @author Akis Papadopoulos
 */
public class Extractor {

    // Logger
    private static final Logger logger = Logger.getLogger(Extractor.class);

    public static void main(String[] args) {
        try {
            // Loading configuration properties
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));

            String inpath = props.getProperty("dataset.images.input.path");
            String method = props.getProperty("descriptors.detection.method");
            String outpath = props.getProperty("local.descriptors.output.path");

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Inpath: " + inpath);
            logger.info("Detector: " + method);
            logger.info("Outpath: " + outpath);

            // Loading image files
            File dirin = new File(inpath);
            String[] filenames = dirin.list(new MultipleFilenameFilter("jpg"));

            // Setting up the detector
            Detector detector = null;

            if (method.equalsIgnoreCase("surf")) {
                int radius = Integer.parseInt(props.getProperty("detector.surf.radius", "1"));
                float threshold = Float.parseFloat(props.getProperty("detector.surf.threshold", "0F"));
                int maxFeaturesPerScale = Integer.parseInt(props.getProperty("detector.surf.max.features.per.scale", "-1"));
                int initialSampleRate = Integer.parseInt(props.getProperty("detector.surf.initial.sample.rate", "2"));
                int initialSize = Integer.parseInt(props.getProperty("detector.surf.initial.size", "9"));
                int numberScalesPerOctave = Integer.parseInt(props.getProperty("detector.surf.number.scales.per.octave", "4"));
                int numberOfOctaves = Integer.parseInt(props.getProperty("detector.surf.number.of.octaves", "4"));

                detector = new SurfDetector(radius, threshold, maxFeaturesPerScale, initialSampleRate, initialSize, numberScalesPerOctave, numberOfOctaves);
            } else if (method.equalsIgnoreCase("csurf")) {
                int radius = Integer.parseInt(props.getProperty("detector.csurf.radius", "1"));
                float threshold = Float.parseFloat(props.getProperty("detector.csurf.threshold", "0F"));
                int maxFeaturesPerScale = Integer.parseInt(props.getProperty("detector.csurf.max.features.per.scale", "-1"));
                int initialSampleRate = Integer.parseInt(props.getProperty("detector.csurf.initial.sample.rate", "2"));
                int initialSize = Integer.parseInt(props.getProperty("detector.csurf.initial.size", "9"));
                int numberScalesPerOctave = Integer.parseInt(props.getProperty("detector.csurf.number.scales.per.octave", "4"));
                int numberOfOctaves = Integer.parseInt(props.getProperty("detector.csurf.number.of.octaves", "4"));
                boolean normalize = Boolean.parseBoolean(props.getProperty("detector.csurf.normalize", "false"));

                detector = new ColorSurfDetector(radius, threshold, maxFeaturesPerScale, initialSampleRate, initialSize, numberScalesPerOctave, numberOfOctaves, normalize);
            } else if (method.equalsIgnoreCase("sift")) {
                int extractRadius = Integer.parseInt(props.getProperty("detector.sift.extract.radius", "2"));
                float detectThreshold = Float.parseFloat(props.getProperty("detector.sift.detect.threshold", "1"));
                int maxFeaturesPerScale = Integer.parseInt(props.getProperty("detector.sift.max.features.per.scale", "-1"));
                double edgeThreshold = Double.parseDouble(props.getProperty("detector.sift.edge.threshold", "5"));
                boolean normalize = Boolean.parseBoolean(props.getProperty("detector.sift.normalize", "false"));

                detector = new SiftDetector(extractRadius, detectThreshold, maxFeaturesPerScale, edgeThreshold, normalize);
            }

            logger.info("Process started");

            // Extracting local descriptors per image
            File dirout = new File(outpath);

            int total = 0;

            for (int i = 0; i < filenames.length; i++) {
                try {
                    BufferedImage image = UtilImageIO.loadImage(dirin.getPath() + "/" + filenames[i]);

                    double[][] descriptors = detector.detect(image);

                    total += descriptors.length;

                    String filename = filenames[i].substring(0, filenames[i].lastIndexOf("."));

                    // Saving descriptor in the outpath
                    FileManager.writeMatrix(descriptors, dirout.getPath() + "/" + filename + "." + method, false);

                    if (i % 100 == 0) {
                        int progress = (i * 100) / filenames.length;
                        logger.info(progress + "%...");
                    }
                } catch (Exception exc) {
                    logger.error("An unknown error occurred extracting local descriptors.", exc);
                }
            }

            logger.info("100%");
            logger.info("Process completed successfuly");
            logger.info("Images: " + filenames.length);
            logger.info("Descriptors: " + total);
        } catch (Exception exc) {
            logger.error("An unknown error occurred detecting images", exc);
        }
    }
}
