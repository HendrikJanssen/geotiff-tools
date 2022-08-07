package de.hendrikjanssen.geotifftools;

import de.hendrikjanssen.geotifftools.metadata.ModelPixelScale;
import de.hendrikjanssen.geotifftools.metadata.ModelTiepoint;
import org.geolatte.geom.Position;
import org.geolatte.geom.Positions;
import org.geolatte.geom.crs.CoordinateReferenceSystem;

class MathUtils {

    static <P extends Position> P transformRasterPointToModelPoint(
        CoordinateReferenceSystem<P> crs,
        RasterPoint rasterPoint,
        ModelTiepoint reference,
        ModelPixelScale pixelScale
    ) {
        double rasterDistanceX = (rasterPoint.x() - reference.i());
        double rasterDistanceY = (rasterPoint.y() - reference.j());

        double x = reference.x() + (rasterDistanceX * pixelScale.getX());
        double y = reference.y() - (rasterDistanceY * pixelScale.getY());

        return Positions.mkPosition(crs, x, y);
    }

    static <P extends Position> RasterPoint transformModelPointToRasterPoint(
        P modelPoint,
        ModelTiepoint reference,
        ModelPixelScale pixelScale
    ) {
        double modelDistanceX = (modelPoint.getCoordinate(0) - reference.x());
        double modelDistanceY = (modelPoint.getCoordinate(1) - reference.y());

        double x = reference.i() + (modelDistanceX * pixelScale.getX());
        double y = reference.j() - (modelDistanceY * pixelScale.getY());

        return new RasterPoint((long) x, (long) y);
    }
}
