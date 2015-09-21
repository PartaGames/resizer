package com.partagames.imageresizetool;

import com.google.common.collect.ImmutableList;

/**
 * Created by Antti on 21.9.2015.
 */
public class Constants {
    public static final String VERSION = "0.0.1";
    public static final ImmutableList<String> OUTPUT_IMAGE_FORMATS = ImmutableList.of("png", "jpg", "gif");
    public static final ImmutableList<String> SUPPORTED_SCALING_HINTS = ImmutableList.of("bicubic", "bilinear");
    
    // list the command line arguments
    public static final String ARG_DIMENSIONS_SHORT = "d";
    public static final String ARG_OUTPUT_SHORT = "o";
    public static final String ARG_FORMAT_SHORT = "f";
    public static final String ARG_HINT_SHORT = "s";
    public static final String ARG_HELP_SHORT = "h";
    
    public static final String ARG_DIMENSIONS = "dimensions";
    public static final String ARG_OUTPUT = "output";
    public static final String ARG_FORMAT = "format";
    public static final String ARG_HINT = "scalinghint";
    public static final String ARG_HELP = "help";
}
