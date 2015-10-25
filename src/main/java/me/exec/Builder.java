package me.exec;

import me.io.MultipleFileFilter;
import java.io.*;
import java.util.Date;
import me.vector.Aggregator;
import me.vector.BowAggregator;
import me.vector.Codebook;
import me.vector.VladAggregator;
import me.vector.VlatAggregator;
import me.io.FileManager;
import org.apache.log4j.Logger;

/**
 * A builder aggregates local descriptors into a fixed size descriptor per
 * image.
 *
 * @author Akis Papadopoulos
 */
public class Builder {
    
    private static final Logger logger = Logger.getLogger(Builder.class);

    /**
     * A method implements the aggregation process to compute the fixed-length
     * descriptor according the BOW, VLAT or VLAD method. Takes as input
     * parameters, the input path of the image local descriptors files, the
     * output path where the fixed-length descriptors stored, the codebook file
     * path, the descriptors type to be aggregated, the aggregation method will
     * be used and the normalization option, e.g. java -jar builder.jar <inpath>
     * <outpath> <codebook> <type> <method> <norm> or using maven mvn exec:java
     * -Dexec.mainClass="me.scripts.Builder" -Dexec.args="inpath outpath
     * codebook type method".
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        try {
            // Printing report message, help messages
            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                System.out.println("Builder - Help\n");
                System.out.println("mvn exec:java -Dexec.mainClass=\"me.scripts.Builder\" -Dexec.args=\"inpath outpath vocabs method\"\n");
                System.out.println("inpath: path to local descriptors");
                System.out.println("outpath: path to final descriptors");
                System.out.println("vocabs: path to codebooks files");
                System.out.println("<type> descriptors type to be aggregated, e.g. surfm.");
                System.out.println("<method> which aggregation method will be used, e.g. vlad.");

                System.exit(0);
            }

            System.out.println("Process started at " + new Date() + ".");

            String inpath = args[0];
            String outpath = args[1];
            String codebookPath = args[2];
            String type = args[3];
            String method = args[4];

            // Printing report message, input parameters
            System.out.println("Inpath: " + inpath);
            System.out.println("Outpath: " + outpath);
            System.out.println("Codebook: " + codebookPath);
            System.out.println("Descriptors: " + type.toLowerCase());
            System.out.println("Aggregator: " + method.toLowerCase());

            // Printing a report message, progress
            System.out.println("Loading codebook...");

            // Reading the codebooks
            Codebook[] codebooks = new Codebook[1];
            double[][] codebook = FileManager.readMatrix(codebookPath);
            codebooks[0] = new Codebook(codebook);

            // Setting up the aggregator, user defined method
            Aggregator aggregator = null;
            String extension = null;

            if (method.equalsIgnoreCase("bow")) {
                aggregator = new BowAggregator(codebooks, true);
                extension = "bow";
            } else if (method.equalsIgnoreCase("vlad")) {
                aggregator = new VladAggregator(codebooks, true);
                extension = "vlad";
            } else if (method.equalsIgnoreCase("vlat")) {
                aggregator = new VlatAggregator(codebooks, true);
                extension = "vlat";
            } else {
                throw new IllegalArgumentException("aggregation method '" + method + "' not supported.");
            }

            // Opening the output directory
            File dirout = new File(outpath);

            if (!dirout.exists()) {
                dirout.mkdir();
            }

            // Opening the input directory
            File dirin = new File(inpath);
            File[] files = dirin.listFiles(new MultipleFileFilter(type));

            if (files.length == 0) {
                throw new Exception("descriptor type '" + type.toLowerCase() + "' not exists.");
            }

            System.out.println("Starting aggregation...");
            System.out.print("Progress:  ");

            // Iterating through the files
            for (int i = 0; i < files.length; i++) {
                double[][] descriptors = FileManager.readMatrix(files[i].getPath());

                double[] descriptor = aggregator.aggregate(descriptors);

                int pos = files[i].getName().lastIndexOf(".");
                String name = files[i].getName().substring(0, pos);

                FileManager.write(descriptor, dirout.getPath() + "/" + name + "." + extension, false);

                if (i % 100 == 0) {
                    int progress = (i * 100) / files.length;
                    System.out.print(progress + "%\b\b\b");
                }
            }

            System.out.println("100%");
            System.out.println("Images examined: " + files.length);
            System.out.println("Aggregation method: " + method.toLowerCase());

            System.out.println("Process finished at " + new Date() + ".");
        } catch (Exception exc) {
            System.err.println("[" + new Date() + "] " + exc);
            exc.printStackTrace(System.err);
            System.out.println("...Process aborted at " + new Date() + ".");
        }
    }
}
