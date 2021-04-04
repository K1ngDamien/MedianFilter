import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Author:   Damien Vermaas, Amsterdam University of Applied Sciences;
 *           Wyomi Beuker, Amsterdam University of Applied Sciences
 *
 * Additional Credits: Shenbaga Prasanna,IT,SASTRA University
 * Program:  Concurrent implementation of the Median Filter, used to remove Salt and Pepper noise from an image.
 * Date:     (Created - 23/MAR/2021), (Finalized - 01/APR/2021)
 * Logic:    An image from the resources folder is imported to be ran through the median filter. The image is first
 *           split into two separate images, it's halved. These two images are then put through the median filter.
 *           The median filter retrieves a pixel from the image and checks its surrounding pixels (1+8). The different
 *           color values R, G, B (Red, Green Blue) are isolated and put in an array. This array is sorted and the
 *           median, the middle value, is selected from the array of nine pixels. This is set as the color of the
 *           target pixel and the process is repeated for the next pixel. After the two images went through this
 *           process, they are "sowed" back together as one image and the image is created as a .JPG file.
 */

class MedianFilter {

    // Two threads use 1 row 2 columns
    // Four threads use 2 row 2 columns
    // Eight threads use 2 row 4 columns
    private static final int  ROWS = 1;
    private static final int COLUMNS = 2;

    /**
     * The Parallelization class
     */
    private static Parallelization _parallelization = new Parallelization();

    /**
     * The Image Model
     */
    static public ImageModel _imageModel = new ImageModel();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        //Executing the method in the main class, timing how long it takes to get a good overview of the time distribution.
        long startTime = System.nanoTime();

        //Importing the image that will be used to remove the salt and pepper noise from.
        //The image can be changed by changing the number at the end (1-5).
        BufferedImage img = ImageIO.read(MedianFilter.class.getResource("resources/images/image1.jpg"));

        //Splitting the image depending on the amount of rows and columns.
        long splitingTime = System.nanoTime();
        imageSplitter(img);
        System.out.println("Total splitting time: " + (System.nanoTime() - splitingTime) / 1e9);

        //Executing the parallel median filter
        long algorithmTime = System.nanoTime();
        _parallelization.concurrentMedianFilter();
        System.out.println("Total median(Concurrent) time: " + (System.nanoTime() - algorithmTime) / 1e9);

        //Get the filtered images and put them back together
        long timeJoining = System.nanoTime();
        joinImage();
        System.out.println("Total joining time: " + (System.nanoTime() - timeJoining) / 1e9);

        //Total processing time
        System.out.println("Total time taken: " + (System.nanoTime() - startTime) / 1e9);
    }

    /**
     * Splits the image in a desired amount of parts
     *
     * @param image,  The image provided
     * @return The split image parts in an Array
     */
    public static void imageSplitter(BufferedImage image) {
        // Processing the photo/image to prep to be split, gathering measurements
        BufferedImage[] splitImageParts = new BufferedImage[ROWS * COLUMNS];
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        g.drawImage(image, 0, 0, null);
        int width = bi.getWidth();
        int height = bi.getHeight();
        int pos = 0;
        int swidth = width / COLUMNS;
        int sheight = height / ROWS;

        //Simply splits the image in x amount of parts, depending on column an row value
        for (int j = 0; j < COLUMNS; j++) {
            for (int i = 0; i < ROWS; i++) {
                //Save the image in  an array
                BufferedImage bimg = bi.getSubimage(j * swidth, i * sheight, swidth, sheight);
                splitImageParts[pos] = bimg;
                pos++;
            }
        }

        //Putting the image parts in the setter array
        for (int i = 0; i < splitImageParts.length; i++) {
            _imageModel.setImages(i, splitImageParts[i]);
        }
    }

    /**
     * Joins the filtered image parts and saves it as a new image
     *
     * @throws IOException
     */
    public static void joinImage() throws IOException {
        //Get the images
        BufferedImage[] imageItems = _imageModel.getImages();

        //Define size of image
        int chunkWidth, chunkHeight;
        int type;
        type = imageItems[0].getType();
        chunkWidth = imageItems[0].getWidth();
        chunkHeight = imageItems[0].getHeight();

        //Initializing the final image
        BufferedImage output = new BufferedImage(chunkWidth*COLUMNS, chunkHeight*ROWS, type);

        int num = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                output.createGraphics().drawImage(imageItems[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }

        // Creating the image file as a jpg.
        ImageIO.write(output, "jpg", new File("output.jpg"));
    }
}

/**
 * The ParallelMedianFilter, for filtering images parallel
 */
class Parallelization {

    /**
     * The concurrentMedianFilter, uses threads to run the sequential median filter parallel
     */
    public void concurrentMedianFilter() {

        BufferedImage[] imageItem = MedianFilter._imageModel.getImages();

        Thread thread1 = new Thread(() -> {
            BufferedImage filteredImage = sequentialMedianFilter(imageItem[0]);
            MedianFilter._imageModel.setImages(0, filteredImage);
        });

        Thread thread2 = new Thread(() -> {
            BufferedImage filteredImage = sequentialMedianFilter(imageItem[1]);
            MedianFilter._imageModel.setImages(1, filteredImage);
        });

//        Thread thread3 = new Thread(() -> {
//            BufferedImage filteredImage = sequentialMedianFilter(imageItem[2]);
//            MedianFilter._imageModel.setImages(2, filteredImage);
//        });
//
//        Thread thread4 = new Thread(() -> {
//            BufferedImage filteredImage = sequentialMedianFilter(imageItem[3]);
//            MedianFilter._imageModel.setImages(3, filteredImage);
//        });

//        Thread thread5 = new Thread(() -> {
//            BufferedImage filteredImage = sequentialMedianFilter(imageItem[0]);
//            MedianFilter._imageModel.setImages(4, filteredImage);
//        });
//
//        Thread thread6 = new Thread(() -> {
//            BufferedImage filteredImage = sequentialMedianFilter(imageItem[1]);
//            MedianFilter._imageModel.setImages(5, filteredImage);
//        });
//
//        Thread thread7 = new Thread(() -> {
//            BufferedImage filteredImage = sequentialMedianFilter(imageItem[2]);
//            MedianFilter._imageModel.setImages(6, filteredImage);
//        });
//
//        Thread thread8 = new Thread(() -> {
//            BufferedImage filteredImage = sequentialMedianFilter(imageItem[3]);
//            MedianFilter._imageModel.setImages(7, filteredImage);
//        });

        // Start the downloads.
        thread1.start();
        thread2.start();
//        thread3.start();
//        thread4.start();
//        thread5.start();
//        thread6.start();
//        thread7.start();
//        thread8.start();


        // Wait for them both to finish
        try {
            thread1.join();
            thread2.join();
//            thread3.join();
//            thread4.join();
//            thread5.join();
//            thread6.join();
//            thread7.join();
//            thread8.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * The sequential median filter, used bij the parallel implementation
     *
     * @param img, the Image to be filtered
     * @return a filtered image
     */
    public static BufferedImage sequentialMedianFilter(BufferedImage img) {

        //Setting up arrays for the pixels surrounding the pixel in its 3 colors (Red, Blue, Green).
        int[] R = new int[9];
        int[] B = new int[9];
        int[] G = new int[9];
        Color[] pixel = new Color[9];

        // Looping through the width and height of the image, gathering the values from each pixel and picking the median.
        for (int i = 1; i < img.getWidth() - 1; i++)
            for (int j = 1; j < img.getHeight() - 1; j++) {
                pixel[0] = new Color(img.getRGB(i - 1, j - 1));
                pixel[1] = new Color(img.getRGB(i - 1, j));
                pixel[2] = new Color(img.getRGB(i - 1, j + 1));
                pixel[3] = new Color(img.getRGB(i, j + 1));
                pixel[4] = new Color(img.getRGB(i + 1, j + 1));
                pixel[5] = new Color(img.getRGB(i + 1, j));
                pixel[6] = new Color(img.getRGB(i + 1, j - 1));
                pixel[7] = new Color(img.getRGB(i, j - 1));
                pixel[8] = new Color(img.getRGB(i, j));
                for (int k = 0; k < 9; k++) {
                    R[k] = pixel[k].getRed();
                    B[k] = pixel[k].getBlue();
                    G[k] = pixel[k].getGreen();
                }
                Arrays.sort(R);
                Arrays.sort(G);
                Arrays.sort(B);
                img.setRGB(i, j, new Color(R[4], B[4], G[4]).getRGB());
            }

        return img;
    }
}
