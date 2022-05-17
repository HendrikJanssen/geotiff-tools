![ci](https://github.com/HendrikJanssen/geotiff-tools/actions/workflows/ci.yaml/badge.svg)
# geotiff-tools

This is a pure, minimal-dependencies java library to enable integration with [Geolatte](https://github.com/GeoLatte/geolatte-geom) and easy access to geokey information stored in geotiffs.
In the future, it will also enable basic rendering functionality for converting geotiffs into plain .png or .jpg images.

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

```java
Optional<GeoKey> key = metdata.getGeoKey(GeoKeyId.GTModelType);

assert key.isPresent();
assert key.get().getValueAsInt() == ModelType.Projected.getCode();
```

### Reading the ModelTiepoints

Tiepoints are accessible via the `getModelTiepoints()` method.

```java
Optional<List<ModelTiepoint>>tiepoints = metadata.getModelTiepoints();
```

### Reading the CoordinateReferenceSystem (CRS)

You can access high-level information provided as [Geolatte](https://github.com/GeoLatte/geolatte-geom) types in the `GeoTiff` type.

**Note that currently, only standard EPSG CRS are supported, no user-defined CRS and no CRS from other authorities!**

```java
Optional<? extends CoordinateReferenceSystem<? extends Position>> crs = geoTiff.getCoordinateReferenceSystem();

assert crs.isPresent();
assert crs.get().getName().equals("WGS 84 / UTM zone 31N");
```

### Getting the coordinates of a raster point

The library lets you get the real-world coordinates of a raster point. Use
the `Optional<P> transformRasterPointToModelPoint(int rasterX, int rasterY)`
method on the `GeoTiff` class for that.

**Note that currently, only georeferencing via ModelTiepoints and ModelPixelScale is supported!**

```java
Optional<G2D> point = geoTiff.transformRasterPointToModelPoint(50,50);

assert point.isPresent();
assert point.get().equals(new G2D(6.783922,51.232683));
```

### Getting the BoundingBox (Envelope)

You can get the `Envelope<P>` by using the `Optional<Envelope<P>> getEnvelope()` method on the `GeoTiff` class. The method is generic, so
you need to know the projection beforehand. For finding the projection type, use the `getModelType()` method on the `GeoTiff` class.

**Note that currently, only georeferencing via ModelTiepoints and ModelPixelScale is supported!**

```java
ModelType modelType=geoTiff.getModelType();

Optional<Envelope<? extends Position>> envelope;

switch(modelType) {
    case ModelType.Projected:
        envelope = geoTiff.<C2D>getEnvelope();
        break;
        
    case ModeType.Geographic:
        envelope = geoTiff.<G2D>getEnvelope();
        break;

    ...
}

assert envelope.isPresent();
```

## License

This library is licensed under the MIT license.
