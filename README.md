# geotiff-tools

This is a java library in construction to enable basic integration with geolatte and easy access to geokey information stored in geotiffs.
In the future, it will also enable basic rendering functionality for converting geotiffs into plain .png or .jpg images using a sample transformation.

## State of development

This library is currently under construction and is not recommended for production use. It is highly unstable and will introduce breaking changes for minor- or patch-version releases.

## Usage

geotiff-tools is currently not available via maven (but that is planned), so you have to build the library yourself for now. You can do that by cloning the repo
and executing the gradle build process via the respective shell scripts in the project directory root.

**Windows:**
```shell
$: ./gradlew.bat build
```

**Linux/MacOs:**
```shell
$: ./gradlew build
```

The library JAR file will be placed under the `build/libs` directory. It can then be included on your classpath fo usage.

### Reading a GeoKey

```java
GeoTiff geoTiff = new GeoTiff(new File("path/to/your/geotiff/file"))

GeoKeyShort keyData = geoTiff.getGeoKey<GeoKeyShort>(GeoKeyId.GTModelType);

assert keyData.isPresent()
assert keyData.get().getValue() == ModelType.Projected.getCode();
```

## License

This library is licensed under the MIT license.
