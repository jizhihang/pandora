package com.tkb.pandora.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * A singleton implementing various image processing methods.
 *
 * @author Akis Papadopoulos
 */
public final class Transformer {

    /**
     * A method scaling down a given image in the target size in pixels
     * regarding the original ratio retaining the proportions given an optional
     * stepwise mode in which more steps means better quality.
     *
     * @param image the image to be scaled down.
     * @param targetSize the target size in pixels.
     * @param stepwise to apply a stepwise mode scaling.
     * @return a scaled down buffered image.
     */
    public static BufferedImage downscale(BufferedImage image, int targetSize, boolean stepwise) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();

        int width = image.getWidth();
        int height = image.getHeight();

        long originalSize = width * height;

        if (targetSize < originalSize) {
            // Calculating the scale ratio and target dims
            double ratio = Math.sqrt((double) targetSize / originalSize);

            int targetWidth = (int) (width * ratio);
            int targetHeight = (int) (height * ratio);

            // Initiating width and height regarding a stepwised process or not
            int w = stepwise ? width : targetWidth;
            int h = stepwise ? height : targetHeight;

            BufferedImage scaled = (BufferedImage) image;

            do {
                // Calculating the new dims for the next step
                if (stepwise) {
                    // Downscaling in a half for each step until reach target
                    if (w > targetWidth) {
                        w /= 2;

                        // Restoring the minimum target value
                        if (w < targetWidth) {
                            w = targetWidth;
                        }
                    }

                    if (h > targetHeight) {
                        h /= 2;

                        // Restoring the minimum target value
                        if (h < targetHeight) {
                            h = targetHeight;
                        }
                    }
                }

                // Drawing the temporary scaled image regarding rendering parameters
                BufferedImage temp = new BufferedImage(w, h, type);

                Graphics2D graphics = temp.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                graphics.drawImage(scaled, 0, 0, w, h, null);
                graphics.dispose();

                // Saving scaled image for the next step if any
                scaled = temp;
            } while (w != targetWidth || h != targetHeight);

            return scaled;
        } else {
            return image;
        }
    }

    /**
     * A method down scaling a given image in the target size in pixels
     * regarding the original ratio retaining the proportions applying a
     * convolve filter given a kernel factor usually 0.05 is enough in order to
     * get soften results.
     *
     * @param image the image to be scaled.
     * @param targetSize the target size in pixels.
     * @param softenFactor the soften factor for smoother results.
     * @return the scaled image.
     */
    public static BufferedImage downscale(BufferedImage image, int targetSize, float softenFactor) {
        int width = image.getWidth();
        int height = image.getHeight();

        long originalSize = width * height;

        if (targetSize < originalSize) {
            // Calculating the scale ratio and target dims
            double ratio = Math.sqrt((double) targetSize / originalSize);

            int targetWidth = (int) (width * ratio);
            int targetHeight = (int) (height * ratio);

            // Scaling down the image towards target size
            BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = scaled.createGraphics();

            Image temp = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            graphics.drawImage(temp, 0, 0, null);
            graphics.dispose();

            // Applying convolve filter to get soften and smoother results
            float[] factors = {0, softenFactor, 0, softenFactor, 1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0};
            Kernel kernel = new Kernel(3, 3, factors);
            ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

            BufferedImage filtered = convolve.filter(scaled, null);

            return filtered;
        } else {
            return image;
        }
    }

    /**
     * A method compressing a given image using JPEG algorithm regarding the
     * size of quality after compression, where 0 means full compression and 1
     * full of quality.
     *
     * @param image the image to be compressed.
     * @param quality size of quality after compression.
     * @return a compressed buffered image.
     * @throws IOException throws unknown exceptions.
     */
    public static BufferedImage compress(BufferedImage image, float quality) throws IOException {
        // Setting up the compression algorithm
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

        ImageWriteParam parameters = writer.getDefaultWriteParam();
        parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        parameters.setCompressionQuality(quality);

        // Compressing the image
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
        writer.setOutput(ios);

        writer.write(null, new IIOImage(image, null, null), parameters);
        ios.flush();

        ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
        BufferedImage compressed = ImageIO.read(in);

        bos.close();
        ios.close();
        writer.dispose();

        return compressed;
    }
}
