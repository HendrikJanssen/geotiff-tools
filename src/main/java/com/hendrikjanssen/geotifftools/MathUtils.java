package com.hendrikjanssen.geotifftools;

import com.hendrikjanssen.geotifftools.metadata.ModelPixelScale;
import com.hendrikjanssen.geotifftools.metadata.ModelTiepoint;
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
}
