package org.odk.collect.android.location.mapviewmodel;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface MapViewModel {

    // Outputs:
    @NonNull
    Observable<Optional<LatLng>> observeMarkedLocation();

    // Inputs:
    @NonNull
    Completable markLocation(@NonNull LatLng latLng);

    @NonNull
    Completable clearMarkedLocation();

    @NonNull
    Completable zoomToLocation(@NonNull LatLng latLng);

    @NonNull
    Completable showLayers();
}
