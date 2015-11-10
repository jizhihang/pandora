package me.pandora.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A multiple pattern file name filter.
 *
 * @author Akis Papadopoulos
 */
public class MultipleFilenameFilter implements FilenameFilter {

    // Supported file types
    private String[] patterns;

    /**
     * A constructor initiating user defined multiple patterns.
     *
     * @param patterns multiple user define patterns.
     */
    public MultipleFilenameFilter(String... patterns) {
        this.patterns = patterns;
    }

    /**
     * A method accepting the given filename regarding the extension.
     *
     * @param dir the directory of the file.
     * @param name the name of the file.
     * @return true if extension matches, otherwise false.
     */
    @Override
    public boolean accept(File dir, String name) {
        for (String pattern : patterns) {
            String filename = name.toLowerCase();

            if (filename.endsWith("." + pattern.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
