/*
* Copyright 2015 Parta Games Oy
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.partagames.imageresizetool;

import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
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
    private static Dimensions dimensions;
    private static Path outputFolder;
    private static String format = OUTPUT_IMAGE_FORMATS.get(0); // default png
    private static String scalingHint = SUPPORTED_SCALING_HINTS.get(1); // default bilinear

    private static final Map<String, BufferedImage> imageFiles = new HashMap<>();

    public static void main(String[] args) throws Exception {
        options = new Options();

        // required options
        options.addOption(Option.builder(ARG_DIMENSIONS_SHORT).longOpt(ARG_DIMENSIONS).hasArg(true).optionalArg(false)
                .desc("Target image dimensions in pixels (e.g 1280x720)").required(true).build());

        // optional options
        options.addOption(Option.builder(ARG_FORMAT_SHORT).longOpt(ARG_FORMAT).hasArg(true).optionalArg(false)
                .desc("Image output format (png,jpg,gif)").required(false).build());
        options.addOption(Option.builder(ARG_OUTPUT_SHORT).longOpt(ARG_OUTPUT).hasArg(true).optionalArg(false)
                .desc("Image output folder").required(false).build());
        options.addOption(Option.builder(ARG_HINT_SHORT).longOpt(ARG_HINT).hasArg(true).optionalArg(false)
                .desc("Scaling hint (n=nearest, b=bilinear)").required(false).build());
        options.addOption(Option.builder(ARG_HELP_SHORT).longOpt(ARG_HELP).hasArg(false)
                .desc("Shows this help message.").required(false).build());

        if (parseAndPrepareArguments(args, options)) {
            createBufferedImages();
            resizeAndWriteImages();
        }
    }

    /**
     * Parses all command line arguments and prepares them.
     *
     * @param args    Command line arguments.
     * @param options Apache CLI options
     * @return True if arguments were prepared correctly and we can continue execution
     */
    private static boolean parseAndPrepareArguments(String[] args, Options options) {
        // parse through arguments and prepare them appropriately

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd;

        System.out.println("Resizer v" + Constants.VERSION + "\n");

        try {
            cmd = parser.parse(options, args);
        } catch (MissingOptionException | MissingArgumentException e) {
            System.out.println(e.getMessage() + "\n");
            printHelpAndUsage();
            return false;
        } catch (ParseException e2) {
            System.out.println("Error: There was a problem parsing the command line arguments, please check your command.\n");
            printHelpAndUsage();
            throw new RuntimeException(e2);
        }

        // show help
        if (cmd.hasOption(ARG_HELP)) {
            printHelpAndUsage();
            return false;
        }

        if (cmd.getArgList().isEmpty()) {
            System.out.println("Error: Missing argument: comma-separated list of images!\n");
            printHelpAndUsage();
            return false;
        } else {
            final String folderArg = cmd.getArgList().get(0);
            final File folder = new File(folderArg);
            if (folder.isDirectory()) {
                // folder was directory, find all supported image files inside
                final File[] imageFiles = folder.listFiles(new SupportedFileNameFilter());
                if (imageFiles.length > 0) {
                    imageFileStrings = new String[imageFiles.length];
                    for (int i = 0; i < imageFiles.length; i++) {
                        imageFileStrings[i] = imageFiles[i].getAbsolutePath();
                    }
                } else {
                    System.out.println("No input files found in directory: " + folderArg);
                    return false;
                }
            } else {
                imageFileStrings = folderArg.split(",");
            }
        }


        // prepare mandatory arguments
        if (cmd.hasOption(ARG_DIMENSIONS)) {
            final String[] dimensionStrings = cmd.getOptionValue(ARG_DIMENSIONS).split("x");
            try {
                dimensions = new Dimensions(Integer.parseInt(dimensionStrings[0]), Integer.parseInt(dimensionStrings[1]));
            } catch (Exception e) {
                System.out.println("Error: Dimension argument was not correct!\n");
                printHelpAndUsage();
                return false;
            }
        }

        // prepare optional arguments
        if (cmd.hasOption(ARG_OUTPUT)) {
            outputFolder = Paths.get(cmd.getOptionValue(ARG_OUTPUT));
        }
        if (cmd.hasOption(ARG_FORMAT)) {
            final String outputFormatString = cmd.getOptionValue("format").toLowerCase();
            if (Constants.OUTPUT_IMAGE_FORMATS.contains(outputFormatString)) {
                format = outputFormatString;
            } else {
                System.out.println("Error: Wrong output image format!\n");
                printHelpAndUsage();
                return false;
            }
        }
        
        if (cmd.hasOption(ARG_HINT)) {
            final String scalingHintString = cmd.getOptionValue(ARG_HINT);
            if (SUPPORTED_SCALING_HINTS.contains(scalingHintString)) {
                scalingHint = scalingHintString;
            } else {
                System.out.println("Error: Wrong scaling hint!\n");
                printHelpAndUsage();
                return false;
            }
        }

        return true;
    }

    /**
     * Prints help and usage.
     */
    private static void printHelpAndUsage() {
        // generate the help statement
        final HelpFormatter formatter = new HelpFormatter();
        System.out.println("usage: resizer [options ...] [/folder-with-images]");
        formatter.printHelp("resizer [options ...] [/folder/image1,/folder/image2 ...]", options);
    }

    /**
     * Reads the images to memory.
     */
    private static void createBufferedImages() {
        for (int i = 0; i < imageFileStrings.length; i++) {
            try {
                imageFiles.put(imageFileStrings[i], ImageIO.read(new File(imageFileStrings[i])));
            } catch (IOException e) {
                System.out.println("Warning: File " + imageFileStrings[i] + " missing, corrupted or not supported, ignoring...");
            }
        }
    }

    /**
     * Resizes and writes the images to the given or default output folder.
     */
    private static void resizeAndWriteImages() {

        File outputFolderFile;
        // if output folder is given as cli option
        if (outputFolder != null) {
            outputFolderFile = outputFolder.toFile();
            if (!outputFolderFile.exists()) {
                outputFolderFile.mkdirs();
            }
        } else {
            // default output folder
            outputFolderFile = new File("output/");
            if (!outputFolderFile.exists()) {
                outputFolderFile.mkdirs();
            }
        }

        // resize and write images
        for (String key : imageFiles.keySet()) {
            final String fileName = extractFileNameFromPath(key);

            final BufferedImage image = imageFiles.get(key);
            final BufferedImage scaledImage = scale(image, dimensions.width, dimensions.height);
            try {
                ImageIO.write(scaledImage, format,
                        new File(outputFolderFile.getPath() + "/" + dimensions.width + "_x_" + dimensions.height + "_" + fileName + "." + format));
            } catch (IOException e) {
                System.out.println("Error: Cannot write " + key + " to output folder. Ignoring...");
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

        // use provided rendering hint, default is bilinear 
        switch (scalingHint) {
            case "n":
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                break;
            case "b":
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                break;
        }

        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

}
