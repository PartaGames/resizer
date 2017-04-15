package com.partagames.imagescaletool;

/**
 * Created by antti on 15/04/2017.
 */
public enum ImageFormat {

    PNG("png"),
    JPG("jpg"),
    GIF("gif");

    ImageFormat(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    public static ImageFormat getByValue(String value) {
        for (ImageFormat imageFormat : values()) {
            if (imageFormat.getValue().equals((value))) {
                return imageFormat;
            }
        }
        return null;
    }
}
