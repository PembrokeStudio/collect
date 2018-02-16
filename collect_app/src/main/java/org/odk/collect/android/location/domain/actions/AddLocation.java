package org.odk.collect.android.location.domain.actions;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.domain.state.CurrentLocation;
import org.odk.collect.android.location.domain.state.SelectedLocation;
import org.odk.collect.android.location.domain.utility.LocationConverter;
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
        if (isReadOnly) {
            return Completable.complete();
        }

        return currentLocation.observe()
                .firstOrError()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(LocationConverter::locationToLatLng)
                .flatMapCompletable(selectedLocation::select);
    }
}
