package com.hendrikjanssen.geotifftools.metadata.geokeys.values;

import java.util.Arrays;
import java.util.Optional;

public enum ModelType {

    Unknown(0),
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

    public static Optional<ModelType> ofCode(int code) {
        return Arrays.stream(ModelType.values())
            .filter(modelType -> modelType.code == code)
            .findFirst();
    }
}
