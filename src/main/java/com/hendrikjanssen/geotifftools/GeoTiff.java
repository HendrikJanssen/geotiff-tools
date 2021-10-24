package com.hendrikjanssen.geotifftools;

import static mil.nga.tiff.FieldTagType.GeoKeyDirectory;

import com.hendrikjanssen.geotifftools.geokeys.GeoKey;
import com.hendrikjanssen.geotifftools.geokeys.GeoKeyDoubleArray;
import com.hendrikjanssen.geotifftools.geokeys.GeoKeyId;
import com.hendrikjanssen.geotifftools.geokeys.GeoKeyShort;
import com.hendrikjanssen.geotifftools.geokeys.GeoKeyString;
import mil.nga.tiff.FieldTagType;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.TiffReader;
import mil.nga.tiff.util.TiffException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GeoTiff {

    private static final int GeoKeyShortLocation = 0;
    private static final int GeoKeyDoubleLocation = 34736;
    private static final int GeoKeyAsciiLocation = 34737;

    private final FileDirectory fileDirectory;

    private final List<GeoKey> geoKeys;

    public GeoTiff(InputStream inputStream) throws IOException {

        try {
            this.fileDirectory = TiffReader.readTiff(inputStream).getFileDirectory();
        } catch (TiffException tiffException) {
            throw new MalformedGeoTiffException("Unable to read geotiff", tiffException);
        }

        this.geoKeys = readGeoKeyData(this.fileDirectory);
    }

    public GeoTiff(File file) throws IOException {

        try {
            this.fileDirectory = TiffReader.readTiff(file).getFileDirectory();
        } catch (TiffException tiffException) {
            throw new MalformedGeoTiffException("Unable to read geotiff", tiffException);
        }

        this.geoKeys = readGeoKeyData(this.fileDirectory);
    }

    private List<GeoKey> readGeoKeyData(FileDirectory fileDirectory) {
        List<Integer> rawData = fileDirectory.getIntegerListEntryValue(GeoKeyDirectory);
        List<Double> doubleData = fileDirectory.getDoubleListEntryValue(FieldTagType.GeoDoubleParams);
        String asciiData = fileDirectory.getStringEntryValue(FieldTagType.GeoAsciiParams);

        // header parsing
        int geoKeysNum = rawData.get(3);

        List<GeoKey> parsedKeys = new ArrayList<>(geoKeysNum);

        int i = 4;
        while (i < rawData.size()) {
            GeoKeyId geoKeyId = GeoKeyId.getById(rawData.get(i));
            int tiffTagLocation = rawData.get(i + 1);
            int valueCount = rawData.get(i + 2);
            int valueOffset = rawData.get(i + 3);

            switch (tiffTagLocation) {
                case GeoKeyShortLocation:
                    // This implies the value offset is the actual key value
                    parsedKeys.add(new GeoKeyShort(geoKeyId, valueOffset));
                    break;
                case GeoKeyDoubleLocation:
                    double[] values = new double[valueCount];
                    for (int v = 0; v + valueOffset < valueOffset + valueCount; v++) {
                        values[v] = doubleData.get(valueOffset + v);
                    }

                    parsedKeys.add(new GeoKeyDoubleArray(geoKeyId, values));
                    break;
                case GeoKeyAsciiLocation:
                    String data = asciiData.substring(valueOffset, valueOffset + valueCount - 1);
                    parsedKeys.add(new GeoKeyString(geoKeyId, data));
                    break;
            }

            i += 4;
        }

        // Ensure sorting even though the standard requires the keys
        // to be sorted since we rely on that in the code
        // Sorting an already sorted List should be O(n)
        Collections.sort(parsedKeys);

        return parsedKeys;
    }

    public List<GeoKey> getGeoKeys() {
        return Collections.unmodifiableList(this.geoKeys);
    }

    public <T extends GeoKey> Optional<T> getGeoKey(GeoKeyId geoKeyId) {

        for (GeoKey geoKey : geoKeys) {
            if (geoKey.getId() == geoKeyId) {
                return Optional.of((T) geoKey);
            }
        }

        return Optional.empty();
    }
}
