package com.hendrikjanssen.geotifftools.geokeys;

import com.hendrikjanssen.geotifftools.geokeys.values.GeographicCs;
import com.hendrikjanssen.geotifftools.geokeys.values.LinearUnit;
import com.hendrikjanssen.geotifftools.geokeys.values.ModelType;
import com.hendrikjanssen.geotifftools.geokeys.values.RasterType;

import java.util.HashMap;
import java.util.Map;

public enum GeoKeyId {

    GTModelType(1024, ModelType.class),
    GTRasterType(1025, RasterType.class),
    GTCitation(1026, null),
    GeographicType(2048, GeographicCs.class),
    GeogCitation(2049, null),
    GeogAngularUnits(2054, null),
    GeogAngularUnitSize(2055, null),
    ProjectedCSType(3072, null),
    Projection(3074, null),
    ProjCoordTrans(3075, null),
    ProjLinearUnits(3076, LinearUnit.class),
    ProjStdParallel1(3078, null),
    ProjStdParallel2(3079, null),
    ProjNatOriginLong(3080, null),
    ProjNatOriginLat(3081, null),
    ProjFalseEasting(3082, null),
    ProjFalseNorthing(3083, null);

    private final int id;
    private final Class<? extends Enum<?>> valueRepresentation;

    GeoKeyId(int id, Class<? extends Enum<?>> valueRepresentation) {
        this.id = id;
        this.valueRepresentation = valueRepresentation;
    }

    public int getId() {
        return this.id;
    }

    private static final Map<Integer, GeoKeyId> idMapping = new HashMap<>();

    static {
        for (GeoKeyId fieldTag : GeoKeyId.values()) {
            idMapping.put(fieldTag.getId(), fieldTag);
        }
    }

    public static GeoKeyId getById(int id) {
        return idMapping.get(id);
    }
}
