package com.hendrikjanssen.geotifftools.geokeys;

import java.util.Arrays;

public class GeoKeyDoubleArray extends GeoKey {

    private final double[] values;

    public GeoKeyDoubleArray(GeoKeyId id, double[] values) {
        super(id);

        this.values = values;
    }

    public double[] getValues() {
        return values.clone();
    }

    public double getValue(int index) {
        return values[index];
    }

    @Override
    public String toString() {
        return "GeoKeyDoubleArray{" +
               "id=" + getId() +
               ",values=" + Arrays.toString(values) +
               '}';
    }
}
