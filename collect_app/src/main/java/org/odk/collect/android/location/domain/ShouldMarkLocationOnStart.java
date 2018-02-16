package org.odk.collect.android.location.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.Utility;
import org.odk.collect.android.location.injection.Qualifiers.InitialLocation;
import org.odk.collect.android.location.injection.Qualifiers.IsDraggable;
import org.odk.collect.android.utilities.Rx;

import javax.inject.Inject;

import io.reactivex.Observable;

@PerActivity
public class ShouldMarkLocationOnStart {

    @NonNull
    private final CurrentLocation currentLocation;

    @Nullable
    private final LatLng initialLocation;

    private final boolean isDraggable;
    private final boolean hasInitialLocation;

    @Inject
    ShouldMarkLocationOnStart(@NonNull CurrentLocation currentLocation,
                              @InitialLocation @Nullable LatLng initialLocation,
                              @IsDraggable boolean isDraggable) {
        this.currentLocation = currentLocation;
        this.initialLocation = initialLocation;

        this.isDraggable = isDraggable;
        this.hasInitialLocation = initialLocation != null;
    }

    public Observable<LatLng> observe() {
        return Observable.merge(
                shouldMarkFirstReceived(),
                shouldMarkInitialLocation()
        );
    }

    private Observable<LatLng> shouldMarkFirstReceived() {
        return currentLocation.observePresence()
                .filter(Rx::isTrue)
                .distinctUntilChanged()
                .flatMapSingle(__ -> currentLocation.get())
                .filter(__ -> !isDraggable && !hasInitialLocation)
                .map(Optional::get)
                .map(Utility::locationToLatLng);
    }

    private Observable<LatLng> shouldMarkInitialLocation() {
        return initialLocation != null
                ? Observable.just(initialLocation)
                : Observable.empty();
    }
}
