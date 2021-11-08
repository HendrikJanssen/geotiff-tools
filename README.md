# geotiff-tools

![ci](https://github.com/HendrikJanssen/geotiff-tools/actions/workflows/ci.yaml/badge.svg)

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

The library JAR file will be placed under the `build/libs` directory. It can then be included on your classpath for usage.

## Reading metadata

Geotiff metadata are exposed via the `GeoTiffMetadata` class. After reading a GeoTiff via the `new GeoTiff(File | InputStream)` constructor,
you can access the low-level metadata via the `getMetadata()` method.

```java
GeoTiff geoTiff = new GeoTiff(new File("path/to/your/geotiff/file"));

GeoTiffMetadata metdata = geoTiff.getMetadata();
```

### Reading a GeoKey

You can read out a GeoKey by its id. Most common Ids that the standard includes are accessible via the `GeoKeyId` class.
The GeoKeys are returned as an `Optional<GeoKey>`. You can access its value/values via the typed access methods:

- `int getValueAsInt()`
- `int[] getValueAsInts()`
- `double getValueAsDouble()`
- `double[] getValueAsDoubles()`
- `String getValueAsString()`

Example:

```java
Optional<GeoKey> key = metdata.getGeoKey(GeoKeyId.GTModelType);

assert key.isPresent();
assert key.get().getValueAsInt() == ModelType.Projected.getCode();
```

### Reading the ModelTiepoints

Tiepoints are accessible via the `getModelTiepoints()` method. Note that when no tipoints are present, the list ist going to be empty.

```java
List<ModelTiepoint> tipoints = metadata.getModelTiepoints();
```

### Reading the CoordinateReferenceSystem (CRS)

You can access high-level information provided as [Geolatte](https://github.com/GeoLatte/geolatte-geom) types in the `GeoTiff` type.

**Note that currently, only standard EPSG CRS are supported, no user-defined CRS and no CRS from other authorities!**

```java
Optional<? extends CoordinateReferenceSystem<?>> crs = geoTiff.getCoordinateReferenceSystem();

assert crs.isPresent();
assert crs.get().getName().equals("WGS 84 / UTM zone 31N");
```



## License

This library is licensed under the MIT license.
