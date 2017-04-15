package com.partagames.imageresizetool;

import java.io.File;
import java.io.FilenameFilter;

/**
 * FilenameFilter for supported image files.
 * Created by antti on 09/04/2017.
 */
public class SupportedFileNameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        boolean fileIsSupported = false;
        for (ImageFormat supportedImageFormat : ImageFormat.values()) {
            fileIsSupported = name.toLowerCase().contains(("." + supportedImageFormat.getValue()).toLowerCase());
            if (fileIsSupported) {
                break;
            }
        }
        return fileIsSupported;
    }

}
