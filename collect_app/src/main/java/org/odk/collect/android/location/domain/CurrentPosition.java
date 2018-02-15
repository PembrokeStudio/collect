package org.odk.collect.android.location.domain;


import android.location.Location;
import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.jakewharton.rxrelay2.BehaviorRelay;

import org.odk.collect.android.injection.scopes.PerActivity;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

@PerActivity
public class CurrentPosition {

    @NonNull
    private final WatchPosition watchPosition;

    @Inject
    public CurrentPosition(@NonNull WatchPosition watchPosition) {
        this.watchPosition = watchPosition;
    }

    public Observable<Location> observe() {
        return watchPosition.observeLocation()
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Single<Optional<Location>> get() {
        return watchPosition.observeLocation()
                .firstOrError();
    }

    public Observable<Boolean> observePresence() {
        return watchPosition.observeLocation()
                .map(Optional::isPresent)
                .distinctUntilChanged();
    }
}
