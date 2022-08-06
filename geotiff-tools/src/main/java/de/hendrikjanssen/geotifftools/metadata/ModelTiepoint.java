package de.hendrikjanssen.geotifftools.metadata;

import java.util.Objects;

/**
 * Connects a point in raster space to a point in model space.
 *
 * @see <a href="http://geotiff.maptools.org/spec/geotiff2.6.html#2.6.1">Spec reference</a>
 */
public class ModelTiepoint {

    private final int i;
    private final int j;
    private final int k;

    private final double x;
    private final double y;
    private final double z;

    public ModelTiepoint(int i, int j, int k, double x, double y, double z) {
        this.i = i;
        this.j = j;
        this.k = k;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Imitate record-like access to properties
    // CHECKSTYLE:OFF
    public int i() {
        return i;
    }

    public int j() {
        return j;
    }

    public int k() {
        return k;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }
    // CHECKSTYLE:ON

    @Override
    public String toString() {
        return "ModelTiepoint{" + "i=" + i + ", j=" + j + ", k=" + k + ", x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModelTiepoint that = (ModelTiepoint) o;

        return i == that.i
               && j == that.j
               && k == that.k
               && Double.compare(that.x, x) == 0
               && Double.compare(that.y, y) == 0
               && Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j, k, x, y, z);
    }
}
