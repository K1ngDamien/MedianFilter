import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import javax.imageio.*;
/*
 * Author:   Damien Vermaas, Amsterdam University of Applied Sciences;
 *           Wyomi Beuker, Amsterdam University of Applied Sciences
 *
 * Additional Credits: Shenbaga Prasanna,IT,SASTRA University
 * Program:  Sequential implementation of the Median Filter, used to remove Salt and Pepper noise from an image.
 * Date:     (Created - 22/MAR/2021), (Finalized - 25/MAR/2021)
 * Logic:    An image from the resources folder is imported to be ran through the median filter.
 *           The median filter retrieves a pixel from the image and checks its surrounding pixels (1+8). The different
 *           color values R, G, B (Red, Green Blue) are isolated and put in an array. This array is sorted and the
 *           median, the middle value, is selected from the array of nine pixels. This is set as the color of the
 *           target pixel and the process is repeated for the next pixel. After the image goes through this
 *           process, the image is created as a .JPG file.
 */

class MedianFilter{
    public static void main(String[] a)throws Throwable{

        System.out.println("Starting Sequential Median Process ...");

        BufferedImage img = ImageIO.read(MedianFilter.class.getResource("/resources/images/image1.jpg"));

        long start  = System.nanoTime();
        sequentialMedianFilter(img);
        System.out.println("Total sequential time: " + (System.nanoTime() - start) / 1e9);
    }

    public static void sequentialMedianFilter (BufferedImage img) throws IOException {

        Color[] pixel=new Color[9];
        int[] R=new int[9];
        int[] B=new int[9];
        int[] G=new int[9];
        File output = new File("output.jpg");

        for(int i=1;i<img.getWidth()-1;i++)
            for(int j=1;j<img.getHeight()-1;j++)
            {
                pixel[0]=new Color(img.getRGB(i-1,j-1));
                pixel[1]=new Color(img.getRGB(i-1,j));
                pixel[2]=new Color(img.getRGB(i-1,j+1));
                pixel[3]=new Color(img.getRGB(i,j+1));
                pixel[4]=new Color(img.getRGB(i+1,j+1));
                pixel[5]=new Color(img.getRGB(i+1,j));
                pixel[6]=new Color(img.getRGB(i+1,j-1));
                pixel[7]=new Color(img.getRGB(i,j-1));
                pixel[8]=new Color(img.getRGB(i,j));
                for(int k=0;k<9;k++){
                    R[k]=pixel[k].getRed();
                    B[k]=pixel[k].getBlue();
                    G[k]=pixel[k].getGreen();
                }
                Arrays.sort(R);
                Arrays.sort(G);
                Arrays.sort(B);
                img.setRGB(i,j,new Color(R[4],B[4],G[4]).getRGB());
            }
        ImageIO.write(img,"jpg",output);

    }
}
