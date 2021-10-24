package com.hendrikjanssen.geotifftools.geokeys;

import java.util.Arrays;

public class GeoKeyShortArray extends GeoKey {

    private final int[] values;

    public GeoKeyShortArray(GeoKeyId id, int[] values) {
        super(id);

        this.values = values;
    }

    public int[] getValues() {
        return values.clone();
    }

    public int getValue(int index) {
        return values[index];
    }

    @Override
    public String toString() {
        return "GeoKeyShortArray{" +
               "id=" + getId() +
               ",values=" + Arrays.toString(values) +
               '}';
    }
}
