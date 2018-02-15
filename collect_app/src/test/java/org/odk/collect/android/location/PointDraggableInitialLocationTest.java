package org.odk.collect.android.location;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class PointDraggableInitialLocationTest extends PointDraggableTest {

    private final LatLng latLng = randomLatLng();

    @Nullable
    @Override
    protected LatLng initialLocation() {
        return latLng;
    }


}
