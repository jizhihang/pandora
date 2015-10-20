package me.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A simple JPEG image file name filter.
 *
 * @author Akis Papadopoulos
 */
public class ImageFilenameFilter implements FilenameFilter {

    /**
     * A method accepting the given file regarding the extension.
     *
     * @param dir the directory of the file.
     * @param name the name of the file.
     * @return true if extension matches, otherwise false.
     */
    @Override
    public boolean accept(File dir, String name) {
        String filename = name.toLowerCase();
        
        return filename.endsWith(".jpg");
    }
}
