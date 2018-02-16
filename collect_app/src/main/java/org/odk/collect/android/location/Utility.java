package org.odk.collect.android.location;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class Utility {
    @NonNull
    public static LatLng locationToLatLng(@NonNull Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
