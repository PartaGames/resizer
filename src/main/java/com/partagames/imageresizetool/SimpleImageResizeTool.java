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
    
    private static final ImmutableList<String> outputImageFormats = ImmutableList.of("png","jpg","gif");
    private static final ImmutableList<String> supportedScalingHints = ImmutableList.of("bicubic", "bilinear");

    public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption("images", true, "Comma separated list of image files");
        options.addOption("target", true, "Target folder where resized images are saved");
        options.addOption("width", true, "Target image width");
        options.addOption("height", true, "Target image height");
        options.addOption("format", true, "Target image format");
        options.addOption("hint", true, "Scaling hint");
        options.addOption("help", "Help/Usage");
        
        if (parseAndPrepareArguments(args, options)) {
            createBufferedImages();
            resizeAndWriteImages();
        }
    }

    private static boolean parseAndPrepareArguments(String[] args, Options options) {
        // parse through arguments and prepare them appropriately
        
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("There was a problem parsing the command line arguments, please check your command.");
            return false;
        }

        if (cmd.hasOption("help")) {
            printHelpAndUsage();
            return false;
        }
        
        // prepare required arguments
        boolean requiredArgumentMissing = false;
        if (cmd.hasOption("images") && !cmd.getOptionValue("images").isEmpty()) {
            final String imageFileListString = cmd.getOptionValue("images");
            imageFileStrings = imageFileListString.split(",");
        } else {
            requiredArgumentMissing = true;
        }
        if (cmd.hasOption("width") && !cmd.getOptionValue("width").isEmpty()) {
            final String widthString = cmd.getOptionValue("width");
            try {
                width = Integer.parseInt(widthString);
            } catch (Exception e) {
                System.out.println("Width argument was not a number!");
                requiredArgumentMissing = true;
            }
        } else {
            requiredArgumentMissing = true;
        }
        if (cmd.hasOption("height") && !cmd.getOptionValue("height").isEmpty()) {
            final String heightString = cmd.getOptionValue("height");
            try {
                height = Integer.parseInt(heightString);
            } catch (Exception e) {
                System.out.println("Height argument was not a number!");
                requiredArgumentMissing = true;
            }
        } else {
            requiredArgumentMissing = true;
        }
        
        // stop execution if a required argument is missing
        if (requiredArgumentMissing) {
            printHelpAndUsage();
            return false;
        }

        // prepare optional arguments
        if (cmd.hasOption("target")) {
            System.out.println("Got target folder! " + cmd.getOptionValue("target"));
        }
        if (cmd.hasOption("format")) {
            final String outputFormat = cmd.getOptionValue("format").toLowerCase();
            if (outputImageFormats.contains(outputFormat)) {
                
            } else {
                System.out.println("Error: Wrong output image format");
                printHelpAndUsage();
                return false;
            }
        }
        if (cmd.hasOption("target")) {
            System.out.println("Got target folder! " + cmd.getOptionValue("target"));
        }
        
        return true;
    }

    private static void printHelpAndUsage() {
        System.out.println("--- Simple Image Resize Tool v" + version + " ---");
        System.out.println("           Parta Games 2015");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("resize -images <comma separated list of image files> -width <target width> -height <target height>");
        System.out.println("[optional arguments: -target <target output folder> -format <output image format> -hint <scaling hint>]");
        System.out.println();
        System.out.println("Input and output image formats: " + outputImageFormats.toString());
        System.out.println("Scaling hints: " + supportedScalingHints.toString());
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
