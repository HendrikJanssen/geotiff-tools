package de.hendrikjanssen.geotifftools


import de.hendrikjanssen.geotifftools.rendering.GeoTiffRendererBuilder
import de.hendrikjanssen.geotifftools.rendering.IntegerSample
import de.hendrikjanssen.geotifftools.rendering.IntegerSampler
import de.hendrikjanssen.geotifftools.rendering.RenderTarget
import de.hendrikjanssen.geotifftools.rendering.RgbImageWriter
import de.hendrikjanssen.geotifftools.rendering.SimpleColorTransform
import spock.lang.Specification

import javax.imageio.ImageIO

class RenderTest extends Specification {

  def 'should render osgeo sample "intergraph - utm" correctly'() {
    given:
      def sentinelSample = this.class.getResourceAsStream("osgeo-samples/intergraph/utm.tif")
      def output = new File("./test.png").newOutputStream()

    when:
      try (def result = new GeoTiff(sentinelSample as InputStream)) {

        def renderer = GeoTiffRendererBuilder
          .sampleWith(IntegerSampler.ofBandIndices(0))
          .then((GeoTiff geoTiff, IntegerSample sample) -> {
            return sample
          })
          .colorWith(new SimpleColorTransform())
          .writeWith(new RgbImageWriter())
          .build()

        def image = renderer.render(result, RenderTarget.ofOriginalGeoTiffSize(result))

        ImageIO.write(image, "png", output)
      }

    then:
      noExceptionThrown()
  }
}
