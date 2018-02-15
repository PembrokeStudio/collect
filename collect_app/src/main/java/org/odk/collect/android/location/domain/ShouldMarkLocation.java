package org.odk.collect.android.location.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;
import com.jakewharton.rxrelay2.PublishRelay;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.injection.Qualifiers.InitialLocation;
import org.odk.collect.android.location.injection.Qualifiers.IsDraggable;
import org.odk.collect.android.location.injection.Qualifiers.IsReadOnly;
import org.odk.collect.android.utilities.Rx;

import javax.inject.Inject;

import io.reactivex.Observable;

@PerActivity
public class ShouldMarkLocation {

    @NonNull
    private final CurrentPosition currentPosition;

    @Nullable
    private final LatLng initialLocation;

    private final boolean isDraggable;
    private final boolean isReadOnly;
    private final boolean hasInitialLocation;

    private PublishRelay<LatLng> shouldMarkRelay = PublishRelay.create();
    private Observable<LatLng> shouldMark = shouldMarkRelay.hide();

    @Inject

    public ShouldMarkLocation(@NonNull CurrentPosition currentPosition,
                              @InitialLocation @Nullable LatLng initialLocation,
                              @IsDraggable boolean isDraggable,
                              @IsReadOnly boolean isReadOnly) {
        this.currentPosition = currentPosition;
        this.initialLocation = initialLocation;

        this.isDraggable = isDraggable;
        this.isReadOnly = isReadOnly;
        this.hasInitialLocation = initialLocation != null;
    }

    public Observable<LatLng> observe() {
        return Observable.merge(
                shouldMark,
                shouldMarkFirstReceived(),
                shouldMarkInitialLocation()
        );
    }

    private Observable<LatLng> shouldMarkFirstReceived() {
        return currentPosition.observePresence()
                .filter(Rx::isTrue)
                .distinctUntilChanged()
                .flatMapSingle(__ -> currentPosition.get())
                .filter(__ -> !isDraggable && !hasInitialLocation && !isReadOnly)
                .map(Optional::get)
                .map(Utility::locationToLatLng);
    }

    private Observable<LatLng> shouldMarkInitialLocation() {
        return initialLocation != null
                ? Observable.just(initialLocation)
                : Observable.empty();
    }

    public void shouldMark(@NonNull LatLng latLng) {
        shouldMarkRelay.accept(latLng);
    }
}
