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

public class GeoTiff {

    private static final int GeoKeyShortLocation = 0;
    private static final int GeoKeyDoubleLocation = 34736;
    private static final int GeoKeyAsciiLocation = 34737;

    private final GeoKey[] geoKeys;

    public GeoTiff(InputStream inputStream) throws IOException {
        ImageReader imageReader = ImageIO.getImageReadersByFormatName("tiff").next();

        imageReader.setInput(ImageIO.createImageInputStream(inputStream), true);
        IIOMetadata metadata = imageReader.getImageMetadata(0);

        TIFFDirectory tiffDirectory = TIFFDirectory.createFromMetadata(metadata);

        var geoKeyDirectoryField = tiffDirectory.getTIFFField(34735);
        var geoDoubleParamsField = tiffDirectory.getTIFFField(34736);
        var geoAsciiParamsField = tiffDirectory.getTIFFField(34737);

        this.geoKeys = readGeoKeyData(
            geoKeyDirectoryField.getAsInts(),
            geoDoubleParamsField.getAsDoubles(),
            geoAsciiParamsField.getAsString(0)
        );
    }

    private GeoKey[] readGeoKeyData(int[] geoKeyDirectory, double[] geoDoubleParams, String geoAsciiParams) {

        // header parsing
        int geoKeysNum = geoKeyDirectory[3];

        GeoKey[] parsedKeys = new GeoKey[geoKeysNum];

        int i = 4;
        int currentKeyIndex = 0;
        while (i < geoKeyDirectory.length) {

            int geoKeyId = geoKeyDirectory[i];
            int tiffTagLocation = geoKeyDirectory[i + 1];
            int valueCount = geoKeyDirectory[i + 2];
            int valueOffset = geoKeyDirectory[i + 3];

            switch (tiffTagLocation) {
                case GeoKeyShortLocation:
                    // This implies the value offset is the actual key value
                    parsedKeys[currentKeyIndex] = new GeoKey(geoKeyId, valueOffset);
                    currentKeyIndex++;
                    break;
                case GeoKeyDoubleLocation:
                    double[] values = Arrays.copyOfRange(geoDoubleParams, valueOffset, valueOffset + valueCount);
                    parsedKeys[currentKeyIndex] = new GeoKey(geoKeyId, values);
                    currentKeyIndex++;
                    break;
                case GeoKeyAsciiLocation:
                    String data = geoAsciiParams.substring(valueOffset, valueOffset + valueCount - 1);
                    parsedKeys[currentKeyIndex] = new GeoKey(geoKeyId, data);
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

    public Optional<CoordinateReferenceSystem<?>> getCoordinateReferenceSystem() {

        ModelType modelType = this.getModelType();

        switch (modelType) {
            case Geocentric:
                throw new RuntimeException("Geocentric ModelTypes are not supported");
            case Projected:
                return this.getGeoKey(GeoKeyId.ProjectedCSType)
                    .map(GeoKey::getValueAsInt)
                    // These are known EPSG codes, everything above or below are user-defined or obsolete codes
                    .filter(projectedCrsId -> projectedCrsId >= 20000 && projectedCrsId <= 32760)
                    .map(CrsRegistry::getProjectedCoordinateReferenceSystemForEPSG);
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
}
