package com.hendrikjanssen.geotifftools.metadata.geokeys;

import java.util.Objects;

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

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getK() {
        return k;
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
        return "ModelTiepoint{" +
               "i=" + i +
               ", j=" + j +
               ", k=" + k +
               ", x=" + x +
               ", y=" + y +
               ", z=" + z +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelTiepoint that = (ModelTiepoint) o;
        return i == that.i && j == that.j && k == that.k && Double.compare(that.x, x) == 0 && Double.compare(that.y,
            y) == 0 && Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j, k, x, y, z);
    }
}
