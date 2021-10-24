package com.hendrikjanssen.geotifftools


import com.hendrikjanssen.geotifftools.geokeys.GeoKeyId
import com.hendrikjanssen.geotifftools.geokeys.values.AngularUnit
import com.hendrikjanssen.geotifftools.geokeys.values.GeographicCs
import com.hendrikjanssen.geotifftools.geokeys.values.LinearUnit
import com.hendrikjanssen.geotifftools.geokeys.values.ModelType
import com.hendrikjanssen.geotifftools.geokeys.values.RasterType
import spock.lang.Specification

class OsgeoSamplesTest extends Specification {

  def 'should parse osgeo sample "gdal_eg" correctly'() {
    given:
      def osgeoSample = this.class.getResourceAsStream("osgeo-samples/gdal_eg/cea.tif")

    when:
      def result = new GeoTiff(osgeoSample as InputStream)

    then:
      noExceptionThrown()

    and:
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTModelType, ModelType.Projected.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTRasterType, RasterType.PixelIsArea.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTCitation, "unnamed")
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeographicType, GeographicCs.GCS_NAD27.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogCitation, "NAD27")
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogAngularUnits, AngularUnit.Degree.code())

      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjLinearUnits, LinearUnit.Meter.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjStdParallel1, 33.75d)
  }
}
