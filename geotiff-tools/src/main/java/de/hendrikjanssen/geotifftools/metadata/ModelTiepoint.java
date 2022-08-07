package de.hendrikjanssen.geotifftools.metadata;

/**
 * Connects a point in raster space to a point in model space.
 *
 * @see <a href="http://geotiff.maptools.org/spec/geotiff2.6.html#2.6.1">Spec reference</a>
 */
public record ModelTiepoint(int i, int j, int k, double x, double y, double z) {

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
}
