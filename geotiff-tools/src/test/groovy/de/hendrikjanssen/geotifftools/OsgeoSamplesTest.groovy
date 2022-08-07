package de.hendrikjanssen.geotifftools

import de.hendrikjanssen.geotifftools.metadata.GeoTiffMetadata
import de.hendrikjanssen.geotifftools.metadata.ModelPixelScale
import de.hendrikjanssen.geotifftools.metadata.ModelTiepoint
import de.hendrikjanssen.geotifftools.metadata.geokeys.GeoKeyId
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.AngularUnit
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.Ellipsoid
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.LinearUnit
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.ModelType
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.ProjectedCoordinateTransform
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.RasterType
import spock.lang.Specification

class OsgeoSamplesTest extends Specification {

  def 'should parse osgeo sample "gdal_eg" correctly'() {
    given:
      def osgeoSample = this.class.getResourceAsStream("osgeo-samples/gdal_eg/cea.tif")
      GeoTiffMetadata metadata

    when:
      try (def result = new GeoTiff(osgeoSample as InputStream)) {
        metadata = result.metadata()
      }

    then:
      noExceptionThrown()

    and:
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTModelType, ModelType.Projected.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTRasterType, RasterType.PixelIsArea.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTCitation, "unnamed")

      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeographicType, 4267)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogCitation, "NAD27")
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogAngularUnits, AngularUnit.Degree.code())

      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjectedCSType, 32767)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.Projection, 32767)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjCoordTrans, ProjectedCoordinateTransform.CylindricalEqualArea.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjLinearUnits, LinearUnit.Meter.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjStdParallel1, 33.75d)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjNatOriginLong, -117.333333333333d)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjFalseEasting, 0d)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjFalseNorthing, 0d)

    and:
      metadata.width == 514
      metadata.height == 515

      metadata.modelTiepoints() == [new ModelTiepoint(0, 0, 0, -28493.166784412522d, 4255884.5438021915d, 0.0d)]
      metadata.modelPixelScale() == new ModelPixelScale(60.02213698319374d, 60.02213698319374d, 0)
  }

  def 'should parse osgeo sample "GeogToWGS84GeoKey" correctly'() {
    given:
      def osgeoSample = this.class.getResourceAsStream("osgeo-samples/GeogToWGS84GeoKey/GeogToWGS84GeoKey5.tif")
      GeoTiffMetadata metadata

    when:
      try (def result = new GeoTiff(osgeoSample as InputStream)) {
        metadata = result.metadata()
      }

    then:
      noExceptionThrown()

    and:
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTModelType, ModelType.Geographic.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTRasterType, RasterType.PixelIsArea.code())

      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeographicType, 32767)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogGeodeticDatum, 32767)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogAngularUnits, AngularUnit.Degree.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogEllipsoid, Ellipsoid.Bessel_1841.code())

    and:
      metadata.width == 101
      metadata.height == 101

      metadata.modelTiepoints() == [new ModelTiepoint(50, 50, 0, 9.0010573796d, 52.0013760079d, 0.0d)]
      metadata.modelPixelScale() == new ModelPixelScale(2.77777778E-5d, 2.77777778E-5d, 1.0d)
  }

  def 'should parse osgeo sample "intergraph - utm" correctly'() {
    given:
      def osgeoSample = this.class.getResourceAsStream("osgeo-samples/intergraph/utm.tif")
      GeoTiffMetadata metadata

    when:
      try (def result = new GeoTiff(osgeoSample as InputStream)) {
        metadata = result.metadata()
      }

    then:
      noExceptionThrown()

    and:
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTModelType, ModelType.Projected.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTRasterType, RasterType.PixelIsArea.code())

      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjCoordTrans, 32767)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.Projection, 16016)

      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogGeodeticDatum, 6267)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogEllipsoid, Ellipsoid.Clarke_1866.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjNatOriginLong, -87d)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjNatOriginLat, 0d)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjFalseEasting, 500000d)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjFalseNorthing, 0d)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjScaleAtNatOrigin, 0.9996d)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.PCSCitation, "Universal Transverse Mercator North American 1927 Zone Number 16N")
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjectedCSType, 32767)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjLinearUnits, LinearUnit.Meter.code())

    and:
      metadata.width == 200
      metadata.height == 200

      metadata.modelTiepoints() == [new ModelTiepoint(0, 0, 0, 1871032.953888d, 693358.668144d, 0.0d)]
      metadata.modelPixelScale() == new ModelPixelScale(154.4800313997548d, 154.48003139998764d, 0)
  }
}
