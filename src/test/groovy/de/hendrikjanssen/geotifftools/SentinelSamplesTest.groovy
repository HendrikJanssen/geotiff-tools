package de.hendrikjanssen.geotifftools

import de.hendrikjanssen.geotifftools.metadata.GeoTiffMetadata
import de.hendrikjanssen.geotifftools.metadata.ModelPixelScale
import de.hendrikjanssen.geotifftools.metadata.geokeys.GeoKeyId
import de.hendrikjanssen.geotifftools.metadata.ModelTiepoint
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.AngularUnit
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.LinearUnit
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.ModelType
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.RasterType
import org.geolatte.geom.C2D
import org.geolatte.geom.Envelope
import org.geolatte.geom.Position
import org.geolatte.geom.crs.CoordinateReferenceSystem
import spock.lang.Specification

class SentinelSamplesTest extends Specification {

  def 'should parse sentinel sample "sample" correctly'() {
    given:
      def sentinelSample = this.class.getResourceAsStream("sentinel-samples/sample.tif")
      GeoTiffMetadata metadata
      Optional<? extends CoordinateReferenceSystem<? extends Position>> crsOpt
      Optional<Envelope<C2D>> boundingBox

    when:
      try (def result = new GeoTiff(sentinelSample as InputStream)) {
        metadata = result.metadata
        crsOpt = result.getCoordinateReferenceSystem()
        boundingBox = result.getEnvelope()
      }

    then:
      noExceptionThrown()

    and:
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTModelType, ModelType.Projected.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTRasterType, RasterType.PixelIsArea.code())
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GTCitation, "WGS 84 / UTM zone 31N")

      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogCitation, "WGS 84")
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.GeogAngularUnits, AngularUnit.Degree.code())

      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjectedCSType, 32631)
      TestUtils.geoKeyHasProperty(metadata, GeoKeyId.ProjLinearUnits, LinearUnit.Meter.code())

    and:
      metadata.width == 1001
      metadata.height == 1001

      metadata.modelTiepoints.get() == [new ModelTiepoint(0, 0, 0, 590520.0d, 5790630.0d, 0.0d)]
      metadata.modelPixelScale.get() == new ModelPixelScale(10.0d, 10.0d, 0)

    and: 'should return correct geolatte information'
      crsOpt.isPresent()
      crsOpt.get().name == 'WGS 84 / UTM zone 31N'

      boundingBox.isPresent()
      boundingBox.get() == new Envelope(new C2D(590520.0d, 5780620.0d), new C2D(600530.0d, 5790630.0d), crsOpt.get())
  }
}
