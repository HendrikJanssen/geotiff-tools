package com.hendrikjanssen.geotifftools.metadata.geokeys;

import java.util.Arrays;
import java.util.Objects;

public class GeoKey implements Comparable<GeoKey> {

    private final int id;

    private final Object value;

    public GeoKey(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getValueAsInt() {
        return (int) value;
    }

    public int[] getValueAsInts() {
        return (int[]) value;
    }

    public double getValueAsDouble() {
        return (double) value;
    }

    public double[] getValueAsDoubles() {
        return (double[]) value;
    }

    public String getValueAsString() {
        return (String) value;
    }

    @Override
    public int compareTo(GeoKey o) {
        return this.id - o.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoKey)) return false;
        GeoKey geoKey = (GeoKey) o;
        return id == geoKey.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GeoKey{");

        sb.append("id=");
        sb.append(id);
        sb.append(", value=");

        if (value instanceof String) {
            sb.append('"');
            sb.append(value);
            sb.append('"');
        } else if (value instanceof Integer || value instanceof Double) {
            sb.append(value);
        } else if (value instanceof int[]) {
            sb.append(Arrays.toString((int[]) value));
        } else if (value instanceof double[]) {
            sb.append(Arrays.toString((double[]) value));
        }

        sb.append('}');
        return sb.toString();
    }
}
