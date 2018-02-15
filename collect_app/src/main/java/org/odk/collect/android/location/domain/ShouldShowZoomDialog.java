package org.odk.collect.android.location.domain;


import android.support.annotation.NonNull;

import com.jakewharton.rxrelay2.PublishRelay;

import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.model.ZoomData;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@PerActivity
public class ShouldShowZoomDialog {

    @NonNull
    private final ShouldZoomOnFirstLocation shouldZoomOnFirstLocation;

    @NonNull
    private final CurrentPosition currentPosition;

    @NonNull
    private final SelectedLocation selectedLocation;

    private final PublishRelay<Object> showLocationRelay = PublishRelay.create();

    @Inject
    ShouldShowZoomDialog(@NonNull ShouldZoomOnFirstLocation shouldZoomOnFirstLocation,
                         @NonNull CurrentPosition currentPosition,
                         @NonNull SelectedLocation selectedLocation) {

        this.shouldZoomOnFirstLocation = shouldZoomOnFirstLocation;
        this.currentPosition = currentPosition;
        this.selectedLocation = selectedLocation;
    }

    public Observable<ZoomData> observe() {
        return Observable.merge(showLocationRelay.hide(), shouldZoomOnFirstLocation.observe())
                .flatMapSingle(__ -> Single.zip(
                        currentPosition.get(),
                        selectedLocation.get(),
                        (current, selected) -> new ZoomData(current.orNull(), selected.orNull())
                ))
                .filter(zoomData -> !zoomData.isEmpty());
    }

    public Completable show() {
        return Completable.defer(() -> {
            showLocationRelay.accept(this);
            return Completable.complete();
        });
    }
}
