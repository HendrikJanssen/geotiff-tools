package de.hendrikjanssen.geotifftools.rendering;

import de.hendrikjanssen.geotifftools.GeoTiff;

public interface StatefulBegin {

    void onBegin(GeoTiff geoTiff);
}
