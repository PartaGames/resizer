package com.partagames.imageresizetool;

/**
 * Created by Antti on 21.9.2015.
 */
public class Dimensions {

    final int width;
    final int height;

    public Dimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dimensions that = (Dimensions) o;

        if (width != that.width) return false;
        return height == that.height;

    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }
}
