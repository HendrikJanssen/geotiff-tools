package com.hendrikjanssen.geotifftools

class TestUtils {

  static boolean geoKeyHasProperty(GeoTiff geoTiff, int id, int property) {
    return geoTiff.metadata.getGeoKey(id).get().getValueAsInt() == property
  }

  static boolean geoKeyHasProperty(GeoTiff geoTiff, int id, double ... properties) {
    return geoTiff.metadata.getGeoKey(id).get().getValueAsDoubles() == properties
  }

  static boolean geoKeyHasProperty(GeoTiff geoTiff, int id, String property) {
    return geoTiff.metadata.getGeoKey(id).get().getValueAsString() == property
  }

  static void printKeys(GeoTiff tiff) {
    println(tiff.metadata.geoKeys.collect { "$it" }.join("\n"))
  }
}
