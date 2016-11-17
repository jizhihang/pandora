package com.tkb.pandora.io;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * A file name builder creating unique file names regarding the current
 * timestamp.
 *
 * @author Akis Papadopoulos
 */
public final class FileNameBuilder {

    /**
     * A method building new file names based on the CRC32 checksum value
     * produced by the current timestamp.
     *
     * @return a unique time-wise filename.
     */
    public static String create() {
        // Getting unique alphanumeric representation
        String uuid = UUID.randomUUID().toString();

        // Adding the date as a prefix
        SimpleDateFormat sdf = new SimpleDateFormat("YY-MM-dd-HH-mm-ss-SSS");
        String time = sdf.format(new Date());

        return time + "-" + uuid;
    }
}
