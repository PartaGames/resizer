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
package com.partagames.imagescaletool;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Tool utility that takes a list of image files as arguments and returns new resized images as files.
 * Created by Antti on 18.9.2015.
 */
public class ImageScaleTool {

    /**
     * Scales images to given dimensions & output format using given scaling hint
     * @param imageFiles Input image files
     * @param dimensions Target dimensions
     * @param outputFormat Image output format
     * @param scalingHint Scaling hint
     * @return Output image files
     */
    public Map<String, BufferedImage> scale(Map<String, BufferedImage> imageFiles, Dimensions[] dimensions, ImageFormat outputFormat, ScalingHint scalingHint) {
        int numInputFiles = 0;
        int numOutputFiles = 0;

        final Map<String, BufferedImage> scaledImages = new HashMap<>();

        // resize and write each image file in provided dimensions
        for (String key : imageFiles.keySet()) {
            for (Dimensions d : dimensions) {
                final String fileName = extractFileNameFromFullPath(key);

                final BufferedImage image = imageFiles.get(key);
                final BufferedImage scaledImage = scale(image, d.width, d.height, scalingHint);

                scaledImages.put(buildOutputFileName(d, fileName, outputFormat), scaledImage);
                numOutputFiles++;
            }
            numInputFiles++;
        }

        System.out.println(numInputFiles + " input images resulted in " + numOutputFiles + " output images");
        return scaledImages;
    }

    /**
     * Builds an output image file name from dimensions, original file name and output image format
     * @param d Target dimensions
     * @param originalFileName Original file name
     * @param outputFormat Output image format
     * @return File name without path information
     */
    private String buildOutputFileName(final Dimensions d, final String originalFileName, final ImageFormat outputFormat) {
        return d.width + "_x_" + d.height + "_" + originalFileName + "." + outputFormat.getValue();
    }

    /**
     * Extracts a file name from full path.
     *
     * @param filePath File path
     * @return File name
     */
    private String extractFileNameFromFullPath(String filePath) {
        final Path p = Paths.get(filePath);
        return p.getFileName().toString();
    }

    /**
     * Scales an image to the desired dimensions.
     *
     * @param img  Original image
     * @param newW Target width
     * @param newH Target height
     * @param scalingHint Scaling hint to be used
     * @return Scaled image
     */
    private BufferedImage scale(final BufferedImage img, final int newW, final int newH, final ScalingHint scalingHint) {
        int w = img.getWidth();
        int h = img.getHeight();
        final BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        final Graphics2D g = dimg.createGraphics();

        // use provided rendering hint, default is bilinear 
        switch (scalingHint) {
            case NEAREST:
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                break;
            case BILINEAR:
            default:
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                break;
        }

        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

}
