package de.hendrikjanssen.geotifftools;

import de.hendrikjanssen.geotifftools.metadata.ModelPixelScale;
import de.hendrikjanssen.geotifftools.metadata.ModelTiepoint;
import org.geolatte.geom.Position;
import org.geolatte.geom.Positions;
import org.geolatte.geom.crs.CoordinateReferenceSystem;

import java.awt.Point;

class MathUtils {

    static <P extends Position> P transformRasterPointToModelPoint(
        CoordinateReferenceSystem<P> crs,
        Point rasterPoint,
        ModelTiepoint reference,
        ModelPixelScale pixelScale
    ) {
        double rasterDistanceX = (rasterPoint.x - reference.getI());
        double rasterDistanceY = (rasterPoint.y - reference.getJ());

        double x = reference.getX() + (rasterDistanceX * pixelScale.getX());
        double y = reference.getY() - (rasterDistanceY * pixelScale.getY());

        return Positions.mkPosition(crs, x, y);
    }

    static <P extends Position> Point transformModelPointToRasterPoint(
        P modelPoint,
        ModelTiepoint reference,
        ModelPixelScale pixelScale
    ) {
        double distanceX = (modelPoint.getCoordinate(0) - reference.getX());
        double distanceY = (modelPoint.getCoordinate(1) - reference.getY());

        double x = reference.getI() + (distanceX * pixelScale.getX());
        double y = reference.getJ() - (distanceY * pixelScale.getY());

        return new Point((int) x, (int) y);
    }
}
