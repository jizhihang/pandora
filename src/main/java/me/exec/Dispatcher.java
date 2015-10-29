package me.exec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An entry executable classes dispatcher.
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
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            final Class<?> entry = entries.get(args[0]);

            if (entry != null) {
                // Run the choosen entry given the rest of the arguments
                final Object arguments = Arrays.copyOfRange(args, 1, args.length);

                // Making sure all entries implement a main static method
                entry.getMethod("main", String[].class).invoke(null, arguments);
            } else {
                System.out.println("Unable to run, entry " + args[0] + " not found");
                System.out.println("Please run available entries as: ");

                for (String key : entries.keySet()) {
                    System.out.println(" java -jar pandora-box.jar " + key + " configs/file");
                }

                System.exit(1);
            }
        } else {
            System.out.println("Unable to run, no or too many arguments found");
            System.out.println("Please run available entries as: ");

            for (String key : entries.keySet()) {
                System.out.println(" java -jar pandora-box.jar " + key + " configs/file");
            }

            System.exit(1);
        }
    }
}
