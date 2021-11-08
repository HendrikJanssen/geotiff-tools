package com.hendrikjanssen.geotifftools

import com.hendrikjanssen.geotifftools.metadata.geokeys.GeoKeyId
import com.hendrikjanssen.geotifftools.metadata.geokeys.ModelTiepoint
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.AngularUnit
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.Ellipsoid
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.LinearUnit
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.ModelType
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.ProjectedCoordinateTransform
import com.hendrikjanssen.geotifftools.metadata.geokeys.values.RasterType
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

    and:
      result.metaData.width == 514
      result.metaData.height == 515

      result.metaData.modelTiepoints == [new ModelTiepoint(0, 0, 0, -28493.166784412522d, 4255884.5438021915d, 0.0d)]
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

    and:
      result.metaData.width == 101
      result.metaData.height == 101

      result.metaData.modelTiepoints == [new ModelTiepoint(50, 50, 0, 9.0010573796d, 52.0013760079d, 0.0d)]
  }

  def 'should parse osgeo sample "intergraph - utm" correctly'() {
    given:
      def osgeoSample = this.class.getResourceAsStream("osgeo-samples/intergraph/utm.tif")

    when:
      def result = new GeoTiff(osgeoSample as InputStream)

    then:
      noExceptionThrown()

    and:
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTModelType, ModelType.Projected.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GTRasterType, RasterType.PixelIsArea.code())

      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjCoordTrans, 32767)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.Projection, 16016)

      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogGeodeticDatum, 6267)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.GeogEllipsoid, Ellipsoid.Clarke_1866.code())
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjNatOriginLong, -87d)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjNatOriginLat, 0d)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjFalseEasting, 500000d)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjFalseNorthing, 0d)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjScaleAtNatOrigin, 0.9996d)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.PCSCitation, "Universal Transverse Mercator North American 1927 Zone Number 16N")
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjectedCSType, 32767)
      TestUtils.geoKeyHasProperty(result, GeoKeyId.ProjLinearUnits, LinearUnit.Meter.code())

    and:
      result.metaData.width == 200
      result.metaData.height == 200

      result.metaData.modelTiepoints == [new ModelTiepoint(0, 0, 0, 1871032.953888d, 693358.668144d, 0.0d)]
  }
}
