package de.hendrikjanssen.geotifftools.rendering;

import de.hendrikjanssen.geotifftools.GeoTiff;
import de.hendrikjanssen.geotifftools.RasterPoint;
import de.hendrikjanssen.geotifftools.rendering.coloring.ColorTransform;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sample;
import de.hendrikjanssen.geotifftools.rendering.sampling.Sampler;
import de.hendrikjanssen.geotifftools.rendering.transforming.SampleTransform;
import de.hendrikjanssen.geotifftools.rendering.writing.ImageWriter;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.List;

public class GeoTiffRenderer {

    private final Sampler<?> sampler;
    private final List<SampleTransform> transforms;
    private final ColorTransform colorTransform;
    private final ImageWriter imageWriter;

    GeoTiffRenderer(Sampler<?> sampler, List<SampleTransform> transforms, ColorTransform colorTransform, ImageWriter imageWriter) {
        this.sampler = sampler;
        this.transforms = transforms;
        this.colorTransform = colorTransform;
        this.imageWriter = imageWriter;
    }

    public BufferedImage render(GeoTiff geoTiff, RenderTarget renderTarget) throws IOException {

        Rectangle sourceRect = getSourceRect(geoTiff, renderTarget);

        WritableRaster sourceRaster = geoTiff.readImageIntoBuffer().getRaster();
        BufferedImage targetImage = imageWriter.prepareTargetImage(
            geoTiff,
            renderTarget.getTargetDimension().width,
            renderTarget.getTargetDimension().height
        );

        for (SampleTransform transform : transforms) {
            if (transform instanceof StatefulBegin statefulBegin) {
                statefulBegin.onBegin(geoTiff);
            }
        }

        if (colorTransform instanceof StatefulBegin statefulBegin) {
            statefulBegin.onBegin(geoTiff);
        }

        for (int y = sourceRect.y; y < sourceRect.getMaxY(); y++) {
            for (int x = sourceRect.x; x < sourceRect.getMaxX(); x++) {

                Sample sample = sampler.sample(new RasterPoint(x, y), sourceRaster);

                if (transforms.size() > 0) {
                    for (SampleTransform sampleTransform : transforms) {
                        Sample newSample = sampleTransform.transform(geoTiff, sample);

                        if (newSample == null) {
                            sample = null;
                            break;
                        }
                    }
                }

                Color color = colorTransform.transform(geoTiff, sample);

                if (color == null) {
                    throw new IllegalStateException("ColorTransform '%s' produced null Color for %s".formatted(
                        colorTransform.toString(),
                        sample != null ? sample.toString() : "null"
                    ));
                }

                targetImage.setRGB(x, y, color.getRGB());
            }
        }

        return targetImage;
    }

    private Rectangle getSourceRect(GeoTiff geoTiff, RenderTarget renderTarget) {
        if (renderTarget.getSourceRect() != null) {
            return renderTarget.getSourceRect();
        } else {
            return new Rectangle(
                (int) geoTiff.metadata().getWidth(),
                (int) geoTiff.metadata().getHeight()
            );
        }
    }
}
