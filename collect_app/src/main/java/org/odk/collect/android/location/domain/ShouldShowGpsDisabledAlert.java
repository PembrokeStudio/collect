package org.odk.collect.android.location.domain;

import android.support.annotation.NonNull;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.utilities.Rx;

import javax.inject.Inject;

import io.reactivex.Observable;

@PerActivity
public class ShouldShowGpsDisabledAlert {

    @NonNull
    private final WatchPosition watchPosition;

    @Inject
    ShouldShowGpsDisabledAlert(@NonNull WatchPosition watchPosition) {
        this.watchPosition = watchPosition;
    }

    public Observable<Object> observe() {
        return watchPosition.observeAvailability()
                .filter(isAvailable -> !isAvailable)
                .map(Rx::toEvent);
    }
}
