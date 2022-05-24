package de.hendrikjanssen.geotifftools;

import de.hendrikjanssen.geotifftools.metadata.GeoTiffMetadata;
import de.hendrikjanssen.geotifftools.metadata.ModelPixelScale;
import de.hendrikjanssen.geotifftools.metadata.ModelTiepoint;
import de.hendrikjanssen.geotifftools.metadata.geokeys.GeoKey;
import de.hendrikjanssen.geotifftools.metadata.geokeys.GeoKeyId;
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.ModelType;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.tiff.TIFFDirectory;
import javax.imageio.stream.ImageInputStream;

/**
 * The main class that all geotiff operations are based on.
 * It handles things like loading the geotiff metadata and exposes methods for common
 * transform operations.
 */
public class GeoTiff implements AutoCloseable {

    private final GeoTiffMetadata metaData;

    private final ImageInputStream imageInputStream;

    private final ImageReader imageReader;

    public GeoTiff(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public GeoTiff(InputStream inputStream) throws IOException {

        this.imageInputStream = ImageIO.createImageInputStream(inputStream);

        this.imageReader = initializeImageReader(imageInputStream);

        TIFFDirectory tiffDirectory = readTiffTags(this.imageReader);

        this.metaData = new GeoTiffMetadata(tiffDirectory);
    }

    private ImageReader initializeImageReader(ImageInputStream imageInputStream) {

        ImageReader imageReader = ImageIO.getImageReadersByFormatName("tiff").next();
        imageReader.setInput(imageInputStream, false, false);
        return imageReader;
    }

    private TIFFDirectory readTiffTags(ImageReader imageReader) throws IOException {

        IIOMetadata metadata = imageReader.getImageMetadata(0);
        return TIFFDirectory.createFromMetadata(metadata);
    }

    public BufferedImage readImageIntoBuffer() throws IOException {
        return imageReader.read(0);
    }

    /**
     * Gets the metadata associated with this geotiff.
     *
     * @return The loaded metadata contained in the geotiff.
     */
    public GeoTiffMetadata getMetadata() {
        return this.metaData;
    }

    /**
     * Returns the ModelType specified inside the geokey metadata of the geotiff,
     * or ModelType.UNKNOWN if the ModelType is unspecified.
     *
     * @return The ModelType specified by the geotiff metadata.
     * @see <a href="http://geotiff.maptools.org/spec/geotiff6.html#6.3.1.1">Spec reference</a>
     */
    public ModelType getModelType() {
        return this.metaData.getGeoKey(GeoKeyId.GTModelType)
            .map(GeoKey::getValueAsInt)
            .flatMap(ModelType::ofCode)
            .orElse(ModelType.Unknown);
    }

    /**
     * Transforms a point in raster space into model space.
     * If any parameter needed for the transformation is not specified in the geotiff, the function will return an empty Optional.
     * This function supports specifying raster points outside the raster space of the geotiff, like (-10, -20).
     * Note that the further you stray from any model tiepoints, the more inaccurate the interpolation will probably be.
     *
     * @param rasterX The X-coordinate (horizontal axis) of a point in raster space
     * @param rasterY The Y-coordinate (vertical axis) of a point in raster space
     * @param <P>     The type of the point resulting from the transformation, usually a G2D
     * @return An Optional containing the transformed point.
     */
    @SuppressWarnings("unchecked")
    public <P extends Position> Optional<P> transformRasterPointToModelPoint(int rasterX, int rasterY) {
        ModelType modelType = this.getModelType();

        if (ModelType.Unknown.equals(modelType)) {
            return Optional.empty();
        }

        if (ModelType.Geocentric.equals(modelType)) {
            throw new UnsupportedOperationException("Only Projected and Geographic ModelTypes are supported");
        }

        Optional<? extends CoordinateReferenceSystem<P>> crs =
            (Optional<? extends CoordinateReferenceSystem<P>>) this.getCoordinateReferenceSystem();
        Optional<ModelPixelScale> pixelScale = this.metaData.getModelPixelScale();
        Optional<List<ModelTiepoint>> modelTiepoints = this.metaData.getModelTiepoints();

        if (crs.isEmpty() || pixelScale.isEmpty() || modelTiepoints.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(MathUtils.transformRasterPointToModelPoint(
            crs.get(),
            new Point(rasterX, rasterY),
            modelTiepoints.get().get(0), // TODO: Select tiepoint based on raster distance
            pixelScale.get()
        ));
    }

    /**
     * Transforms a model point into raster space.
     * If any parameter needed for the transformation is not specified in the geotiff, the function will return an empty Optional.
     * Note that no bounds checking is performed, so it is perfectly fine to receive a result like (-10, -10).
     * Raster coordinates are not rounded, but cut off by simply converting the resulting raster coordinates to integers.
     *
     * @param modelPoint A point in model space.
     * @return An Optional containing the transformed point, or an empty Optional
     * if any information needed for the transformation is not present.
     * @param <P> The type of the Position in model, usually a G2D
     */
    public <P extends Position> Optional<Point> transformModelPointToRasterPoint(P modelPoint) {
        ModelType modelType = this.getModelType();

        if (ModelType.Unknown.equals(modelType)) {
            return Optional.empty();
        }

        if (ModelType.Geocentric.equals(modelType)) {
            throw new UnsupportedOperationException("Only Projected and Geographic ModelTypes are supported");
        }

        Optional<ModelPixelScale> pixelScale = this.metaData.getModelPixelScale();
        Optional<List<ModelTiepoint>> modelTiepoints = this.metaData.getModelTiepoints();

        if (pixelScale.isEmpty() || modelTiepoints.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(MathUtils.transformModelPointToRasterPoint(
            modelPoint,
            modelTiepoints.get().get(0),
            pixelScale.get()
        ));
    }

    /**
     * Calculates the envelope of the geotiff by transforming the lower left and upper right corners of the geotiff into model space.
     * If any parameter needed for the transformation is not specified in the geotiff, the function will return an empty Optional.
     *
     * @param <P> The type of Positions of the model space, usually a G2D
     * @return An Optional containing the calculated bounding box, or an empty Optional
     * if any information needed for envelope calculation is missing.
     */
    @SuppressWarnings("unchecked")
    public <P extends Position> Optional<Envelope<P>> getEnvelope() {
        ModelType modelType = this.getModelType();

        if (ModelType.Unknown.equals(modelType)) {
            return Optional.empty();
        }

        if (ModelType.Geocentric.equals(modelType)) {
            throw new UnsupportedOperationException("Only Projected and Geographic ModelTypes are supported");
        }

        Optional<? extends CoordinateReferenceSystem<P>> crs =
            (Optional<? extends CoordinateReferenceSystem<P>>) this.getCoordinateReferenceSystem();
        Optional<ModelPixelScale> pixelScale = this.metaData.getModelPixelScale();
        Optional<List<ModelTiepoint>> modelTiepoints = this.metaData.getModelTiepoints();

        if (crs.isEmpty() || pixelScale.isEmpty() || modelTiepoints.isEmpty()) {
            return Optional.empty();
        }

        // TODO: Select tiepoint based on raster distance to the points
        ModelTiepoint referenceTiepoint = modelTiepoints.get().get(0);

        P lowerLeft = MathUtils.transformRasterPointToModelPoint(
            crs.get(),
            new Point(0, this.metaData.getHeight()),
            referenceTiepoint,
            pixelScale.get()
        );

        P upperRight = MathUtils.transformRasterPointToModelPoint(
            crs.get(),
            new Point(this.metaData.getWidth(), 0),
            referenceTiepoint,
            pixelScale.get()
        );

        Envelope<P> envelope = new Envelope<>(lowerLeft, upperRight, crs.get());

        return Optional.of(envelope);
    }

    public Optional<? extends CoordinateReferenceSystem<? extends Position>> getCoordinateReferenceSystem() {
        ModelType modelType = this.getModelType();

        switch (modelType) {
            case Projected:
                return this.metaData.getGeoKey(GeoKeyId.ProjectedCSType)
                    .map(GeoKey::getValueAsInt)
                    // These are known EPSG codes, everything above or below are user-defined or obsolete codes
                    .filter(projectedCrsId -> projectedCrsId >= 20000 && projectedCrsId <= 32760)
                    .map(CrsRegistry::getProjectedCoordinateReferenceSystemForEPSG);
            case Geographic:
                return this.metaData.getGeoKey(GeoKeyId.GeographicType)
                    .map(GeoKey::getValueAsInt)
                    // These are known EPSG codes, everything above or below are user-defined or obsolete codes
                    .filter(geographicCrsId -> geographicCrsId >= 4000 && geographicCrsId <= 4999)
                    .map(CrsRegistry::getGeographicCoordinateReferenceSystemForEPSG);
            default:
                return Optional.empty();
        }
    }

    @Override
    public void close() throws IOException {
        this.imageInputStream.flush();
        this.imageInputStream.close();

        this.imageReader.setInput(null);
        this.imageReader.dispose();
    }
}
