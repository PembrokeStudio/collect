package org.odk.collect.android.location.domain;


import android.location.Location;
import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.jakewharton.rxrelay2.BehaviorRelay;

import org.odk.collect.android.injection.scopes.PerActivity;

@PerActivity
public class CurrentPosition {

    @NonNull
    private final WatchPosition watchPosition;


    public CurrentPosition(@NonNull WatchPosition watchPosition) {
        this.watchPosition = watchPosition;
    }
}
