package com.hendrikjanssen.geotifftools.geokeys.values;

public enum ProjectedCoordinateTransform {

    TransverseMercator(1),
    TransvMercator_Modified_Alaska(2),
    ObliqueMercator(3),
    ObliqueMercator_Laborde(4),
    ObliqueMercator_Rosenmund(5),
    ObliqueMercator_Spherical(6),
    Mercator(7),
    LambertConfConic_2SP(8),
    LambertConfConic_Helmert(9),
    LambertAzimEqualArea(10),
    AlbersEqualArea(11),
    AzimuthalEquidistant(12),
    EquidistantConic(13),
    Stereographic(14),
    PolarStereographic(15),
    ObliqueStereographic(16),
    Equirectangular(17),
    CassiniSoldner(18),
    Gnomonic(19),
    MillerCylindrical(20),
    Orthographic(21),
    Polyconic(22),
    Robinson(23),
    Sinusoidal(24),
    VanDerGrinten(25),
    NewZealandMapGrid(26),
    TransvMercator_SouthOriented(7),
    CylindricalEqualArea(28);

    private final int code;

    ProjectedCoordinateTransform(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
