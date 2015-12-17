package me.pandora.units;

import java.awt.image.BufferedImage;
import java.util.Random;
import me.pandora.image.FeatureDetector;
import me.pandora.image.local.DenseSift;
import me.pandora.image.local.FastSift;
import me.pandora.image.local.GaussianSift;
import me.pandora.image.local.GridSift;
import me.pandora.image.local.Sift;
import me.pandora.image.local.Surf;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A test case for various local detectors.
 *
 * @author Akis Papadopoulos
 */
public class LocalDetectorsTest {
    
    private static final Logger logger = Logger.getLogger(LocalDetectorsTest.class);

    private static final double EXTRACTION_TIME_TOLERANCE = 9.5;

    private static final double DESCRIPTOR_SIZE_TOLERANCE = 256;

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

        double[][] descriptors = null;

        try {
            descriptors = detector.extract(image).getDescriptors();
        } catch (Exception exc) {
        }

        long end = System.currentTimeMillis();

        double extractionTime = (end - start) / 1000.0;

        String msg = "Extracted local descriptors shuld not be null flavored";

        assertNotNull(msg, descriptors);

        msg = "Found large local descriptor length '" + descriptors[0].length + "', where tolerance is '" + DESCRIPTOR_SIZE_TOLERANCE + "'";

        assertTrue(msg, descriptors[0].length > 0 && descriptors[0].length < DESCRIPTOR_SIZE_TOLERANCE);

        msg = "Found long extraction time '" + extractionTime + "', where tolerance is '" + EXTRACTION_TIME_TOLERANCE + "'";

        assertTrue(msg, extractionTime < EXTRACTION_TIME_TOLERANCE);
        
        logger.info(detector.getClass() + "[" + descriptors[0].length + "]: " + descriptors.length + " in " + extractionTime + " secs");

    }

    @Test
    public void testSurf() {
        FeatureDetector detector = new Surf(2, 0F, -1, 2, 9, 4, 4, true);

        submit(detector);
    }

    @Test
    public void testSift() {
        FeatureDetector detector = new Sift(2, 1, -1, 5, true);

        submit(detector);
    }

    @Test
    public void testGaussianSift() {
        FeatureDetector detector = new GaussianSift(3, 36, 1.5f, 6, 3.0f, 8, 4, 0.2f, 1.0f, false, true);

        submit(detector);
    }

    @Test
    public void testGridSift() {
        FeatureDetector detector = new GridSift(3, 36, 1.5f, 6, 3.0f, 8, 4, 0.2f, 1.0f, true, true);

        submit(detector);
    }

    @Test
    public void testDenseSift() {
        FeatureDetector detector = new DenseSift(5, 5, 5, 5, 4, 4, 8, 2f, 0.2f, false, false);

        submit(detector);
    }

    @Test
    public void testFastSift() {
        FeatureDetector detector = new FastSift(5, 5, 5, 5, 4, 4, 8, 2f, 0.2f, false);

        submit(detector);
    }
}
