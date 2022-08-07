package de.hendrikjanssen.geotifftools.rendering.sampling;

import de.hendrikjanssen.geotifftools.RasterPoint;

import java.awt.image.Raster;

public interface Sampler<O extends Sample> {

    O sample(RasterPoint samplePoint, Raster raster);
}
