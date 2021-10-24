package com.hendrikjanssen.geotifftools.geokeys;

import java.util.Objects;

public class GeoKeyShort extends GeoKey {

    private final int value;

    public GeoKeyShort(GeoKeyId id, int value) {
        super(id);

        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GeoKeyShort{" +
               "id=" + getId() +
               ",value=" + value +
               '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
