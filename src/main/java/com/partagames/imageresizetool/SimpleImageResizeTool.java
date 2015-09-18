package com.partagames.imageresizetool;

import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple image resize tool that takes a list of image
 * files as arguments and saves new resized image files to the given folder.
 * Created by Antti on 18.9.2015.
 */
public class SimpleImageResizeTool {

    private static String[] imageFileStrings;
    private static int width;
    private static int height;
    private static Map<String, BufferedImage> imageFiles = new HashMap<>();

    public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption("i", true, "List of image files to be resized");
        options.addOption("t", true, "Target folder where resized images are saved");
        options.addOption("w", true, "Target width");
        options.addOption("h", true, "Target height");

        parseArguments(args, options);
        createBufferedImages();
        resizeAndWriteImages();
    }

    private static void parseArguments(String[] args, Options options) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("i")) {
            final String imageFileListString = cmd.getOptionValue("i");
            imageFileStrings = imageFileListString.split(",");
        }
        if (cmd.hasOption("t")) {
            System.out.println("Got target folder! " + cmd.getOptionValue("t"));
        }
        if (cmd.hasOption("w")) {
            final String widthString = cmd.getOptionValue("w");
            try {
                width = Integer.parseInt(widthString);
            } catch (Exception e) {
                System.out.println("Width argument was not a number!");
            }
        }
        if (cmd.hasOption("h")) {
            final String heightString = cmd.getOptionValue("h");
            try {
                height = Integer.parseInt(heightString);
            } catch (Exception e) {
                System.out.println("Height argument was not a number!");
            }
        }
    }

    private static void createBufferedImages() {
        for (int i = 0; i < imageFileStrings.length; i++) {
            try {
                imageFiles.put(imageFileStrings[i], ImageIO.read(new File(imageFileStrings[i])));
            } catch (IOException e) {
                System.out.println("Image " + imageFileStrings[i] + " corrupted or not supported, ignoring...");
            }
        }
    }

    private static void resizeAndWriteImages() {

        final File outputFolder = new File("output/");
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        int i = 0;
        for (String key : imageFiles.keySet()) {
            i++;
            final BufferedImage image = imageFiles.get(key);
            final BufferedImage scaledImage = resizeImg(image, width, height);
            try {
                ImageIO.write(scaledImage, "png", new File("output/" + width + "_x_" + height + "_image_" + i + ".png"));
            } catch (IOException e) {
                System.out.println("Cannot write " + key + " to output folder. Ignoring...");
            }
        }
    }

    /**
     * scale image
     *
     * @param sbi       image to scale
     * @param imageType type of image
     * @param dWidth    width of destination image
     * @param dHeight   height of destination image
     * @param fWidth    x-factor for transformation / scaling
     * @param fHeight   y-factor for transformation / scaling
     * @return scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }

    public static BufferedImage resizeImg(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        final BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        final Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

}
