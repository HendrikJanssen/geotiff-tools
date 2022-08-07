package de.hendrikjanssen.geotifftools

import de.hendrikjanssen.geotifftools.metadata.GeoTiffMetadata

class TestUtils {

  static boolean geoKeyHasProperty(GeoTiffMetadata metadata, int id, int property) {
    return metadata.getGeoKey(id).getValueAsInt() == property
  }

  static boolean geoKeyHasProperty(GeoTiffMetadata metadata, int id, double ... properties) {
    return metadata.getGeoKey(id).getValueAsDoubles() == properties
  }

  static boolean geoKeyHasProperty(GeoTiffMetadata metadata, int id, String property) {
    return metadata.getGeoKey(id).getValueAsString() == property
  }

  static void printKeys(GeoTiffMetadata metadata) {
    println(metadata.geoKeys.collect { "$it" }.join("\n"))
  }
}
