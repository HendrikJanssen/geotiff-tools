package de.hendrikjanssen.geotifftools.rendering;

import de.hendrikjanssen.geotifftools.GeoTiff;

import java.awt.image.BufferedImage;

public interface ImageWriter {

    BufferedImage prepareTargetImage(GeoTiff geoTiff, int width, int height);
}
