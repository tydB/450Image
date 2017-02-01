// CMPT450: Winter, 2017
// Starting point for Labs and Assignments - you should change this comment
//
// Instructor: Michael Janzen
// Student: Tyler deBoon
// SID: 120030
// Jan. 17, 2017

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.util.Arrays;


// This class loads two images and allows grayscale and sepia colouring of the images
// Eventually more convolutions will be added
public class Convolution extends JComponent implements KeyListener {
	public BufferedImage imageKings;
	public BufferedImage imageChristmas;
	public BufferedImage imageOutside;
	public BufferedImage image;
	
	// Construct the frame and make it exit when the x button is clicked
	//
	public static void main(String[] args) {
		JFrame f = new JFrame ("CMPT 450 - Assignment ");
		
		// Make the window closed when the x button is clicked
		// This makes a new instance that overrides the windowClosing function
		// Hey look - it's an anonymous inner class - you did learn something from CMPT 305 ?
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		Convolution convolution = new Convolution();
		
		f.add(convolution);  // put the panel with the image in the frame
		f.pack();			// layout the frame
		f.setVisible(true);	// show the frame
		f.addKeyListener(convolution); // make the window respond to keyboard button presses
	}
	
	// Constructor
	// Load the images that the program will work with
	public Convolution() {
		try
		{
			// Load the image, plus keep a copy of each image so that image can
			// be easily reset
			// Images are hard coded - should be fine for purposes of this assignment
			imageKings = ImageIO.read(new File("2011-01-26_13_31_24.jpg"));
			imageChristmas = ImageIO.read(new File("2011-01-26_14_18_03.jpg"));
			imageOutside = ImageIO.read(new File("IMG_2145.jpg"));
			image = ImageIO.read(new File("2011-01-26_13_31_24.jpg"));
			
			System.out.println("The image has been loaded and is of size "+image.getWidth(null)+" by "+image.getHeight(null));
			
			// Example code for playing with the first pixel
			//System.out.println("The first pixel is: "+image.getRGB(0,0));
			//System.out.println("Alpha = "+getAlpha(image.getRGB(0,0)));
			//System.out.println("Red = "+getRed(image.getRGB(0,0)));
			//System.out.println("Green = "+getGreen(image.getRGB(0,0)));
			//System.out.println("Blue = "+getBlue(image.getRGB(0,0)));
			
		}
		catch (Exception e) // Generic Exception handler with information on what happened
		{
			System.out.println("There was a problem loading the image\n"+e);
		}	
	}
	
	// Grayscale the image using 0.3R+0.59G+0.11B.
	// This follows that our eye is most sensative to green and least sensative to blue
	public void grayScale() {
		//System.out.println("Gray scaling the image");
		
		// For each row
		for(int j=0; j<image.getHeight(); j++)
		{
			// For each column
			for(int i=0; i<image.getWidth(); i++)
			{
				// using Y = 0.3*R + 0.59*G + 0.11*B to grayscale
				int gs = (int)(0.3*(double)getRed(image.getRGB(i,j)) + 0.59*(double)getGreen(image.getRGB(i,j)) + 0.11*(double)getBlue(image.getRGB(i,j)));
				image.setRGB(i, j, makeColour(gs, gs, gs) );
			}
		}
		repaint(); // request the image be redrawn
	}
	
	// Loop through the image and change the red, green, and blue component to sepia
	// This assumes that the black value is (R,G,B) = (112,66,20) and white is (255, 255, 255)
	public void sepia() {
		//System.out.println("Applying Sepia");
		// For each row
		for(int j=0; j<image.getHeight(); j++)
		{
			// For each column
			for(int i=0; i<image.getWidth(); i++)
			{
				// Linearly interpolate between the sepia colour and white
				double sepia = (0.33*(double)getRed(image.getRGB(i,j)) + 0.33*(double)getGreen(image.getRGB(i,j)) + 0.33*(double)getBlue(image.getRGB(i,j)))/255.0;
				image.setRGB(i, j, makeColour( (int)((1-sepia)*112.0+sepia*255.0), (int)((1-sepia)*66.0+sepia*255.0), (int)((1-sepia)*20.0+sepia*255.0)) );
				
			}
		}
		repaint(); // request the image be redrawn
	}

	// Invert the image
	// invert each pixel using 255 - R, 255 - g...
	public void invert() {
		// For each row
		for(int j=0; j<image.getHeight(); j++)
		{
			// For each column
			for(int i=0; i<image.getWidth(); i++)
			{
				int red = 255 - getRed(image.getRGB(i,j));
				int green = 255 - getGreen(image.getRGB(i,j));
				int blue = 255 - getBlue(image.getRGB(i,j));
				image.setRGB(i, j, makeColour(red, green, blue));
			}
		}
		repaint();
	}
	
	// Threshold the image at 127
	// convert to black and white and cutoff at 127
	public void threshold() {
		// For each row
		for(int j=0; j<image.getHeight(); j++)
		{
			// For each column
			for(int i=0; i<image.getWidth(); i++)
			{
				// to greyScale
				int gs = (int)(0.3*(double)getRed(image.getRGB(i,j)) + 0.59*(double)getGreen(image.getRGB(i,j)) + 0.11*(double)getBlue(image.getRGB(i,j)));
				if (gs > 127) {
					image.setRGB(i, j, makeColour(255, 255, 255));
				}
				else {
					image.setRGB(i, j, makeColour(0, 0, 0));
				}
			}
		}
		repaint();
	}
	
	// create a histogram and possibly draw the results to the image
	// type is a 'R' for red, 'G' for green, 'B' for blue, or 'S' for grey scale
	// draw will determine if the histogram is drawn on the image
	// returns the median of the histogram
	public int histogram(char type, boolean draw) {
		// draw values
		int redV = 0;
		int greenV = 0;
		int blueV = 0;
		// get counts
		int[] count = new int[256];
		// For each row
		for(int j=0; j<image.getHeight(); j++)
		{
			// For each column
			for(int i=0; i<image.getWidth(); i++)
			{
				if (type == 'R') {
					++count[getRed(image.getRGB(i,j))];
					redV = 255;
				}
				if (type == 'G') {
					++count[getGreen(image.getRGB(i,j))];
					greenV = 255;
				}
				if (type == 'B') {
					++count[getBlue(image.getRGB(i,j))];
					blueV = 255;
				}
				if (type == 'S') {
					int gs = (int)(0.3*(double)getRed(image.getRGB(i,j)) + 0.59*(double)getGreen(image.getRGB(i,j)) + 0.11*(double)getBlue(image.getRGB(i,j)));
					++count[gs];
					redV = 127;
					greenV = 127;
					blueV = 127;
				}
			}
		}
		int halfValue = 0;
		int halfCount = 0;
		int total = image.getHeight() * image.getWidth();
		for (int i = 0; i < count.length; ++i) {
			halfCount += count[i];
			if (halfCount > total / 2) {
				halfValue = i;
				break;
			}
		}

		if (draw) {
			// get the largest and normalize it to the height of the image being drawn on
			int largest = 0;
			for (int i = 0; i < count.length; ++i) {
				if (count[i] > largest) {
					largest = count[i];
				}
			}
			int height = image.getHeight();
			for (int i = 0; i < count.length; ++i) {
				// normalize and draw
				count[i] = (int)(((double)count[i] / (double)largest) * (double)height);
				while(count[i] > 0) {
					image.setRGB(i, height - count[i], makeColour(redV, greenV, blueV));
					--count[i];
				}
			}
			repaint();
		}
		return halfValue;
	}

	// convert to black and white but will try and correct for errors
	public void errorCorrection(int cutOff) {
		int w = image.getWidth();
		int h = image.getHeight();
		double[][] pixels = new double[w][h];
		// For each row
		for(int j=0; j<h; j++)
		{
			// For each column
			for(int i=0; i<w; i++)
			{
				// to gs
				int gs = (int)(0.3*(double)getRed(image.getRGB(i,j)) + 0.59*(double)getGreen(image.getRGB(i,j)) + 0.11*(double)getBlue(image.getRGB(i,j)));
				// w * row + c
				// into the array
				pixels[i][j] = (double)gs;
			}
		}
		double threshold = (double)cutOff;
		// For each row
		for(int j = 0; j < h - 1; j++)
		{
			// For each column
			for(int i = 1; i < w - 1; i++)
			{
				if (pixels[i][j] > threshold) { // go to 255
					double error = 255.0 - pixels[i][j] / 4;
					pixels[i + 1][j] -= error;
					pixels[i - 1][j + 1] -= error;
					pixels[i][j + 1] -= error;
					pixels[i + 1][j + 1] -= error;
					image.setRGB(i,j, makeColour(255,255,255));
				}
				else { // go to 0
					double error = pixels[i][j] / 4;
					pixels[i + 1][j] += error;
					pixels[i - 1][j + 1] += error;
					pixels[i][j + 1] += error;
					pixels[i + 1][j + 1] += error;
					image.setRGB(i,j, makeColour(0, 0, 0));
				}
			}
		}
		repaint();
	}

	// convert to black and white around a given value
	public void valueThreshold(int cutOff) {
		System.out.println("Using a threshold of " + cutOff);
		// For each row
		for(int j=0; j<image.getHeight(); j++)
		{
			// For each column
			for(int i=0; i<image.getWidth(); i++)
			{
				int gs = (int)(0.3*(double)getRed(image.getRGB(i,j)) + 0.59*(double)getGreen(image.getRGB(i,j)) + 0.11*(double)getBlue(image.getRGB(i,j)));
				if (gs > cutOff) {
					image.setRGB(i, j, makeColour(255, 255, 255));
				}
				else {
					image.setRGB(i, j, makeColour(0, 0, 0));
				}
			}
		}
		repaint();
	}

	// switch black pixels to white if they have less then num black neighbors
	public void erosion(int num) {
		// make the buffer array
		int w = image.getWidth();
		int h = image.getHeight();
		boolean[][] pixels = new boolean[w][h];
		// For each row
		for(int j=0; j<h; j++)
		{
			// For each column
			for(int i=0; i<w; i++)
			{
				// to gs
				int gs = (int)(0.3*(double)getRed(image.getRGB(i,j)) + 0.59*(double)getGreen(image.getRGB(i,j)) + 0.11*(double)getBlue(image.getRGB(i,j)));
				if (gs > 127) {
					pixels[i][j] = false; // white pixels are false
				}
				else {
					pixels[i][j] = true; // black pixels are true
				}
			}
		}
		// loop through it and set the results into the image
		for(int j=1; j<h - 1; j++) {
			// For each column
			for(int i=1; i<w - 1; i++) {
				if (pixels[i][j]) { // only for black pixels
					int n = nCount(i,j, pixels);
					if (n < num) { // pixels with less then num black neighbors turns white
						image.setRGB(i,j, makeColour(255, 255, 255));
					}
					else { // go black
						image.setRGB(i,j, makeColour(0, 0, 0));
					}
				}
				else { // white pixels stay white
					image.setRGB(i,j, makeColour(255, 255, 255));
				}
			}
		}
		repaint();
	}

	// switch white to black if they have more then num black neighbors
	public void dialation(int num) {
		// make the buffer array
		int w = image.getWidth();
		int h = image.getHeight();
		boolean[][] pixels = new boolean[w][h];
		// For each row
		for(int j=0; j<h; j++)
		{
			// For each column
			for(int i=0; i<w; i++)
			{
				// to gs
				int gs = (int)(0.3*(double)getRed(image.getRGB(i,j)) + 0.59*(double)getGreen(image.getRGB(i,j)) + 0.11*(double)getBlue(image.getRGB(i,j)));
				if (gs > 127) {
					pixels[i][j] = false; // white pixels are false
				}
				else {
					pixels[i][j] = true; // black pixels are true
				}
			}
		}
		// loop through it and set the results into the image
		for(int j=1; j<h - 1; j++) {
			// For each column
			for(int i=1; i<w - 1; i++) {
				if (!pixels[i][j]) { // only for white pixels
					int n = nCount(i,j, pixels);
					if (n >= num) { // pixels with the same or more then num black neighbors turns black
						image.setRGB(i,j, makeColour(0, 0, 0));
					}
					else { // go white
						image.setRGB(i,j, makeColour(255, 255, 255));
					}
				}
				else { // black pixels stay black
					image.setRGB(i,j, makeColour(0, 0, 0));
				}
			}
		}
		repaint();
	}

	// count the number of 'true' pixels around the row, col.
	public int nCount(int row, int col, boolean[][] pixels) { 
		int count = 0;
		for(int j = -1; j < 2; j++) {
			for(int i = -1; i < 2; i++) {
				// inbounds check
				if (row + j >= 0 && row + j < image.getWidth() && col + i >= 0 && col + i < image.getHeight()) {
					if (i != 0 || j != 0) {
						if (pixels[row + j][col + i]) {
							++count;
						}
					}
				}
			}
		}
		return count;
	}

	public void boxBlur(int size) {
		if (size % 2 != 1) {
			System.out.println("Size must be odd");
			size = 3;
		}
		System.out.println("Box Blur " + size + "x" + size);
		double[][] kernel = new double[size][size];
		for (int i = 0; i < kernel.length; ++i) {
			for (int j = 0; j < kernel[i].length; ++j) {
				kernel[i][j] = 1.0 / (double)(kernel.length * kernel[i].length);
			}
		}
		copyImage(convolution(kernel), image);
		repaint();
	}

	public void edgeDetect(int size) {
		System.out.println("Edge Detect " + size + "x" + size);
		double[][] kernel = new double[size][size];
		for (int i = 0; i < kernel.length; ++i) {
			for (int j = 0; j < kernel[i].length; ++j) {
				kernel[i][j] = -1.0 / ((double)(kernel.length * kernel[i].length) - 1.0);
			}
		}
		kernel[kernel.length / 2][kernel[0].length / 2] = 1;
		copyImage(convolution(kernel), image);
		repaint();
	}

	public void gaussianBlur(int size) {
		System.out.println("Gaussian Blur " + size + "x" + size);
		double[][] kernel = new double[size][size];
		if (size == 3) {
			kernel[0][0] = 1.0 / 16.0;
			kernel[0][1] = 1.0 / 8.0;
			kernel[0][2] = 1.0 / 16.0;
			kernel[1][0] = 1.0 / 8.0;
			kernel[1][1] = 1.0 / 4.0;
			kernel[1][2] = 1.0 / 8.0;
			kernel[2][0] = 1.0 / 16.0;
			kernel[2][1] = 1.0 / 8.0;
			kernel[2][2] = 1.0 / 16.0;
		}
		else {
			kernel[0][0] = 0.000001;
			kernel[0][1] = 0.000026;
			kernel[0][2] = 0.000208;
			kernel[0][3] = 0.000413;
			kernel[0][4] = 0.000208;
			kernel[0][5] = 0.000026;
			kernel[0][6] = 0.000001;

			kernel[1][0] = 0.000026;
			kernel[1][1] = 0.000871;
			kernel[1][2] = 0.006952;
			kernel[1][3] = 0.013811;
			kernel[1][4] = 0.006952;
			kernel[1][5] = 0.000871;
			kernel[1][6] = 0.000026;

			kernel[2][0] = 0.000208;
			kernel[2][1] = 0.006952;
			kernel[2][2] = 0.055504;
			kernel[2][3] = 0.110265;
			kernel[2][4] = 0.055504;
			kernel[2][5] = 0.006952;
			kernel[2][6] = 0.000208;

			kernel[3][0] = 0.000413;
			kernel[3][1] = 0.013811;
			kernel[3][2] = 0.110265;
			kernel[3][3] = 0.219057;
			kernel[3][4] = 0.110265;
			kernel[3][5] = 0.013811;
			kernel[3][6] = 0.000413;

			kernel[4][0] = 0.000208;
			kernel[4][1] = 0.006952;
			kernel[4][2] = 0.055504;
			kernel[4][3] = 0.110265;
			kernel[4][4] = 0.055504;
			kernel[4][5] = 0.006952;
			kernel[4][6] = 0.000208;

			kernel[5][0] = 0.000026;
			kernel[5][1] = 0.000871;
			kernel[5][2] = 0.006952;
			kernel[5][3] = 0.013811;
			kernel[5][4] = 0.006952;
			kernel[5][5] = 0.000871;
			kernel[5][6] = 0.000026;

			kernel[6][0] = 0.000001;
			kernel[6][1] = 0.000026;
			kernel[6][2] = 0.000208;
			kernel[6][3] = 0.000413;
			kernel[6][4] = 0.000208;
			kernel[6][5] = 0.000026;
			kernel[6][6] = 0.000001;
		}
		copyImage(convolution(kernel), image);
		repaint();
	}

	public void printKernel(double[][] kernel) {
		for (int i = 0; i < kernel.length; ++i) {
			String line = "[" + kernel[i][0];
			for (int j = 1; j < kernel[i].length; ++j) {
				line += ", " + kernel[i][j];
			}
			System.out.println(line + "]");
		}
	}

	public BufferedImage convolution(double[][] kernel) {
		System.out.println("Convolution Started");
		printKernel(kernel);
		// do a convolution procedure
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage newImg = new BufferedImage(w, h, image.getType());
		int kernelOffsetI = kernel.length / 2;
		int kernelOffsetJ = kernel[0].length / 2;
		// For each row
		for(int j = kernelOffsetJ; j < h - kernelOffsetJ; j++) {
			// For each column
			for(int i = kernelOffsetI; i < w - kernelOffsetI; i++) {
				double red = 0;
				for (int sampleJ = 0; sampleJ < kernel.length; ++sampleJ) {
					for (int sampleI = 0; sampleI < kernel[sampleJ].length; ++sampleI) {
						red += (double)getRed(image.getRGB(i + sampleI - kernelOffsetI, j + sampleJ - kernelOffsetJ)) * kernel[sampleI][sampleJ];
					}
				}
				// clamp the output
				if (red > 255) {
					red = 255.0;
				}
				if (red < 0) {
					red = 0;
				}
				double green = 0;
				for (int sampleJ = 0; sampleJ < kernel.length; ++sampleJ) {
					for (int sampleI = 0; sampleI < kernel[sampleJ].length; ++sampleI) {
						green += (double)getGreen(image.getRGB(i + sampleI - kernelOffsetI, j + sampleJ - kernelOffsetJ)) * kernel[sampleI][sampleJ];
					}
				}
				// clamp the output
				if (green > 255) {
					green = 255.0;
				}
				if (green < 0) {
					green = 0;
				}
				double blue = 0;
				for (int sampleJ = 0; sampleJ < kernel.length; ++sampleJ) {
					for (int sampleI = 0; sampleI < kernel[sampleJ].length; ++sampleI) {
						blue += (double)getBlue(image.getRGB(i + sampleI - kernelOffsetI, j + sampleJ - kernelOffsetJ)) * kernel[sampleI][sampleJ];
					}
				}
				// clamp the output
				if (blue > 255) {
					blue = 255.0;
				}
				if (blue < 0) {
					blue = 0;
				}
				newImg.setRGB(i, j, makeColour((int)red, (int)green, (int)blue));
			}
		}
		System.out.println("Convolution Ended");
		// copyImage(newImg, image);
		// repaint();
		return newImg;
	}

	public void gamma(double g) {
		System.out.println("Gamma Start:" + g);
		// create look up table
		int[] table = new int[256];
		for (int i = 0; i < table.length; ++i) {
			table[i] = (int)(Math.pow((double)i / 255.0, g) * 255.0);
		}
		int w = image.getWidth();
		int h = image.getHeight();
		// For each row
		for(int j = 0; j < h; j++) {
			// For each column
			for(int i = 0; i < w; i++) {
				image.setRGB(i, j, makeColour(table[getRed(image.getRGB(i,j))], table[getGreen(image.getRGB(i,j))], table[getBlue(image.getRGB(i,j))]));
			}
		}
		System.out.println("Gamma End");
		repaint();
	}

	public void medianFilter(int size) {
		System.out.println("Median Filter Start " + size + "x" + size);
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage newImg = new BufferedImage(w, h, image.getType());
		int inset = size / 2;
		// For each row
		for(int j = inset; j < h - inset; j++) {
			// For each column
			for(int i = inset; i < w - inset; i++) {
				int[] list = new int[size * size];
				for (int sampleJ = 0; sampleJ < size; ++sampleJ) {
					for (int sampleI = 0; sampleI < size; ++sampleI) {
						// think I will still have to go to grey scale and do it that way but this appears to work
						list[sampleJ * size + sampleI] = image.getRGB(i + sampleI - inset, j + sampleJ - inset);
					}
				}
				// sort array
				Arrays.sort(list);
				// pick median
				// replace in new image
				newImg.setRGB(i, j, list[list.length / 2]);
			}
		}
		System.out.println("Median Filter End");
		copyImage(newImg, image);
		repaint();
	}

	public void sharpen(int size) {
		System.out.println("Sharpen Start " + size + "x" + size);
		double[][] kernel = new double[size][size];
		for (int i = 0; i < kernel.length; ++i) {
			for (int j = 0; j < kernel[i].length; ++j) {
				kernel[i][j] = 1.0 / (double)(kernel.length * kernel[i].length);
			}
		}
		BufferedImage blur = convolution(kernel);
		BufferedImage newImg = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		for(int j = 0; j < image.getHeight(); j++) {
			// For each column
			for(int i = 0; i < image.getWidth(); i++) {
				int red = (int)((double)getRed(image.getRGB(i,j)) * 1.6 - (double)getRed(blur.getRGB(i,j)) * 0.6);
				if (red > 255) red = 255;
				if (red < 0) red = 0;
				int green = (int)((double)getGreen(image.getRGB(i,j)) * 1.6 - (double)getGreen(blur.getRGB(i,j)) * 0.6);
				if (green > 255) green = 255;
				if (green < 0) green = 0;
				int blue = (int)((double)getBlue(image.getRGB(i,j)) * 1.6 - (double)getBlue(blur.getRGB(i,j)) * 0.6);
				if (blue > 255) blue = 255;
				if (blue < 0) blue = 0;
				newImg.setRGB(i, j, makeColour(red,green,blue));
			}
		}
		copyImage(newImg, image);
		System.out.println("Sharpen End");
	}

	// These function definitions must be included to satisfy the KeyListener interface
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	
	// Respond to key pressed
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE)  System.exit(0); 	// exit when escape is pressed
		else if (e.getKeyChar() == 'i') invert();
		else if (e.getKeyChar() == 't') threshold();
		else if (e.getKeyChar() == 'l') errorCorrection(127);
		else if (e.getKeyChar() == 'T') valueThreshold(histogram('S', false));
		else if (e.getKeyChar() == 'h') histogram('R', true);
		else if (e.getKeyChar() == 'H') histogram('G', true);
		else if (e.getKeyChar() == 'j') histogram('B', true);
		else if (e.getKeyChar() == 'J') histogram('S', true);
		else if (e.getKeyChar() == '!') erosion(1); // the symbols are not in a nice order so i will leave them as individual statements
		else if (e.getKeyChar() == '@') erosion(2);
		else if (e.getKeyChar() == '#') erosion(3);
		else if (e.getKeyChar() == '$') erosion(4);
		else if (e.getKeyChar() == '%') erosion(5);
		else if (e.getKeyChar() == '^') erosion(6);
		else if (e.getKeyChar() == '&') erosion(7);
		else if (e.getKeyChar() == '*') erosion(8);
		else if ((int)e.getKeyChar() >= 49 && (int)e.getKeyChar() <= 56) dialation((int)e.getKeyChar() - 48);
		else if (e.getKeyChar() == 'e') edgeDetect(3);
		else if (e.getKeyChar() == 'E') edgeDetect(7);
		else if (e.getKeyChar() == 's') boxBlur(3);
		else if (e.getKeyChar() == 'S') boxBlur(7);
		else if (e.getKeyChar() == 'f') gaussianBlur(3);
		else if (e.getKeyChar() == 'F') gaussianBlur(7);
		else if (e.getKeyChar() == 'b') gamma(0.8);
		else if (e.getKeyChar() == 'B') gamma(0.6);
		else if (e.getKeyChar() == 'd') gamma(1.2);
		else if (e.getKeyChar() == 'D') gamma(1.4);
		else if (e.getKeyChar() == 'm') medianFilter(3);
		else if (e.getKeyChar() == 'M') medianFilter(7);
		else if (e.getKeyChar() == 'z') sharpen(3);
		else if (e.getKeyChar() == 'Z') sharpen(7);
		// existing 
		else if (e.getKeyChar() == 'g') grayScale();
		else if (e.getKeyChar() == 'G') sepia();
		else if (e.getKeyChar() == 'k') copyImage(imageKings, image); // reload the building picture
		else if (e.getKeyChar() == 'c') copyImage(imageChristmas, image); // reload the class picture
		else if (e.getKeyChar() == 'o') copyImage(imageOutside, image); // reload the class picture
	}
	
	// Return the size this component should be - usually the size of the image,
	// or 100 x 100 if the image hasn't been loaded for some reason
	public Dimension getPreferredSize() {
		if(image == null) return new Dimension(100,100);
		else return new Dimension(image.getWidth(null), image.getHeight(null));
	}
	
	// When redrawing just paint the image on the component
	public void paint(Graphics g) { g.drawImage(image, 0, 0, null); }
	
	// This function copies each pixel from the source image to the destination image
	// This function assumes that the images are the same size
	public void copyImage(BufferedImage src, BufferedImage dst) {
		for(int j=0; j<src.getHeight(); j++)
			for(int i=0; i<src.getWidth(); i++)
				dst.setRGB(i, j, src.getRGB(i,j));
		repaint(); // request the image be redrawn
	}
	
	// Some functions for getting the alpha, red, green, or blue values from an integer that
	// represents a colour
	public static int getAlpha(int pixelColour) { return (0xFF000000 & pixelColour)>>>24;}
	public static int getRed(int pixelColour) { return   (0x00FF0000 & pixelColour)>>>16;}
	public static int getGreen(int pixelColour) { return (0x0000FF00 & pixelColour)>>>8;}
	public static int getBlue(int pixelColour) { return  (0x000000FF & pixelColour);}
	
	// Given the red, green, and blue values make the colour as an integer assuming pixel is opaque
	public static int makeColour(int red, int green, int blue) {return (255<<24 | red<<16 | green << 8 | blue);}	
}