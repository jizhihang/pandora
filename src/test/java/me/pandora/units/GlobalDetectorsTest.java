package me.pandora.units;

import java.awt.image.BufferedImage;
import me.pandora.image.global.TamuraHistogram;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A test case for various global feature detectors.
 *
 * @author Akis Papadopoulos
 */
public class GlobalDetectorsTest {

    private static BufferedImage image;

    @BeforeClass
    public static void setUp() {
        int width = 800;
        int height = 640;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = (int) (Math.random() * 256);
                int r = (int) (Math.random() * 256);
                int g = (int) (Math.random() * 256);
                int b = (int) (Math.random() * 256);

                int p = (a << 24) | (r << 16) | (g << 8) | b;

                image.setRGB(x, y, p);
            }
        }
    }

    @Test
    public void testHistogram() throws Exception {
        TamuraHistogram detector = new TamuraHistogram(false);

        double[] descriptor = detector.extract(image).getDescriptor(0);

        assertEquals(descriptor.length, 18);
    }
}
