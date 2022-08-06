package de.hendrikjanssen.geotifftools.rendering

import de.hendrikjanssen.geotifftools.GeoTiff
import de.hendrikjanssen.geotifftools.rendering.coloring.SimpleColorTransform
import de.hendrikjanssen.geotifftools.rendering.sampling.IntegerSampler
import de.hendrikjanssen.geotifftools.rendering.transforming.FilterNoData
import de.hendrikjanssen.geotifftools.rendering.writing.RgbImageWriter
import spock.lang.Specification

import javax.imageio.ImageIO

class RenderTest extends Specification {

  def 'should render osgeo sample "intergraph - utm" correctly'() {
    given:
      def sentinelSample = this.class.getResourceAsStream("/de/hendrikjanssen/geotifftools/osgeo-samples/intergraph/utm.tif")
      def file = new File("./test.png")

    when:
      try (def result = new GeoTiff(sentinelSample as InputStream)) {

        def renderer = GeoTiffRendererBuilder
          .sampleWith(IntegerSampler.ofBandIndices(0))
          .then(new FilterNoData())
          .colorWith(new SimpleColorTransform())
          .writeWith(new RgbImageWriter())
          .build()

        def image = renderer.render(result, RenderTarget.ofOriginalGeoTiffSize(result))

        ImageIO.write(image, "png", file.newOutputStream())
      }

    then:
      noExceptionThrown()

    cleanup:
      if (file.exists()) {
        file.delete()
      }
  }
}
