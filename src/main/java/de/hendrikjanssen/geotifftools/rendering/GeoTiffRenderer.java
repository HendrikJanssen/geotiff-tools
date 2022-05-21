package de.hendrikjanssen.geotifftools.rendering;

import de.hendrikjanssen.geotifftools.GeoTiff;

import java.awt.Color;
import java.awt.Point;
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

        for (int y = sourceRect.y; y < sourceRect.getMaxY(); y++) {
            for (int x = sourceRect.x; x < sourceRect.getMaxX(); x++) {

                Sample sample = sampler.sample(new Point(x, y), sourceRaster);

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
                geoTiff.getMetadata().getWidth(),
                geoTiff.getMetadata().getHeight()
            );
        }
    }
}
