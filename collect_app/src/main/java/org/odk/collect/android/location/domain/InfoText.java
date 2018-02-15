package org.odk.collect.android.location.domain;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.odk.collect.android.R;
import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.injection.Qualifiers.HasInitialLocation;
import org.odk.collect.android.location.injection.Qualifiers.IsDraggable;
import org.odk.collect.android.location.injection.Qualifiers.IsReadOnly;
import org.odk.collect.android.location.model.MapFunction;

import javax.inject.Inject;

import io.reactivex.Observable;

@PerActivity
public class InfoText {
    @NonNull
    private final Context context;

    @NonNull
    private final MapFunction mapFunction;

    @NonNull
    private final SelectedLocation selectedLocation;

    private final boolean isDraggable;
    private final boolean isReadOnly;
    private final boolean hasInitialLocation;

    @Inject
    public InfoText(@NonNull Context context,
                    @NonNull MapFunction mapFunction,
                    @NonNull SelectedLocation selectedLocation,
                    @IsDraggable boolean isDraggable,
                    @IsReadOnly boolean isReadOnly,
                    @HasInitialLocation boolean hasInitialLocation) {
        this.context = context;
        this.mapFunction = mapFunction;
        this.selectedLocation = selectedLocation;
        this.isDraggable = isDraggable;
        this.isReadOnly = isReadOnly;
        this.hasInitialLocation = hasInitialLocation;
    }

    public Observable<String> observeText() {
        return Observable.just(isDraggable
                ? R.string.geopoint_instruction
                : R.string.geopoint_no_draggable_instruction

        ).map(context::getString);
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
