package com.hendrikjanssen.geotifftools.geokeys;

import java.util.Comparator;
import java.util.Objects;

public abstract class GeoKey implements Comparable<GeoKey> {

    public static final Comparator<GeoKey> COMPARATOR = Comparator.nullsLast(GeoKey::compareTo);

    private final GeoKeyId id;

    public GeoKey(GeoKeyId id) {
        this.id = id;
    }

    public GeoKeyId getId() {
        return id;
    }

    @Override
    public int compareTo(GeoKey o) {
        return this.id.getId() - o.getId().getId();
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
        return "GeoKey{" +
               "id=" + id +
               '}';
    }
}
