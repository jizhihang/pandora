package me.pandora.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * A singleton implementing various image processing methods.
 *
 * @author Akis Papadopoulos
 */
public final class Transformer {

    /**
     * A method scaling down a given image in the target size in pixels
     * regarding the original ratio retaining the proportions.
     *
     * @param image the image to be scaled down.
     * @param target the target size in pixels.
     * @return a buffered image.
     */
    public static BufferedImage scale(BufferedImage image, int target) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();

        int width = image.getWidth();
        int height = image.getHeight();

        long size = width * height;

        // Scaling down otherwise return the same image
        if (target < size) {
            // Calculating the scale ratio and target dims
            double ratio = Math.sqrt((double) target / size);

            int targetWidth = (int) (width * ratio);
            int targetHeight = (int) (height * ratio);

            // Drawing the scaled image
            BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, type);

            Graphics2D graphics = scaled.createGraphics();

            // Regarding quality parameters
            graphics.setComposite(AlphaComposite.Src);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
            graphics.dispose();

            return scaled;
        } else {
            return image;
        }
    }
}
