package de.hendrikjanssen.geotifftools.rendering;

import de.hendrikjanssen.geotifftools.rendering.coloring.ColorTransform;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sampler;
import de.hendrikjanssen.geotifftools.rendering.transforming.SampleTransform;
import de.hendrikjanssen.geotifftools.rendering.writing.ImageWriter;

import java.util.ArrayList;
import java.util.List;

public class GeoTiffRendererBuilder {

    private final Sampler<?> sampler;

    private final List<SampleTransform> transforms;

    private ColorTransform colorTransform;

    private ImageWriter imageWriter;

    GeoTiffRendererBuilder(Sampler<?> sampler) {
        this.sampler = sampler;
        this.transforms = new ArrayList<>(1);
    }

    public static GeoTiffRendererBuilder sampleWith(Sampler<?> sampler) {
        return new GeoTiffRendererBuilder(sampler);
    }

    public GeoTiffRendererBuilder then(SampleTransform sampleTransform) {
        this.transforms.add(sampleTransform);
        return this;
    }

    public GeoTiffRendererBuilder colorWith(ColorTransform colorTransform) {
        this.colorTransform = colorTransform;
        return this;
    }

    public GeoTiffRendererBuilder writeWith(ImageWriter imageWriter) {
        this.imageWriter = imageWriter;
        return this;
    }

    public GeoTiffRenderer build() {
        return new GeoTiffRenderer(this.sampler, this.transforms, this.colorTransform, this.imageWriter);
    }
}
