package com.tkb.pandora.exec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An entry executable classes dispatcher.
 *
 * Run as: mvn exec:java -Dexec.mainClass="com.tkb.pandora.exec.Dispatcher" -Dexec.args="entry path/to/config.properties"
 * Run as: java -jar pandora.jar 'entry' 'configs/file.properties'
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
        System.out.println("\nCopyright 2016 Akis Papadopoulos, github.com/tzeikob/pandora\n");
        System.out.println("Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this");
        System.out.println("file except in compliance with the License. You may obtain a copy of the License at \n");
        System.out.println("http://www.apache.org/licenses/LICENSE-2.0 \n");
        System.out.println("Unless required by applicable law or agreed to in writing, software distributed under the");
        System.out.println("License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,");
        System.out.println("either express or implied. See the License for the specific language governing permissions");
        System.out.println("and limitations under the License.\n");

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
