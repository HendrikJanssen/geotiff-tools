package de.hendrikjanssen.geotifftools.metadata;

import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageMetadata;
import de.hendrikjanssen.geotifftools.MalformedGeoTiffException;
import de.hendrikjanssen.geotifftools.metadata.geokeys.GeoKey;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GeoTiffMetadata {

    private static final int ImageWidthTagId = 256;
    private static final int ImageLengthTagId = 257;

    private static final int GeoKeyShortTagId = 34735;
    private static final int GeoKeyDoubleTagId = 34736;
    private static final int GeoKeyAsciiTagId = 34737;

    private static final int ModelTiepointsTagId = 33922;
    private static final int ModelPixelScaleTagId = 33550;

    private static final int GdalNoDataTagId = 42113;

    private final GeoKey[] geoKeys;

    private final ModelTiepoint[] tiepoints;
    private final ModelPixelScale pixelScale;

    private final Double noDataValue;

    private final long width;
    private final long height;

    public GeoTiffMetadata(TIFFImageMetadata metadata) {
        this.geoKeys = readGeoKeyData(metadata);

        this.width = readDimensionParam(metadata.getTIFFField(ImageWidthTagId));
        this.height = readDimensionParam(metadata.getTIFFField(ImageLengthTagId));

        this.tiepoints = this.readTiepoints(metadata.getTIFFField(ModelTiepointsTagId));
        this.pixelScale = this.readPixelScale(metadata.getTIFFField(ModelPixelScaleTagId));
        this.noDataValue = this.readNoDataValue(metadata.getTIFFField(GdalNoDataTagId));
    }

    private long readDimensionParam(Entry widthField) {
        if (widthField.getValue() instanceof Long) {
            return (long) widthField.getValue();
        } else {
            return (int) widthField.getValue();
        }
    }

    public long getWidth() {
        return this.width;
    }

    public long getHeight() {
        return this.height;
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

    public Optional<List<ModelTiepoint>> getModelTiepoints() {
        return Optional
            .ofNullable(this.tiepoints)
            .map(ModelTiepoint[]::clone)
            .map(Arrays::asList);
    }

    public Optional<ModelPixelScale> getModelPixelScale() {
        return Optional.ofNullable(this.pixelScale);
    }

    private GeoKey[] readGeoKeyData(TIFFImageMetadata metadata) {

        int[] geoKeyDirectoryData = (int[]) metadata.getTIFFField(GeoKeyShortTagId).getValue();

        double[] geoDoubleParamsData = new double[1];
        Entry geoKeyDoubleField = metadata.getTIFFField(GeoKeyDoubleTagId);
        if (geoKeyDoubleField != null) {
            geoDoubleParamsData = (double[]) geoKeyDoubleField.getValue();
        }

        String geoAsciiParamsData = "";
        Entry geoAsciiField = metadata.getTIFFField(GeoKeyAsciiTagId);
        if (geoAsciiField != null) {
            geoAsciiParamsData = (String) geoAsciiField.getValue();
        }

        // Header (4 integers)
        int geoKeysNum = geoKeyDirectoryData[3];

        GeoKey[] parsedKeys = new GeoKey[geoKeysNum];

        int i = 4; // Skip header
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

                case GeoKeyDoubleTagId:
                    double[] values = Arrays.copyOfRange(geoDoubleParamsData, valueOffset, valueOffset + valueCount);
                    parsedKeys[currentKeyIndex] = new GeoKey(geoKeyId, values);

                    currentKeyIndex++;
                    break;

                case GeoKeyAsciiTagId:
                    String value = geoAsciiParamsData.substring(valueOffset, valueOffset + valueCount - 1);
                    parsedKeys[currentKeyIndex] = new GeoKey(geoKeyId, value);

                    currentKeyIndex++;
                    break;
                default:
                    // Broken GeoKey?
                    i += 4;
                    continue;
            }

            i += 4;
        }

        // Ensure sorting (even though the standard requires the keys
        // to be sorted) since we rely on that in the code
        Arrays.sort(parsedKeys);

        return parsedKeys;
    }

    private ModelTiepoint[] readTiepoints(Entry tiepointField) {

        if (tiepointField == null) {
            return null;
        }

        double[] values = (double[]) tiepointField.getValue();

        if (values.length % 6 != 0) {
            throw new MalformedGeoTiffException(String.format(
                "Invalid number of ModelTiepoints values. Expected multiple of 6, got %d", values.length
            ));
        }

        int numberOfTiepoints = values.length / 6;

        ModelTiepoint[] tiepoints = new ModelTiepoint[numberOfTiepoints];

        for (int i = 0; i < numberOfTiepoints; i++) {
            tiepoints[i] = new ModelTiepoint(
                (int) values[i],
                (int) values[i + 1],
                (int) values[i + 2],
                values[i + 3],
                values[i + 4],
                values[i + 5]
            );
        }

        return tiepoints;
    }

    @Nullable
    private ModelPixelScale readPixelScale(@Nullable Entry pixelScaleField) {

        if (pixelScaleField == null) {
            return null;
        }

        double[] values = (double[]) pixelScaleField.getValue();

        if (values.length != 3) {
            throw new MalformedGeoTiffException(String.format(
                "Invalid number of ModelPixelScale values. Expected 3, got %d", values.length
            ));
        }

        return new ModelPixelScale(values[0], values[1], values[2]);
    }

    @Nullable
    private Double readNoDataValue(@Nullable Entry noDataTag) {
        if (noDataTag == null) {
            return null;
        }

        String val = noDataTag.getValueAsString();

        if (val == null) {
            return null;
        }

        val = val.strip();

        if (val.length() >= 1) {
            try {
                return Double.parseDouble(val);
            } catch (NumberFormatException exception) {
                return null;
            }

        }

        return null;
    }

    public Optional<Double> getNoDataValue() {
        return Optional.ofNullable(noDataValue);
    }
}
