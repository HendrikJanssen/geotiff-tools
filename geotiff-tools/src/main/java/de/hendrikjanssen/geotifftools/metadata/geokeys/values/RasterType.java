package de.hendrikjanssen.geotifftools.metadata.geokeys.values;

public enum RasterType {
    PixelIsArea(1),
    PixelIsPoint(2);

    private final int code;

    RasterType(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
