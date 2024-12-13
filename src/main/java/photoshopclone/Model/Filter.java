package photoshopclone.Model;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

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

    private static Mat bufferedImageToMat(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        int channels = bi.getRaster().getNumDataElements();
        Mat mat = new Mat(height, width, channels == 3 ? CvType.CV_8UC3 : CvType.CV_8UC4);
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