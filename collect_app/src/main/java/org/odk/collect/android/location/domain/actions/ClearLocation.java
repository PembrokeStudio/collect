package org.odk.collect.android.location.domain.actions;


import android.support.annotation.NonNull;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.domain.SelectedLocation;
import org.odk.collect.android.location.injection.Qualifiers.IsReadOnly;

import javax.inject.Inject;

import io.reactivex.Completable;

@PerActivity
public class ClearLocation {

    @NonNull
    private final SelectedLocation selectedLocation;
    private final boolean isReadOnly;

    @Inject
    ClearLocation(@NonNull SelectedLocation selectedLocation,
                  @IsReadOnly boolean isReadOnly) {
        this.selectedLocation = selectedLocation;
        this.isReadOnly = isReadOnly;
    }

    @NonNull
    public Completable clear() {
        return !isReadOnly
                ? selectedLocation.clear()
                : Completable.complete();
    }
}
