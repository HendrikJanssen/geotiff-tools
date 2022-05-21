---
title: Introduction
description: geotiff-tools introduction
layout: ../../layouts/MainLayout.astro
---

geotiff-tools is a pure, minimal-dependencies java library to enable integration with [geolatte-geom](https://github.com/GeoLatte/geolatte-geom) and easy access to geokey information stored in geotiffs.
It also provides a configurable way to visualize raw data stored in geotiffs by rendering them to ARGB `BufferedImage`s.

- Java 11
- 1 dependency: geolatte-geom
- Calculate bounding boxes (`Envelope`)
- Transform raster points to coordinates and vice-versa
- Render to BufferedImage (ARGB): coloring via color ramps and full support for filtering or transforming data beforehand

## Getting Started

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

You can check out the code examples for [common use cases](use-cases) or view the [javadoc](javadoc).

## License

geotiff-tools is licensed under the MIT license and is thus free for personal and commercial use. 
