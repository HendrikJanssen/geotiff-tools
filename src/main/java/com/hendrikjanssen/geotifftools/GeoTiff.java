package com.hendrikjanssen.geotifftools;

import com.hendrikjanssen.geotifftools.metadata.GeoTiffMetadata;
import com.hendrikjanssen.geotifftools.metadata.geokeys.GeoKey;
import com.hendrikjanssen.geotifftools.metadata.geokeys.GeoKeyId;
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.ModelType;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.tiff.TIFFDirectory;
import javax.imageio.stream.ImageInputStream;

public class GeoTiff implements AutoCloseable {

    private final GeoTiffMetadata metaData;

    private final ImageInputStream imageInputStream;
    private final ImageReader imageReader;

    public GeoTiff(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public GeoTiff(InputStream inputStream) throws IOException {

        this.imageInputStream = ImageIO.createImageInputStream(inputStream);

        this.imageReader = initializeImageReader(imageInputStream);

        TIFFDirectory tiffDirectory = readTiffTags(this.imageReader);

        this.metaData = new GeoTiffMetadata(tiffDirectory);
    }

    private ImageReader initializeImageReader(ImageInputStream imageInputStream) {

        ImageReader imageReader = ImageIO.getImageReadersByFormatName("tiff").next();
        imageReader.setInput(imageInputStream, false, false);
        return imageReader;
    }

    private TIFFDirectory readTiffTags(ImageReader imageReader) throws IOException {

        IIOMetadata metadata = imageReader.getImageMetadata(0);
        return TIFFDirectory.createFromMetadata(metadata);
    }

    public GeoTiffMetadata getMetadata() {
        return this.metaData;
    }

    public ModelType getModelType() {
        return this.metaData.getGeoKey(GeoKeyId.GTModelType)
            .map(GeoKey::getValueAsInt)
            .flatMap(ModelType::ofCode)
            .orElse(ModelType.Unknown);
    }

    public Optional<? extends CoordinateReferenceSystem<?>> getCoordinateReferenceSystem() {

        ModelType modelType = this.getModelType();

        switch (modelType) {
            case Geocentric:
                throw new RuntimeException("Geocentric ModelTypes are not supported");
            case Projected:
                return this.metaData.getGeoKey(GeoKeyId.ProjectedCSType)
                    .map(GeoKey::getValueAsInt)
                    // These are known EPSG codes, everything above or below are user-defined or obsolete codes
                    .filter(projectedCrsId -> projectedCrsId >= 20000 && projectedCrsId <= 32760)
                    .map(CrsRegistry::getProjectedCoordinateReferenceSystemForEPSG)
                    .or(() -> this.metaData.getGeoKey(GeoKeyId.Projection)
                        .map(GeoKey::getValueAsInt)
                        .filter(projectedCrsId -> projectedCrsId >= 10000 && projectedCrsId <= 19999)
                        .map(CrsRegistry::getProjectedCoordinateReferenceSystemForEPSG));
            case Geographic:
                this.metaData.getGeoKey(GeoKeyId.GeographicType)
                    .map(GeoKey::getValueAsInt)
                    // These are known EPSG codes, everything above or below are user-defined or obsolete codes
                    .filter(geographicCrsId -> geographicCrsId >= 4000 && geographicCrsId <= 4999)
                    .map(CrsRegistry::getGeographicCoordinateReferenceSystemForEPSG);
            default:
                return Optional.empty();
        }
    }

    @Override
    public void close() throws Exception {
        this.imageInputStream.flush();
        this.imageInputStream.close();

        this.imageReader.setInput(null);
        this.imageReader.dispose();
    }
}
