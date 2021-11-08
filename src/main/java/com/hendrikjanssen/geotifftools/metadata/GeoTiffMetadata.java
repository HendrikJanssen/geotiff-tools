package com.hendrikjanssen.geotifftools.metadata;

import com.hendrikjanssen.geotifftools.MalformedGeoTiffException;
import com.hendrikjanssen.geotifftools.metadata.geokeys.GeoKey;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.imageio.plugins.tiff.TIFFDirectory;
import javax.imageio.plugins.tiff.TIFFField;

public class GeoTiffMetadata {

    private static final int ImageWidthTagLocation = 256;
    private static final int ImageLengthTagLocation = 257;

    private static final int GeoKeyShortTagId = 34735;
    private static final int GeoKeyDoubleTagId = 34736;
    private static final int GeoKeyAsciiTagId = 34737;

    private static final int ModelTiepointsTagId = 33922;
    private static final int ModelPixelScaleTagId = 33550;

    private final GeoKey[] geoKeys;

    private final ModelTiepoint[] tiepoints;
    private final ModelPixelScale pixelScale;

    private final int width;
    private final int height;

    public GeoTiffMetadata(TIFFDirectory tiffDirectory) {

        this.width = tiffDirectory.getTIFFField(ImageWidthTagLocation).getAsInt(0);
        this.height = tiffDirectory.getTIFFField(ImageLengthTagLocation).getAsInt(0);

        this.geoKeys = readGeoKeyData(tiffDirectory);

        this.tiepoints = this.readTiepoints(tiffDirectory.getTIFFField(ModelTiepointsTagId));
        this.pixelScale = this.readPixelScale(tiffDirectory.getTIFFField(ModelPixelScaleTagId));
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
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
        return Optional.ofNullable(this.tiepoints)
            .map(ModelTiepoint[]::clone)
            .map(Arrays::asList);
    }

    public Optional<ModelPixelScale> getModelPixelScale() {
        return Optional.ofNullable(this.pixelScale);
    }

    private GeoKey[] readGeoKeyData(TIFFDirectory tiffDirectory) {

        int[] geoKeyDirectoryData = tiffDirectory.getTIFFField(GeoKeyShortTagId).getAsInts();

        double[] geoDoubleParamsData = new double[1];
        TIFFField gGeoKeyDoubleField = tiffDirectory.getTIFFField(GeoKeyDoubleTagId);
        if (gGeoKeyDoubleField != null) {
            geoDoubleParamsData = gGeoKeyDoubleField.getAsDoubles();
        }

        String geoAsciiParamsData = "";
        TIFFField geoAsciiField = tiffDirectory.getTIFFField(GeoKeyAsciiTagId);
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
            }

            i += 4;
        }

        // Ensure sorting (even though the standard requires the keys
        // to be sorted) since we rely on that in the code.
        // Sorting an already sorted array should be O(n)
        Arrays.sort(parsedKeys);

        return parsedKeys;
    }

    private ModelTiepoint[] readTiepoints(TIFFField tiepointField) {

        if (tiepointField == null) {
            return null;
        }

        double[] values = tiepointField.getAsDoubles();

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

    private ModelPixelScale readPixelScale(TIFFField pixelScaleField) {

        if (pixelScaleField == null) {
            return null;
        }

        double[] values = pixelScaleField.getAsDoubles();

        if (values.length != 3) {
            throw new MalformedGeoTiffException(String.format(
                "Invalid number of ModelPixelScale values. Expected 3, got %d", values.length
            ));
        }

        return new ModelPixelScale(values[0], values[1], values[2]);
    }
}
