package me.pandora.units;

import java.awt.image.BufferedImage;
import java.util.Random;
import me.pandora.image.FeatureDetector;
import me.pandora.image.global.Cedd;
import me.pandora.image.global.ColorHistogram;
import me.pandora.image.global.ColorScale;
import me.pandora.image.global.Edge;
import me.pandora.image.global.Hog;
import me.pandora.image.global.Phog;
import me.pandora.image.global.TamuraHistogram;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A test case for various global detectors.
 *
 * @author Akis Papadopoulos
 */
public class GlobalDetectorsTest {

    private static final double EXTRACTION_TIME_TOLERANCE = 1.9;

    private static final double DESCRIPTOR_SIZE_TOLERANCE = 8 * 1024;

    private static final long SEED = 1L;

    private static final int IMG_WIDTH = 1024;

    private static final int IMG_HEIGHT = 681;

    private static BufferedImage image;

    @BeforeClass
    public static void setUp() {
        image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Random rand = new Random(SEED);

        for (int y = 0; y < IMG_HEIGHT; y++) {
            for (int x = 0; x < IMG_WIDTH; x++) {
                int a = (int) (rand.nextDouble() * 256);
                int r = (int) (rand.nextDouble() * 256);
                int g = (int) (rand.nextDouble() * 256);
                int b = (int) (rand.nextDouble() * 256);

                int p = (a << 24) | (r << 16) | (g << 8) | b;

                image.setRGB(x, y, p);
            }
        }
    }

    private void submit(FeatureDetector detector) {
        long start = System.currentTimeMillis();

        double[] descriptor = null;

        try {
            descriptor = detector.extract(image).getDescriptor(0);
        } catch (Exception exc) {
        }

        long end = System.currentTimeMillis();

        double extractionTime = (end - start) / 1000.0;

        String msg = "Extracted descriptor shuld not be null flavored";

        assertNotNull(msg, descriptor);

        msg = "Found large descriptor length '" + descriptor.length + "', where tolerance is '" + DESCRIPTOR_SIZE_TOLERANCE + "'";

        assertTrue(msg, descriptor.length > 0 && descriptor.length < DESCRIPTOR_SIZE_TOLERANCE);

        msg = "Found long extraction time '" + extractionTime + "', where tolerance is '" + EXTRACTION_TIME_TOLERANCE + "'";

        assertTrue(msg, extractionTime < EXTRACTION_TIME_TOLERANCE);
    }

    @Test
    public void testCedd() {
        Cedd detector = new Cedd(14d, 0.68d, 0.98d, 0.98d, false, true);

        submit(detector);
    }

    @Test
    public void testColorHistogram() {
        ColorHistogram detector = new ColorHistogram(5, false);

        submit(detector);
    }

    @Test
    public void testColorScale() {
        ColorScale detector = new ColorScale(true);

        submit(detector);
    }

    @Test
    public void testEdge() {
        Edge detector = new Edge(true);

        submit(detector);
    }

    @Test
    public void testHog() {
        Hog detector = new Hog(5, 5);

        submit(detector);
    }

    @Test
    public void testPhog() {
        Phog detector = new Phog(2, 12, true);

        submit(detector);
    }

    @Test
    public void testTamuraHistogram() {
        TamuraHistogram detector = new TamuraHistogram(true);

        submit(detector);
    }
}
