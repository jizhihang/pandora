package me.pandora.io;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;

/**
 * A filename builder creating unique file names regarding the current
 * timestamp.
 *
 * @author Akis Papadopoulos
 */
public final class FilenameBuilder {

    /**
     * A method building new file names based on the CRC32 checksum value
     * produced by the current timestamp.
     *
     * @return a unique time-wise filename.
     */
    public static String build() {
        // Getting the alphanumeric representation of the current timestamp
        Date now = new Date();
        String str = now.toString();

        // Creating a unique crc32 checksum, based on timestamp
        byte[] bytes = str.getBytes();

        CRC32 builder = new CRC32();
        builder.update(bytes);

        // Getting the checksum value
        long checksum = builder.getValue();

        // Converting into hexadecimal format
        String filename = Long.toHexString(checksum);

        // Adding the date as a prefix
        SimpleDateFormat sdf = new SimpleDateFormat("YY-MM-dd-HH-mm");
        String prefix = sdf.format(now);

        return prefix + "-" + filename;
    }
}
