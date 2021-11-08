package com.hendrikjanssen.geotifftools

import com.hendrikjanssen.geotifftools.metadata.GeoTiffMetadata

class TestUtils {

  static boolean geoKeyHasProperty(GeoTiffMetadata metadata, int id, int property) {
    return metadata.getGeoKey(id).get().getValueAsInt() == property
  }

  static boolean geoKeyHasProperty(GeoTiffMetadata metadata, int id, double ... properties) {
    return metadata.getGeoKey(id).get().getValueAsDoubles() == properties
  }

  static boolean geoKeyHasProperty(GeoTiffMetadata metadata, int id, String property) {
    return metadata.getGeoKey(id).get().getValueAsString() == property
  }

  static void printKeys(GeoTiffMetadata metadata) {
    println(metadata.geoKeys.collect { "$it" }.join("\n"))
  }
}
