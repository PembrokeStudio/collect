package org.odk.collect.android.location.domain.actions;

import android.support.annotation.NonNull;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.domain.CurrentLocation;
import org.odk.collect.android.location.domain.SelectedLocation;
import org.odk.collect.android.location.Utility;
import org.odk.collect.android.location.injection.Qualifiers.IsReadOnly;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * @author James Knight
 */

@PerActivity
public class AddLocation {

    @NonNull
    private final CurrentLocation currentLocation;

    @NonNull
    private final SelectedLocation selectedLocation;

    private final boolean isReadOnly;

    @Inject
    AddLocation(@NonNull CurrentLocation currentLocation,
                @NonNull SelectedLocation selectedLocation,
                @IsReadOnly boolean isReadOnly) {

        this.currentLocation = currentLocation;
        this.selectedLocation = selectedLocation;
        this.isReadOnly = isReadOnly;
    }

    public Completable add() {
        return !isReadOnly
                ? createAdd()
                : Completable.complete();
    }

    private Completable createAdd() {
        return currentLocation.observe()
                .map(Utility::locationToLatLng)
                .doOnNext(selectedLocation::select)
                .flatMapCompletable(__ -> Completable.complete());
    }
}
