package me.pandora.image;

/**
 * A computer vision purposed feature description.
 *
 * @author Akis Papadopoulos
 */
public class Description {

    // Descriptors
    private double[][] descriptors;

    /**
     * A constructor creating a description given a list of local descriptors.
     *
     * @param descriptors the list of local descriptors.
     */
    public Description(double[][] descriptors) {
        this.descriptors = descriptors;
    }

    /**
     * A constructor creating a description given a global descriptor.
     *
     * @param descriptor the global descriptor.
     */
    public Description(double[] descriptor) {
        descriptors = new double[1][];

        descriptors[0] = descriptor;
    }

    public double[][] getDescriptors() {
        return descriptors;
    }

    public double[] getDescriptor(int index) {
        if (index > 0 && index < descriptors.length) {
            return descriptors[index];
        } else {
            return null;
        }
    }
}
