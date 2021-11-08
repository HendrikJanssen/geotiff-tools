package com.hendrikjanssen.geotifftools


import com.hendrikjanssen.geotifftools.metadata.geokeys.GeoKeyId
import com.hendrikjanssen.geotifftools.metadata.geokeys.ModelTiepoint
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.AngularUnit
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.LinearUnit
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.ModelType
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.RasterType
import spock.lang.Specification

class SentinelSamplesTest extends Specification {

  def 'should parse sentinel sample "sample" correctly'() {
    given:
      def sentinelSample = this.class.getResourceAsStream("sentinel-samples/sample.tif")

    when:
      def result = new GeoTiff(sentinelSample as InputStream)

    then:
      noExceptionThrown()

    and:
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTModelType, ModelType.Projected.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTRasterType, RasterType.PixelIsArea.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTCitation, "WGS 84 / UTM zone 31N")

      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogCitation, "WGS 84")
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogAngularUnits, AngularUnit.Degree.code())

      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjectedCSType, 32631)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjLinearUnits, LinearUnit.Meter.code())

    and:
      result.metaData.width == 1001
      result.metaData.height == 1001

      result.metaData.modelTiepoints == [new ModelTiepoint(0, 0, 0, 590520.0d, 5790630.0d, 0.0d)]

    and: 'should parse CRS correctly'
      def crsOpt = result.getCoordinateReferenceSystem()

      crsOpt.isPresent()

      crsOpt.get().name == 'WGS 84 / UTM zone 31N'
  }
}
