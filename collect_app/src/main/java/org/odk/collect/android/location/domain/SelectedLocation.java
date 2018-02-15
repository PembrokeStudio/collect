package org.odk.collect.android.location.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.injection.Qualifiers.InitialLocation;
import org.odk.collect.android.location.injection.Qualifiers.IsReadOnly;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@PerActivity
public class SelectedLocation {

    private final boolean isReadOnly;

    @NonNull
    private final BehaviorRelay<Optional<LatLng>> selectedLocationRelay;

    @NonNull
    private final Observable<Optional<LatLng>> selectedLocation;

    @NonNull
    private final BehaviorRelay<Boolean> hasBeenClearedRelay =
            BehaviorRelay.createDefault(false);

    @NonNull
    private final Observable<Boolean> hasBeenCleared =
            hasBeenClearedRelay.hide();

    @NonNull
    private final PublishRelay<Object> clearedRelay =
            PublishRelay.create();

    @NonNull
    private final Observable<Object> cleared = clearedRelay.hide();


    @Inject
    public SelectedLocation(@IsReadOnly boolean isReadOnly,
                            @InitialLocation @Nullable LatLng initalLocation) {
        this.isReadOnly = isReadOnly;

        this.selectedLocationRelay = BehaviorRelay.createDefault(Optional.fromNullable(initalLocation));
        this.selectedLocation = selectedLocationRelay.hide();
    }

    public Completable select(@Nullable LatLng latLng) {
        return Completable.defer(() -> {
            if (!isReadOnly) {
                selectedLocationRelay.accept(Optional.fromNullable(latLng));
            }

            return Completable.complete();
        });
    }

    public Completable clear() {
        return Completable.defer(() -> {
            if (!isReadOnly) {
                selectedLocationRelay.accept(Optional.absent());

                clearedRelay.accept(this);
                hasBeenClearedRelay.accept(true);
            }

            return Completable.complete();
        });
    }

    public Observable<Optional<LatLng>> observe() {
        return selectedLocation;
    }

    public Observable<Object> cleared() {
        return cleared;
    }

    public Single<Optional<LatLng>> get() {
        return selectedLocation.firstOrError();
    }

    public Observable<Boolean> observePresence() {
        return selectedLocation.map(Optional::isPresent)
                .distinctUntilChanged();
    }

    public Observable<Boolean> hasBeenCleared() {
        return hasBeenCleared;
    }
}
