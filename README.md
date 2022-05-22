![ci](https://github.com/HendrikJanssen/geotiff-tools/actions/workflows/ci.yaml/badge.svg)
![maven-central](https://img.shields.io/maven-central/v/de.hendrik-janssen/geotiff-tools)
# geotiff-tools

geotiff-tools is a pure, minimal-dependencies java library to enable integration with [geolatte-geom](https://github.com/GeoLatte/geolatte-geom) and easy access to geokey information stored in geotiffs. It also provides a configurable way to visualize raw data stored in geotiffs by rendering them to ARGB `BufferedImage`s.

## State of development

This library is currently under construction and is not recommended for production use. It is highly unstable and will introduce breaking changes for minor- or patch-version releases.

## Quickstart

**Gradle:**

```groovy
implementation("de.hendrik-janssen:geotiff-tools:0.1.0")
```

**Maven:**

```xml
<dependency>
  <groupId>de.hendrik-janssen</groupId>
  <artifactId>geotiff-tools</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Documentation

Full documentation with code examples is available on [geotiff-tools.hendrik-janssen.de](https://geotiff-tools.hendrik-janssen.de)

You can also find the [javadoc](https://geotiff-tools.hendrik-janssen.de/javadoc/index.html) there.

## License

This library is licensed under the MIT license.
