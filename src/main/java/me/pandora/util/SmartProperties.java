package me.pandora.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * A extended version of the build-in properties class.
 *
 * @author Akis Papadopoulos
 */
public class SmartProperties extends Properties {

    /**
     * A method returning matching property values given a regex pattern.
     *
     * @param pattern the regex pattern to match.
     * @return a list of values.
     */
    public List<String> matchProperties(String pattern) {
        List<String> matched = new ArrayList<String>();

        for (Enumeration keys = this.keys(); keys.hasMoreElements();) {
            String key = (String) keys.nextElement();

            if (key.matches(pattern)) {
                matched.add(key);
            }
        }

        // Sorting alphabetically because order is important
        if (!matched.isEmpty()) {
            Collections.sort(matched);
        }

        List<String> values = new ArrayList<String>();

        for (String key : matched) {
            String value = this.getProperty(key);

            values.add(value);
        }

        return values;
    }
}
