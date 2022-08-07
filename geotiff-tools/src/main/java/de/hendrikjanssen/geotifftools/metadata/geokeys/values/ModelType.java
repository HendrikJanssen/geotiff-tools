package de.hendrikjanssen.geotifftools.metadata.geokeys.values;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;

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

    @Nullable
    public static ModelType ofCode(int code) {

        return Arrays.stream(ModelType.values())
            .filter(modelType -> modelType.code == code)
            .findFirst()
            .orElse(null);
    }
}
