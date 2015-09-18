package com.partagames.imageresizetool;

import com.google.common.collect.ImmutableList;
import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple tool that takes a list of image files as arguments and saves new resized image files to the given folder.
 * Created by Antti on 18.9.2015.
 */
public class SimpleImageResizeTool {

    private static final String version = "0.0.1";
    private static String[] imageFileStrings;
    private static int width;
    private static int height;
    private static String format;
    private static String hint;
    private static Map<String, BufferedImage> imageFiles = new HashMap<>();
    
//    private static final List<String> allowedImageFormats = ImmutableList.of("png","jpg","gif");

    public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption("images", true, "Comma separated list of image files");
        options.addOption("target", true, "Target folder where resized images are saved");
        options.addOption("width", true, "Target image width");
        options.addOption("height", true, "Target image height");
        options.addOption("format", true, "Target image format");
        options.addOption("hint", true, "Scaling hint");
        options.addOption("help", "Help/Usage");
        
        if (parseArguments(args, options)) {
            createBufferedImages();
            resizeAndWriteImages();
        }
    }

    private static boolean parseArguments(String[] args, Options options) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            System.out.println("Simple Image Resize Tool v" + version);
            System.out.println("Parta Games 2015");
            System.out.println();
            System.out.println("Usage:");
            System.out.println("resize -images <comma separated list of image files> -width <target width> -height <target height> -t <target output folder>");
            System.out.println("[optional arguments: -format <output image format (png, jpg, gif)> -hint <scaling hint (c=cubic, b=bilinear)]");
            System.out.println();
            System.out.println("Supported input and output image files: PNG,JPG,GIF");
            return false;
        }

        if (cmd.hasOption("images")) {
            final String imageFileListString = cmd.getOptionValue("images");
            imageFileStrings = imageFileListString.split(",");
        }
        if (cmd.hasOption("target")) {
            System.out.println("Got target folder! " + cmd.getOptionValue("target"));
        }
        if (cmd.hasOption("width")) {
            final String widthString = cmd.getOptionValue("width");
            try {
                width = Integer.parseInt(widthString);
            } catch (Exception e) {
                System.out.println("Width argument was not a number!");
            }
        }
        if (cmd.hasOption("height")) {
            final String heightString = cmd.getOptionValue("height");
            try {
                height = Integer.parseInt(heightString);
            } catch (Exception e) {
                System.out.println("Height argument was not a number!");
            }
        }
        return true;
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

        // create output folder if it does not exist
        final File outputFolder = new File("output/");
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // resize and write images
        int i = 0;
        for (String key : imageFiles.keySet()) {
            i++;
            
            final String fileName = extractFileNameFromPath(key);
            
            final BufferedImage image = imageFiles.get(key);
            final BufferedImage scaledImage = scale(image, width, height);
            try {
                ImageIO.write(scaledImage, "png", new File("output/" + width + "_x_" + height + " " + fileName + ".png"));
            } catch (IOException e) {
                System.out.println("Cannot write " + key + " to output folder. Ignoring...");
            }
        }
    }

    /**
     * Extracts file name from full file path.
     * @param filePath File path
     * @return File name
     */
    private static String extractFileNameFromPath(String filePath) {
        final Path p = Paths.get(filePath);
        return p.getFileName().toString();
    }

    /**
     * Scales an image to the desired dimensions.
     * @param img Original image
     * @param newW Target width
     * @param newH Target height
     * @return Scaled image
     */
    public static BufferedImage scale(BufferedImage img, int newW, int newH) {
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
