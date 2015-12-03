package me.pandora.units;

import java.awt.image.BufferedImage;
import me.pandora.image.global.BinaryPattern;
import me.pandora.image.global.ColorHistogram;
import me.pandora.image.global.Hog;
import me.pandora.image.global.Phog2;
import me.pandora.image.global.SpatialGist;
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
        int width = 420;
        int height = 241;

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
    public void testTamuraHistogram() throws Exception {
        TamuraHistogram detector = new TamuraHistogram(true);

        double[] descriptor = detector.extract(image).getDescriptor(0);

        assertEquals(descriptor.length, 18);
    }

    @Test
    public void testColorHistogram() throws Exception {
        int bins = 4;

        ColorHistogram detector = new ColorHistogram(bins, false);

        double[] descriptor = detector.extract(image).getDescriptor(0);

        assertEquals(bins * bins * bins, descriptor.length);
    }

    @Test
    public void testSpatialGist() throws Exception {
        SpatialGist detector = new SpatialGist(false);

        double[] descriptor = detector.extract(image).getDescriptor(0);

        assertEquals(descriptor.length, 2048);
    }

    @Test
    public void testHog() throws Exception {
        int xBlocks = 4;
        int yBlocks = 4;

        Hog detector = new Hog(xBlocks, yBlocks);

        double[] descriptor = detector.extract(image).getDescriptor(0);

        assertEquals(descriptor.length, 9 * xBlocks * yBlocks);
    }

    @Test
    public void testPhog2() throws Exception {
        int levels = 2;
        int bins = 1;

        Phog2 detector = new Phog2(levels, bins, true);

        double[] descriptor = detector.extract(image).getDescriptor(0);

        assertEquals(descriptor.length, descriptor.length);
    }

    @Test
    public void testBinaryPattern() throws Exception {
        BinaryPattern detector = new BinaryPattern(false);

        double[] descriptor = detector.extract(image).getDescriptor(0);

        assertEquals(descriptor.length, image.getWidth() * image.getHeight());
    }
}
