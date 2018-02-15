package org.odk.collect.android.location.domain;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.odk.collect.android.R;
import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.injection.Qualifiers.HasInitialLocation;
import org.odk.collect.android.location.injection.Qualifiers.IsReadOnly;
import org.odk.collect.android.location.model.MapFunction;

import javax.inject.Inject;

import io.reactivex.Observable;

@PerActivity
public class StatusText {
    @NonNull
    private final Context context;

    @NonNull
    private final MapFunction mapFunction;

    @NonNull
    private final WatchPosition watchPosition;

    @NonNull
    private final SelectedLocation selectedLocation;

    @NonNull
    private final LocationFormatter locationFormatter;

    private final boolean isReadOnly;
    private final boolean hasInitialLocation;

    @Inject
    public StatusText(@NonNull Context context,
                      @NonNull MapFunction mapFunction,
                      @NonNull WatchPosition watchPosition,
                      @NonNull SelectedLocation selectedLocation,
                      @NonNull LocationFormatter locationFormatter,
                      @IsReadOnly boolean isReadOnly,
                      @HasInitialLocation boolean hasInitialLocation) {
        this.context = context;
        this.mapFunction = mapFunction;
        this.watchPosition = watchPosition;
        this.selectedLocation = selectedLocation;
        this.locationFormatter = locationFormatter;

        this.isReadOnly = isReadOnly;
        this.hasInitialLocation = hasInitialLocation;
    }

    public Observable<String> observeText() {
        return watchPosition.observeLocation()
                .map(currentLocation -> currentLocation.isPresent()
                        ? locationFormatter.getStringForLocation(currentLocation.get())
                        : context.getString(R.string.please_wait_long));

    }

    public Observable<Integer> observeVisibility() {
        return selectedLocation.hasBeenCleared()
                .map(this::shouldHide);
    }

    private int shouldHide(boolean wasCleared) {
        return isReadOnly || (hasInitialLocation && !wasCleared)
                ? View.GONE
                : View.VISIBLE;
    }
}
