package org.odk.collect.android.location.domain;

import android.content.Context;
import android.support.annotation.NonNull;

import org.odk.collect.android.R;
import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.injection.Qualifiers;
import org.odk.collect.android.location.model.MapFunction;

import javax.inject.Inject;

import io.reactivex.Observable;

@PerActivity
public class StatusText {
    @NonNull
    private final Context context;

    @NonNull
    private final MapFunction mapFunction;

    private final boolean isDraggable;

    @Inject
    public StatusText(@NonNull Context context,
                    @NonNull MapFunction mapFunction,
                    @Qualifiers.IsDraggable boolean isDraggable) {
        this.context = context;
        this.mapFunction = mapFunction;
        this.isDraggable = isDraggable;
    }

    public Observable<String> observe() {
        return Observable.just(isDraggable
                ? R.string.geopoint_instruction
                : R.string.geopoint_no_draggable_instruction

        ).map(context::getString);
    }
}
