package org.odk.collect.android.location;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface GeoViewModelType {
    // UI State
    @NonNull
    Observable<String> locationInfoText();

    @NonNull
    Observable<String> locationStatusText();

    @NonNull
    Observable<Integer> locationInfoVisibility();

    @NonNull
    Observable<Integer> locationStatusVisibility();

    @NonNull
    Observable<Integer> pauseButtonVisibility();

    @NonNull
    Observable<Boolean> isAddLocationEnabled();

    @NonNull
    Observable<Boolean> isShowLocationEnabled();

    @NonNull
    Observable<Boolean> isClearLocationEnabled();

    @NonNull
    Observable<LatLng> locationSelected();

    @NonNull
    Observable<Object> locationCleared();

    @NonNull
    Observable<LatLng> shouldZoomToLocation();

    // Inputs:
    @NonNull
    Completable addLocation();

    @NonNull
    Completable pause();

    @NonNull
    Completable showLocation();

    @NonNull
    Completable clearLocation();

    @NonNull
    Completable saveLocation();

    @NonNull
    Completable mapLongPressed(@NonNull LatLng latLng);

    @NonNull
    Completable markerMoved(@NonNull LatLng latLng);

    @NonNull
    Observable<Object> watchLocation();
}
