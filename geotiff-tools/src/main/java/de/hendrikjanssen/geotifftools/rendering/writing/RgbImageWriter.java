package de.hendrikjanssen.geotifftools.rendering.writing;

import de.hendrikjanssen.geotifftools.GeoTiff;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class RgbImageWriter implements ImageWriter {

    @Override
    public BufferedImage prepareTargetImage(GeoTiff geoTiff, int width, int height) {
        int[] matrix = new int[width * height];

        DataBufferInt buffer = new DataBufferInt(matrix, matrix.length);
        int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000};
        WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, bandMasks, null);

        ColorModel colorModel = ColorModel.getRGBdefault();
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }
}
