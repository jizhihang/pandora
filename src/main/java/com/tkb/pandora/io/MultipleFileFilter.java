package com.tkb.pandora.io;

import java.io.File;
import java.io.FileFilter;

/**
 * A multiple custom file filter, supporting user defined file types.
 *
 * @author Akis Papadopoulos
 */
public class MultipleFileFilter implements FileFilter {

    // Supported file types
    private String[] patterns;

    /**
     * A constructor initiating user defined multiple patterns.
     *
     * @param patterns multiple user define patterns.
     */
    public MultipleFileFilter(String... patterns) {
        this.patterns = patterns;
    }

    /**
     * A method filtering the file regarding the extension.
     *
     * @param file the file to be filtered.
     * @return true if extension matching at least one pattern, otherwise false.
     */
    @Override
    public boolean accept(File file) {
        for (String pattern : patterns) {
            String name = file.getName().toLowerCase();

            if (name.endsWith("." + pattern.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
