package com.hendrikjanssen.geotifftools


import com.hendrikjanssen.geotifftools.geokeys.GeoKeyDoubleArray
import com.hendrikjanssen.geotifftools.geokeys.GeoKeyId
import com.hendrikjanssen.geotifftools.geokeys.GeoKeyShort
import com.hendrikjanssen.geotifftools.geokeys.GeoKeyString

class TestUtils {

  static boolean geoKeyHasProperty(GeoTiff geoTiff, GeoKeyId id, int property) {
    return (geoTiff.getGeoKey(id).get() as GeoKeyShort).value == property
  }

  static boolean geoKeyHasProperty(GeoTiff geoTiff, GeoKeyId id, double ... properties) {
    return (geoTiff.getGeoKey(id).get() as GeoKeyDoubleArray).values == properties
  }

  static boolean geoKeyHasProperty(GeoTiff geoTiff, GeoKeyId id, String property) {
    return (geoTiff.getGeoKey(id).get() as GeoKeyString).value == property
  }

  static void printKeys(GeoTiff tiff) {
    println(tiff.geoKeys.collect { "$it" }.join("\n"))
  }
}
