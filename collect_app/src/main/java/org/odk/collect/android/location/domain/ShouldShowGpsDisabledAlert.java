package org.odk.collect.android.location.domain;

import android.support.annotation.NonNull;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.utilities.Rx;

import javax.inject.Inject;

import io.reactivex.Observable;

@PerActivity
public class ShouldShowGpsDisabledAlert {

    @NonNull
    private final WatchLocation watchLocation;

    @Inject
    ShouldShowGpsDisabledAlert(@NonNull WatchLocation watchLocation) {
        this.watchLocation = watchLocation;
    }

    public Observable<Object> observe() {
        return watchLocation.observeAvailability()
                .filter(isAvailable -> !isAvailable)
                .map(Rx::toEvent);
    }
}
