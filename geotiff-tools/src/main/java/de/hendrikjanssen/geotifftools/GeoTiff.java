package de.hendrikjanssen.geotifftools;

import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageMetadata;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader;
import de.hendrikjanssen.geotifftools.metadata.GeoTiffMetadata;
import de.hendrikjanssen.geotifftools.metadata.ModelPixelScale;
import de.hendrikjanssen.geotifftools.metadata.ModelTiepoint;
import de.hendrikjanssen.geotifftools.metadata.geokeys.GeoKey;
import de.hendrikjanssen.geotifftools.metadata.geokeys.GeoKeyId;
import de.hendrikjanssen.geotifftools.metadata.geokeys.values.ModelType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;

/**
 * The main class that all geotiff operations are based on.
 * It handles things like loading the geotiff metadata and exposes methods for common
 * transform operations.
 */
public class GeoTiff implements AutoCloseable {

    private final GeoTiffMetadata metaData;

    private final ImageInputStream imageInputStream;

    private final TIFFImageReader imageReader;

    public GeoTiff(File file) throws MalformedGeoTiffException, IOException {
        this(new FileInputStream(file));
    }

    public GeoTiff(InputStream inputStream) throws MalformedGeoTiffException, IOException {
        this.imageInputStream = ImageIO.createImageInputStream(inputStream);

        this.imageReader = initializeImageReader(imageInputStream);

        TIFFImageMetadata metadata = readTiffTags(this.imageReader);

        this.metaData = new GeoTiffMetadata(metadata);
    }

    private TIFFImageReader initializeImageReader(ImageInputStream imageInputStream) {
        ImageReader imageReader = ImageIO.getImageReadersByFormatName("tiff").next();

        if (!(imageReader instanceof TIFFImageReader)) {
            throw new IllegalStateException("");
        }

        imageReader.setInput(imageInputStream, false, false);
        return (TIFFImageReader) imageReader;
    }

    private TIFFImageMetadata readTiffTags(TIFFImageReader imageReader) throws IOException {

        return (TIFFImageMetadata) imageReader.getImageMetadata(0);
    }

    public BufferedImage readImageIntoBuffer() throws IOException {
        return imageReader.read(0);
    }

    /**
     * Gets the metadata associated with this geotiff.
     *
     * @return The loaded metadata contained in the geotiff.
     */
    public GeoTiffMetadata metadata() {
        return this.metaData;
    }

    /**
     * Returns the ModelType specified inside the geokey metadata of the geotiff,
     * or ModelType.UNKNOWN if the ModelType is unspecified.
     *
     * @return The ModelType specified by the geotiff metadata.
     * @see <a href="http://geotiff.maptools.org/spec/geotiff6.html#6.3.1.1">Spec reference</a>
     */
    @Nullable
    public ModelType modelType() {
        GeoKey modelType = this.metaData.getGeoKey(GeoKeyId.GTModelType);

        if (modelType == null) {
            return ModelType.Unknown;
        }

        return ModelType.ofCode(modelType.getValueAsInt());
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
     * @return The transformed point or null if any information is missing to transform the given raster point.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <P extends Position> P transformRasterPointToModelPoint(int rasterX, int rasterY) {
        ModelType modelType = this.modelType();
        if (ModelType.Unknown.equals(modelType)) {
            return null;
        }

        if (ModelType.Geocentric.equals(modelType)) {
            throw new UnsupportedOperationException("Only Projected and Geographic ModelTypes are supported");
        }

        CoordinateReferenceSystem<P> crs = (CoordinateReferenceSystem<P>) this.getCoordinateReferenceSystem();
        ModelPixelScale pixelScale = this.metaData.modelPixelScale();
        List<ModelTiepoint> modelTiepoints = this.metaData.modelTiepoints();

        if (crs == null || pixelScale == null || modelTiepoints == null) {
            return null;
        }

        return MathUtils.transformRasterPointToModelPoint(
            crs,
            new RasterPoint(rasterX, rasterY),
            modelTiepoints.get(0), // TODO: Select tiepoint based on raster distance
            pixelScale
        );
    }

    /**
     * Transforms a model point into raster space.
     * If any parameter needed for the transformation is not specified in the geotiff, the function will return an empty Optional.
     * Note that no bounds checking is performed, so it is perfectly fine to receive a result like (-10, -10).
     * Raster coordinates are not rounded, but cut off by simply converting the resulting raster coordinates to integers.
     *
     * @param modelPoint A point in model space.
     * @param <P>        The type of the Position in model, usually a G2D
     * @return An Optional containing the transformed point, or an empty Optional
     * if any information needed for the transformation is not present.
     */
    @Nullable
    public <P extends Position> RasterPoint transformModelPointToRasterPoint(P modelPoint) {
        ModelType modelType = this.modelType();

        if (modelType == null || ModelType.Unknown.equals(modelType)) {
            return null;
        }

        if (ModelType.Geocentric.equals(modelType)) {
            throw new UnsupportedOperationException("Only Projected and Geographic ModelTypes are supported");
        }

        ModelPixelScale pixelScale = this.metaData.modelPixelScale();
        List<ModelTiepoint> modelTiepoints = this.metaData.modelTiepoints();

        if (pixelScale == null || modelTiepoints == null) {
            return null;
        }

        return MathUtils.transformModelPointToRasterPoint(
            modelPoint,
            modelTiepoints.get(0),
            pixelScale
        );
    }

    /**
     * Calculates the envelope of the geotiff by transforming the lower left and upper right corners of the geotiff into model space.
     * If any parameter needed for the transformation is not specified in the geotiff, the function will return null.
     *
     * @param <P> The type of Positions of the model space, usually a G2D
     * @return The calculated bounding box, or null if any information needed for envelope calculation is missing.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <P extends Position> Envelope<P> getEnvelope() {
        ModelType modelType = this.modelType();

        if (ModelType.Unknown.equals(modelType)) {
            return null;
        }

        if (ModelType.Geocentric.equals(modelType)) {
            throw new UnsupportedOperationException("Only Projected and Geographic ModelTypes are supported");
        }

        CoordinateReferenceSystem<P> crs = (CoordinateReferenceSystem<P>) this.getCoordinateReferenceSystem();
        ModelPixelScale pixelScale = this.metaData.modelPixelScale();
        List<ModelTiepoint> modelTiepoints = this.metaData.modelTiepoints();

        if (crs == null || pixelScale == null || modelTiepoints == null) {
            return null;
        }

        // TODO: Select tiepoint based on raster distance to the points
        ModelTiepoint referenceTiepoint = modelTiepoints.get(0);

        P lowerLeft = MathUtils.transformRasterPointToModelPoint(
            crs,
            new RasterPoint(0, this.metaData.getHeight()),
            referenceTiepoint,
            pixelScale
        );

        P upperRight = MathUtils.transformRasterPointToModelPoint(
            crs,
            new RasterPoint(this.metaData.getWidth(), 0),
            referenceTiepoint,
            pixelScale
        );

        return new Envelope<>(lowerLeft, upperRight, crs);
    }


    /**
     * Reads the CRS (coordinate reference system) as specified in the GeoTiff metadata.
     * Only known EPSG codes are recognized (this is a limitation of Geolatte) and only projected
     * or geographic CRS are supported. If any information for determining the CRS is missing or the EPSG code is unknown,
     * returns null.
     *
     * @return The CRS or null if any information is missing from the GeoTiff to specify the CRS.
     * @see <a href="http://geotiff.maptools.org/spec/geotiff6.html#6.3.2.1">Recognized Geographic EPSG codes</a>
     * @see <a href="http://geotiff.maptools.org/spec/geotiff6.html#6.3.3.1">Recognized Projected EPSG codes</a>
     */
    @Nullable
    public CoordinateReferenceSystem<? extends Position> getCoordinateReferenceSystem() {
        ModelType modelType = this.modelType();
        if (modelType == null) {
            return null;
        }

        return switch (modelType) {
            // These are known EPSG codes, everything above or below are user-defined or obsolete codes
            case Projected -> readCrsId(GeoKeyId.ProjectedCSType, 20000, 32760);
            case Geographic -> readCrsId(GeoKeyId.GeographicType, 4000, 4999);
            default -> null;
        };
    }

    @Nullable
    private CoordinateReferenceSystem<? extends Position> readCrsId(int geoKeyId, int minId, int maxId) {
        GeoKey projectedCsTypeKey = this.metaData.getGeoKey(geoKeyId);

        if (projectedCsTypeKey == null) {
            return null;
        }

        int crsId = projectedCsTypeKey.getValueAsInt();
        if (crsId < minId || crsId > maxId) {
            return null;
        }

        return CrsRegistry.getCoordinateReferenceSystemForEPSG(crsId, null);
    }

    @Override
    public void close() throws IOException {
        this.imageInputStream.flush();
        this.imageInputStream.close();

        this.imageReader.setInput(null);
        this.imageReader.dispose();
    }
}
