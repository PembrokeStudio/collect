package org.odk.collect.android.location.mapviewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;

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
