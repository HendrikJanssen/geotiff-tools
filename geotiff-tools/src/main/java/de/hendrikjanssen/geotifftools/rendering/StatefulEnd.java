package de.hendrikjanssen.geotifftools.rendering;

import de.hendrikjanssen.geotifftools.GeoTiff;

public interface StatefulEnd {

    void onEnd(GeoTiff geoTiff);
}
