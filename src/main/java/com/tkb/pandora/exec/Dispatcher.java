package com.tkb.pandora.exec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An entry executable classes dispatcher.
 *
 * Run as: mvn exec:java -Dexec.mainClass="com.tkb.pandora.exec.Dispatcher"
 * -Dexec.args="entry path/to/config.properties" Run as: java -jar pandora.jar
 * 'entry' 'configs/file.properties'
 *
 * @author Akis Papadopoulos
 */
public class Dispatcher {

    // Eecutable entries map
    private static final Map<String, Class<?>> entries;

    static {
        // Setting up entries
        entries = new HashMap<String, Class<?>>();

        entries.put("extract", Extractor.class);
        entries.put("sample", Sampler.class);
        entries.put("cluster", Clusterer.class);
        entries.put("build", Builder.class);
        entries.put("project", Projector.class);
        entries.put("reduce", Reducer.class);
        entries.put("index", Indexer.class);
    }

    public static void main(String[] args) throws Exception {
        // Printing the license notice
        try (BufferedReader br = new BufferedReader(new FileReader("LICENSE"))) {
            String line = null;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException exc) {
            System.err.println("Error: Application aborted, missing LICENSE file.");
            System.exit(1);
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            System.out.println("Pandora's Help Guide");
            System.out.println("Please run one of the available tasks: ");

            for (String key : entries.keySet()) {
                System.out.println(" java -jar pandora.jar " + key + " config/properties");
            }

            System.exit(1);
        } else if (args.length == 2) {
            final Class<?> entry = entries.get(args[0]);

            if (entry != null) {
                // Run the choosen entry given the rest of the arguments
                final Object arguments = Arrays.copyOfRange(args, 1, args.length);

                // Making sure all entries implement a main static method
                entry.getMethod("main", String[].class).invoke(null, arguments);
            } else {
                System.out.println("Unable to run, entry '" + args[0] + "' not found");
                System.out.println("Please run help: ");
                System.out.println(" java -jar pandora.jar help");

                System.exit(1);
            }
        } else {
            System.out.println("Unable to run, none or too many arguments found");
            System.out.println("Please run help: ");
            System.out.println(" java -jar pandora.jar help");

            System.exit(1);
        }
    }
}
