package com.partagames.imageresizetool;

import com.google.common.collect.ImmutableList;

/**
 * Created by Antti on 21.9.2015.
 */
public class Constants {
    public static final String version = "0.0.1";
    public static final ImmutableList<String> outputImageFormats = ImmutableList.of("png", "jpg", "gif");
    public static final ImmutableList<String> supportedScalingHints = ImmutableList.of("bicubic", "bilinear");
}
