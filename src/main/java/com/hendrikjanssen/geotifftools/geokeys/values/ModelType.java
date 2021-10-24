package com.hendrikjanssen.geotifftools.geokeys.values;

public enum ModelType {

    Projected(1),
    Geographic(2),
    Geocentric(3);

    private final int code;

    ModelType(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
