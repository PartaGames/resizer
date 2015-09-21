package com.partagames.imageresizetool;

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

import static com.partagames.imageresizetool.Constants.*;

/**
 * Simple tool that takes a list of image files as arguments and saves new resized image files to the given folder.
 * Created by Antti on 18.9.2015.
 */
public class SimpleImageResizeTool {

    private static Options options;
    private static String[] imageFileStrings;
    private static int width;
    private static int height;
    private static String format;
    private static String hint;
    private static Map<String, BufferedImage> imageFiles = new HashMap<>();

    public static void main(String[] args) throws Exception {
        options = new Options();
        options.addOption(ARG_WIDTH_SHORT, ARG_WIDTH, true, "Target image width in pixels");
        options.addOption(ARG_HEIGHT_SHORT, ARG_HEIGHT, true, "Target image height in pixels");
        options.addOption(ARG_FORMAT_SHORT, ARG_FORMAT, true, "Image output format (png,jpg.gif)");
        options.addOption(ARG_OUTPUT_SHORT, ARG_OUTPUT, true, "Image output folder");
        options.addOption(ARG_HINT_SHORT, ARG_HINT, true, "Scaling hint (bicubic, bilinear)");
        options.addOption(ARG_HELP_SHORT, ARG_HELP, true, "Shows this help message.");

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

        if (cmd.hasOption(ARG_HELP)) {
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
            if (Constants.OUTPUT_IMAGE_FORMATS.contains(outputFormat)) {

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
        // automatically generate the help statement
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("resizer [options ...] [/folder/image1,/folder/image2 ...]", options);
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
     *
     * @param filePath File path
     * @return File name
     */
    private static String extractFileNameFromPath(String filePath) {
        final Path p = Paths.get(filePath);
        return p.getFileName().toString();
    }

    /**
     * Scales an image to the desired dimensions.
     *
     * @param img  Original image
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
