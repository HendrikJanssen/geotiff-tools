package de.hendrikjanssen.geotifftools;

public class MalformedGeoTiffException extends RuntimeException {

    public MalformedGeoTiffException(String message) {
        super(message);
    }

    public MalformedGeoTiffException(String message, Throwable cause) {
        super(message, cause);
    }
}
