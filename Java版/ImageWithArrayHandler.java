import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageWithArrayHandler {
    public static void main(String[] args) {
        // 读取图片到BufferedImage
        BufferedImage bf = readImage("D:\\Programimg\\JAVA\\ImageHandler\\input.jpg");
        // 将图片转换为二维数组
        int width = bf.getWidth();
        int height = bf.getHeight();
        int[][] rawArray = convertImageToArray(bf);
        int[][] newArray = new int[height - 2][width - 2];
        int[][] kernel = new int[3][3];
        System.out.println("开始");
        convolve(rawArray, newArray, kernel);
        System.out.println("结束");
        // 输出图片到指定文件
        writeImageFromArray("D:\\Programimg\\JAVA\\ImageHandler\\output.jpg", "jpg", newArray);
        // 这里写你要输出的绝对路径+文件名
        System.out.println("图片输出完毕!");
    }

    private static int ctrlLim(int num) {
        if (num > 255) {return 255;}
        if (num < 0) {return 0;}
        return num;
    }

    private static int rgbToGray(int color) {
        int red = (color & 0x00FF0000) >> 16;
        int green = (color & 0x0000FF00) >> 8;
        int blue = (color & 0x000000FF);
        return (red + green + blue) / 3;
    }

    private static int grayToRgb(int color) {
        return color * (65536 + 256 + 1);
    }


    private static void convolve(int[][] rawArray, int[][] newArray, int[][] kernel) {
        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;
        int pictureHeight = rawArray.length;
        int pictureWidth = rawArray[0].length;
        int r = (kernelHeight - 1) / 2;
        //整张图片黑白化
        for (int j = 0; j < pictureHeight; j++) {
            for (int i = 0; i < pictureWidth; i++) {
                rawArray[j][i] = rgbToGray(rawArray[j][i]);
            }
        }

        for (int j = 0; j < pictureHeight - kernelHeight + 1; j++) {
            for (int i = 0; i < pictureWidth - kernelWidth + 1; i++) {
                int rawColor = rawArray[j + r][i + r];
                //找到最大值
                int maxColor = 0;                
                for (int m = 0; m < kernelHeight; m++) {
                    for (int n = 0; n < kernelWidth; n++) {
                        if (rawArray[j + m][i + n] > maxColor) {
                            maxColor = rawArray[j + m][i + n];
                        }
                    }
                }
                //反色
                maxColor = 255 -maxColor;
                //颜色减淡
                int newColor = 0;
                if (rawColor == 255 || maxColor == 255) {
                    newColor = 255;
                } else {
                    newColor = (int)(rawColor / (1.0 - maxColor / 255.0));
                }
                newArray[j][i] = grayToRgb(ctrlLim(newColor));
            }
        }
    }

    public static BufferedImage readImage(String imageFile) {
        File file = new File(imageFile);
        BufferedImage bf = null;
        try {
            bf = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bf;
    }

    public static int[][] convertImageToArray(BufferedImage bf) {
        // 获取图片宽度和高度
        int width = bf.getWidth();
        int height = bf.getHeight();
        // 将图片sRGB数据写入一维数组
        int[] data = new int[width * height];
        bf.getRGB(0, 0, width, height, data, 0, width);
        // 将一维数组转换为为二维数组
        int[][] rgbArray = new int[height][width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                rgbArray[i][j] = data[i * width + j];
        return rgbArray;
    }

    public static void writeImageFromArray(String imageFile, String type, int[][] rgbArray) {
        // 获取数组宽度和高度
        int width = rgbArray[0].length;
        int height = rgbArray.length;
        // 将二维数组转换为一维数组
        int[] data = new int[width * height];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                data[i * width + j] = rgbArray[i][j];
        // 将数据写入BufferedImage
        BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        bf.setRGB(0, 0, width, height, data, 0, width);
        // 输出图片
        try {
            File file = new File(imageFile);
            ImageIO.write((RenderedImage) bf, type, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
