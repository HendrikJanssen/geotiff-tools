package com.hendrikjanssen.geotifftools.geokeys.values;

public enum AngularUnit {

    Radian(9101),
    Degree(9102),
    Arc_Minute(9103),
    Arc_Second(9104),
    Grad(9105),
    Gon(9106),
    DMS(9107),
    DMS_Hemisphere(9108);

    private final int code;

    AngularUnit(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
