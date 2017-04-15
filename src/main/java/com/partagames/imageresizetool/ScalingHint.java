package com.partagames.imageresizetool;

/**
 * Created by antti on 15/04/2017.
 */
public enum ScalingHint {

    NEAREST("n"),
    BILINEAR("b");

    ScalingHint(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    public static ScalingHint getByValue(String value) {
        for (ScalingHint scalingHint : values()) {
            if (scalingHint.getValue().equals((value))) {
                return scalingHint;
            }
        }
        return null;
    }
}
