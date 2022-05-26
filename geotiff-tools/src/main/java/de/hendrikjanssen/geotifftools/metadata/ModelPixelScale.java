package de.hendrikjanssen.geotifftools.metadata;

import java.util.Objects;

public class ModelPixelScale {

    private final double x;
    private final double y;
    private final double z;

    public ModelPixelScale(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "ModelPixelScale{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModelPixelScale that = (ModelPixelScale) o;

        return Double.compare(that.x, x) == 0
               && Double.compare(that.y, y) == 0
               && Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
