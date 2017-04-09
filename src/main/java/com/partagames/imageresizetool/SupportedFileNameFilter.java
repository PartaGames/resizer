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
        for (String supportedFileExtension : Constants.OUTPUT_IMAGE_FORMATS) {
            fileIsSupported = name.toLowerCase().contains(("." + supportedFileExtension).toLowerCase());
            if (fileIsSupported) {
                break;
            }
        }
        return fileIsSupported;
    }

}
