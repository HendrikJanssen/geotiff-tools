package com.hendrikjanssen.geotifftools

import com.hendrikjanssen.geotifftools.geokeys.GeoKeyId
import com.hendrikjanssen.geotifftools.geokeys.values.AngularUnit
import com.hendrikjanssen.geotifftools.geokeys.values.Ellipsoid
import com.hendrikjanssen.geotifftools.geokeys.values.LinearUnit
import com.hendrikjanssen.geotifftools.geokeys.values.ModelType
import com.hendrikjanssen.geotifftools.geokeys.values.ProjectedCoordinateTransform
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

      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeographicType, 4267)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogCitation, "NAD27")
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogAngularUnits, AngularUnit.Degree.code())

      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjectedCSType, 32767)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.Projection, 32767)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjCoordTrans, ProjectedCoordinateTransform.CylindricalEqualArea.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjLinearUnits, LinearUnit.Meter.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjStdParallel1, 33.75d)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjNatOriginLong, -117.333333333333d)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjFalseEasting, 0d)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjFalseNorthing, 0d)
  }

  def 'should parse osgeo sample "GeogToWGS84GeoKey" correctly'() {
    given:
      def osgeoSample = this.class.getResourceAsStream("osgeo-samples/GeogToWGS84GeoKey/GeogToWGS84GeoKey5.tif")

    when:
      def result = new GeoTiff(osgeoSample as InputStream)

    then:
      noExceptionThrown()

    and:
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTModelType, ModelType.Geographic.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTRasterType, RasterType.PixelIsArea.code())

      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeographicType, 32767)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogGeodeticDatum, 32767)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogAngularUnits, AngularUnit.Degree.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogEllipsoid, Ellipsoid.Bessel_1841.code())
  }
}
