package com.hendrikjanssen.geotifftools.geokeys;

import java.util.Objects;

public class GeoKeyString extends GeoKey {

    private final String value;

    public GeoKeyString(GeoKeyId id, String value) {
        super(id);

        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GeoKeyString{" +
               "id=" + getId() +
               ",value=" + value +
               '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
