package org.odk.collect.android.location.domain;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.injection.Qualifiers.InitialLocation;

import javax.inject.Inject;

import io.reactivex.Observable;

@PerActivity
public class ShouldZoom {

    @NonNull
    private final ZoomDialog zoomDialog;

    @Nullable
    private final LatLng initialLocation;

    @Inject
    public ShouldZoom(@NonNull ZoomDialog zoomDialog,
                      @InitialLocation @Nullable LatLng initialLocation) {
        this.zoomDialog = zoomDialog;
        this.initialLocation = initialLocation;
    }

    public Observable<LatLng> observe() {
        return Observable.merge(initialLocationObservable(), zoomDialog.zoomToLocation());
    }

    private Observable<LatLng> initialLocationObservable() {
        return initialLocation != null
                ? Observable.just(initialLocation)
                : Observable.empty();
    }
}
