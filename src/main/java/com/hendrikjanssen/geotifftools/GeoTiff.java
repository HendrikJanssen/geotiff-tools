package com.hendrikjanssen.geotifftools;

import com.hendrikjanssen.geotifftools.geokeys.GeoKey;
import com.hendrikjanssen.geotifftools.geokeys.GeoKeyId;
import com.hendrikjanssen.geotifftools.geokeys.values.ModelType;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.tiff.TIFFDirectory;
import javax.imageio.plugins.tiff.TIFFField;
import javax.imageio.stream.ImageInputStream;

public class GeoTiff implements AutoCloseable {

    private static final int GeoKeyShortLocation = 34735;
    private static final int GeoKeyDoubleLocation = 34736;
    private static final int GeoKeyAsciiLocation = 34737;

    private final GeoKey[] geoKeys;

    private final ImageInputStream imageInputStream;
    private final ImageReader imageReader;

    public GeoTiff(InputStream inputStream) throws IOException {

        this.imageInputStream = ImageIO.createImageInputStream(inputStream);

        this.imageReader = initializeImageReader(imageInputStream);

        TIFFDirectory tiffDirectory = readTiffTags(this.imageReader);

        this.geoKeys = readGeoKeyData(tiffDirectory);
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

    private GeoKey[] readGeoKeyData(TIFFDirectory tiffDirectory) {

        int[] geoKeyDirectoryData = tiffDirectory.getTIFFField(GeoKeyShortLocation).getAsInts();

        double[] geoDoubleParamsData = new double[1];
        TIFFField gGeoKeyDoubleField = tiffDirectory.getTIFFField(GeoKeyDoubleLocation);
        if (gGeoKeyDoubleField != null) {
            geoDoubleParamsData = gGeoKeyDoubleField.getAsDoubles();
        }

        String geoAsciiParamsData = "";
        TIFFField geoAsciiField = tiffDirectory.getTIFFField(GeoKeyAsciiLocation);
        if (geoAsciiField != null) {
            geoAsciiParamsData = geoAsciiField.getAsString(0);
        }

        // header parsing
        int geoKeysNum = geoKeyDirectoryData[3];

        GeoKey[] parsedKeys = new GeoKey[geoKeysNum];

        int i = 4;
        int currentKeyIndex = 0;
        while (i < geoKeyDirectoryData.length) {

            int geoKeyId = geoKeyDirectoryData[i];
            int tiffTagLocation = geoKeyDirectoryData[i + 1];
            int valueCount = geoKeyDirectoryData[i + 2];
            int valueOffset = geoKeyDirectoryData[i + 3];

            switch (tiffTagLocation) {
                case 0:
                    // This implies the value offset is the actual key value
                    parsedKeys[currentKeyIndex] = new GeoKey(geoKeyId, valueOffset);

                    currentKeyIndex++;
                    break;

                case GeoKeyDoubleLocation:
                    double[] values = Arrays.copyOfRange(geoDoubleParamsData, valueOffset, valueOffset + valueCount);
                    parsedKeys[currentKeyIndex] = new GeoKey(geoKeyId, values);

                    currentKeyIndex++;
                    break;

                case GeoKeyAsciiLocation:
                    String value = geoAsciiParamsData.substring(valueOffset, valueOffset + valueCount - 1);
                    parsedKeys[currentKeyIndex] = new GeoKey(geoKeyId, value);

                    currentKeyIndex++;
                    break;
            }

            i += 4;
        }

        // Ensure sorting (even though the standard requires the keys
        // to be sorted) since we rely on that in the code.
        // Sorting an already sorted array should be O(n)
        Arrays.sort(parsedKeys);

        return parsedKeys;
    }

    public List<GeoKey> getGeoKeys() {
        return Arrays.asList(this.geoKeys.clone());
    }

    public Optional<GeoKey> getGeoKey(int geoKeyId) {

        for (GeoKey geoKey : geoKeys) {
            if (geoKey.getId() == geoKeyId) {
                return Optional.of(geoKey);
            }
        }

        return Optional.empty();
    }

    public ModelType getModelType() {
        return this.getGeoKey(GeoKeyId.GTModelType)
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
                return this.getGeoKey(GeoKeyId.ProjectedCSType)
                    .map(GeoKey::getValueAsInt)
                    // These are known EPSG codes, everything above or below are user-defined or obsolete codes
                    .filter(projectedCrsId -> projectedCrsId >= 20000 && projectedCrsId <= 32760)
                    .map(CrsRegistry::getProjectedCoordinateReferenceSystemForEPSG)
                    .or(() -> this.getGeoKey(GeoKeyId.Projection)
                        .map(GeoKey::getValueAsInt)
                        .filter(projectedCrsId -> projectedCrsId >= 10000 && projectedCrsId <= 19999)
                        .map(CrsRegistry::getProjectedCoordinateReferenceSystemForEPSG));
            case Geographic:
                this.getGeoKey(GeoKeyId.GeographicType)
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
