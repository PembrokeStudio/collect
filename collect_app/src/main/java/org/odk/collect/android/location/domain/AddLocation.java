package org.odk.collect.android.location.domain;

import android.support.annotation.NonNull;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.injection.Qualifiers.IsReadOnly;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * @author James Knight
 */

@PerActivity
public class AddLocation {

    @NonNull
    private final CurrentPosition currentPosition;

    @NonNull
    private final ShouldMarkLocation shouldMarkLocation;

    private final boolean isReadOnly;

    @Inject
    public AddLocation(@NonNull CurrentPosition currentPosition,
                       @NonNull ShouldMarkLocation shouldMarkLocation,
                       @IsReadOnly boolean isReadOnly) {

        this.currentPosition = currentPosition;
        this.shouldMarkLocation = shouldMarkLocation;
        this.isReadOnly = isReadOnly;
    }

    public Completable add() {
        return !isReadOnly
                ? createAdd()
                : Completable.complete();
    }

    private Completable createAdd() {
        return currentPosition.observe().map(Utility::locationToLatLng)
                .doOnNext(shouldMarkLocation::shouldMark)
                .flatMapCompletable(__ -> Completable.complete());
    }
}
