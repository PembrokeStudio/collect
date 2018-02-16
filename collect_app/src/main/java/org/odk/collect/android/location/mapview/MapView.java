package org.odk.collect.android.location.mapview;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface MapView {

    @NonNull
    Observable<LatLng> observeLongPress();

    @NonNull
    Observable<LatLng> observeMarkerMoved();

    Completable markLocation(@NonNull LatLng latLng);
    Completable clearLocation();

    Completable zoomToLocation(@NonNull LatLng latLng);
    Completable showLayers();
}
