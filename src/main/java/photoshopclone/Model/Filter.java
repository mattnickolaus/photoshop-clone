package photoshopclone.Model;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.util.List;

public class Filter {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static BufferedImage applyBrightnessContrast(BufferedImage src, double alpha, int beta) {
        Mat srcMat = bufferedImageToMat(src);
        Mat dstMat = new Mat();
        srcMat.convertTo(dstMat, -1, alpha, beta);
        return matToBufferedImage(dstMat);
    }

    /**
     * Adjust saturation by converting to HSV, scaling S channel, and converting back.
     * saturation parameter: -100 to 100
     */
    public static BufferedImage applySaturation(BufferedImage src, double saturation) {
        Mat mat = bufferedImageToMat(src);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);

        // saturation factor: 1.0 means no change, <1 means decrease, >1 means increase
        double factor = (saturation / 100.0) + 1.0;

        // Modify S channel
        List<Mat> channels = new java.util.ArrayList<>();
        Core.split(mat, channels);
        Mat S = channels.get(1);
        S.convertTo(S, S.type(), factor, 0);
        Core.merge(channels, mat);

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_HSV2BGR);
        return matToBufferedImage(mat);
    }

    /**
     * Vibrance adjustment is trickier; we'll treat vibrance like saturation for demonstration.
     */
    public static BufferedImage applyVibrance(BufferedImage src, double vibrance) {
        // For demonstration, just reuse saturation
        return applySaturation(src, vibrance / 2.0); // less aggressive than saturation
    }

    /**
     * Warmth/Tint adjustment: We'll approximate by shifting color balance in BGR space.
     * warmth > 0: add warmth (reduce blue, increase red)
     * tint > 0: add green/magenta shift (for simplicity, increase green if positive, increase magenta if negative)
     */
    public static BufferedImage applyWarmthTint(BufferedImage src, double warmth, double tint) {
        Mat mat = bufferedImageToMat(src);

        // We'll adjust channels directly:
        // warmth: positive = more red/yellow (increase R, decrease B)
        // tint: positive = more green, negative = more magenta (increase G or R+B)
        double warmFactor = warmth / 100.0;
        double tintFactor = tint / 100.0;

        // Convert to float for precision
        mat.convertTo(mat, CvType.CV_32F);
        List<Mat> channels = new java.util.ArrayList<>();
        Core.split(mat, channels);
        Mat B = channels.get(0);
        Mat G = channels.get(1);
        Mat R = channels.get(2);

        // Apply warmth: increase R, decrease B
        if (warmFactor != 0) {
            Core.add(R, new Scalar(20 * warmFactor), R);
            Core.add(B, new Scalar(-20 * warmFactor), B);
        }

        // Apply tint: positive = more green, negative = more magenta
        if (tintFactor != 0) {
            if (tintFactor > 0) {
                // Increase green
                Core.add(G, new Scalar(20 * tintFactor), G);
            } else {
                // Increase magenta (red + blue)
                double mag = Math.abs(tintFactor);
                Core.add(R, new Scalar(10 * mag), R);
                Core.add(B, new Scalar(10 * mag), B);
            }
        }

        Core.merge(channels, mat);
        mat.convertTo(mat, CvType.CV_8U);
        return matToBufferedImage(mat);
    }

    /**
     * Sharpness: We'll apply a simple unsharp mask or sharpen kernel.
     * amount: -1 to 1, 0 means no change, positive = sharpen, negative = blur.
     */
    public static BufferedImage applySharpness(BufferedImage src, double amount) {
        Mat mat = bufferedImageToMat(src);

        if (amount == 0) return src;

        // Create a sharpen kernel
        // For positive amount, sharpen. For negative, blur.
        double alpha = amount;
        // A simple sharpen kernel: center = 1+alpha, others = -alpha/8
        // If alpha positive: sharpen
        // If alpha negative: blur (since negative alpha reduces center)
        float centerVal = (float)(1.0 + alpha);
        float edgeVal = (float)(-alpha / 8.0f);
        Mat kernel = new Mat(3,3,CvType.CV_32F);
        kernel.put(0,0, edgeVal, edgeVal, edgeVal,
                edgeVal, centerVal, edgeVal,
                edgeVal, edgeVal, edgeVal);

        Mat dst = new Mat();
        Imgproc.filter2D(mat, dst, mat.depth(), kernel);
        return matToBufferedImage(dst);
    }

    private static Mat bufferedImageToMat(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        int type = bi.getType();

        // If not already a byte-based type, convert it
        if (type != BufferedImage.TYPE_3BYTE_BGR && type != BufferedImage.TYPE_4BYTE_ABGR) {
            BufferedImage converted = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = converted.createGraphics();
            g.drawImage(bi, 0, 0, null);
            g.dispose();
            bi = converted;
            type = bi.getType();
        }

        int channels = bi.getRaster().getNumDataElements();
        Mat mat = new Mat(height, width, (channels == 3) ? CvType.CV_8UC3 : CvType.CV_8UC4);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);

        if (channels == 3) {
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
        }

        return mat;
    }


    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = (mat.channels() == 3) ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_4BYTE_ABGR;
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
        if (mat.channels() == 3) {
            // Convert RGB to BGR
            byte b;
            for (int i = 0; i < data.length; i += 3) {
                b = data[i];
                data[i] = data[i + 2];
                data[i + 2] = b;
            }
        }
        return image;
    }
}