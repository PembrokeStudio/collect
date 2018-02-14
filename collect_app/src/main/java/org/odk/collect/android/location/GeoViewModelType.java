package org.odk.collect.android.location;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.odk.collect.android.location.model.ZoomData;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

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

    // Events:
    @NonNull
    Observable<Object> onLocationCleared();

    // Inputs:
    @NonNull
    Completable addLocation();

    @NonNull
    Completable pause();

    @NonNull
    Completable showLocation();

    @NonNull
    Completable showLayers();

    @NonNull
    Completable clearLocation();

    @NonNull
    Completable selectLocation(@NonNull LatLng latLng);

    @NonNull
    Completable clearSelectedLocation();

    @NonNull
    Single<String> saveLocation();

    @NonNull
    Observable<Object> watchLocation();
}
