package com.hendrikjanssen.geotifftools.rendering;

import com.hendrikjanssen.geotifftools.GeoTiff;

import java.awt.image.BufferedImage;

public interface ImageWriter {

    BufferedImage prepareTargetImage(GeoTiff geoTiff, int width, int height);
}
