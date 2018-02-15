package org.odk.collect.android.location;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;

import org.odk.collect.android.architecture.rx.RxViewModel;
import org.odk.collect.android.location.domain.AddLocation;
import org.odk.collect.android.location.domain.ClearLocation;
import org.odk.collect.android.location.domain.CurrentPosition;
import org.odk.collect.android.location.domain.InfoText;
import org.odk.collect.android.location.domain.SaveAnswer;
import org.odk.collect.android.location.domain.SelectedLocation;
import org.odk.collect.android.location.domain.ShouldMarkLocation;
import org.odk.collect.android.location.domain.ShouldShowGpsDisabledAlert;
import org.odk.collect.android.location.domain.ShouldShowZoomDialog;
import org.odk.collect.android.location.domain.ShouldZoom;
import org.odk.collect.android.location.domain.ShowGpsDisabledAlert;
import org.odk.collect.android.location.domain.StatusText;
import org.odk.collect.android.location.domain.WatchPosition;
import org.odk.collect.android.location.domain.ZoomDialog;
import org.odk.collect.android.location.injection.Qualifiers.InitialLocation;
import org.odk.collect.android.location.injection.Qualifiers.IsReadOnly;
import org.odk.collect.android.utilities.Rx;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;


public class GeoViewModel
        extends RxViewModel
        implements GeoViewModelType {

    @NonNull
    private final WatchPosition watchPosition;

    @NonNull
    private final SelectedLocation selectedLocation;

    @NonNull
    private final CurrentPosition currentPosition;

    @NonNull
    private final ZoomDialog zoomDialog;

    @NonNull
    private final ShowGpsDisabledAlert showGpsDisabledAlert;

    @NonNull
    private final ShouldShowGpsDisabledAlert shouldShowGpsDisabledAlert;

    @NonNull
    private final ShouldShowZoomDialog shouldShowZoomDialog;

    @NonNull
    private final ShouldZoom shouldZoom;

    @NonNull
    private final ClearLocation clearLocation;

    @NonNull
    private final SaveAnswer saveAnswer;

    @NonNull
    private final InfoText infoText;

    @NonNull
    private final StatusText statusText;

    @NonNull
    private final ShouldMarkLocation shouldMarkLocation;

    @NonNull
    private final AddLocation addLocation;

    // Variables:

    @Nullable
    private final LatLng initialLocation;

    private final boolean isReadOnly;
    private final boolean hasInitialLocation;

    @Inject
    GeoViewModel(@NonNull WatchPosition watchPosition,
                 @NonNull SelectedLocation selectedLocation,
                 @NonNull CurrentPosition currentPosition,
                 @NonNull ZoomDialog zoomDialog,
                 @NonNull ShowGpsDisabledAlert showGpsDisabledAlert,
                 @NonNull ShouldShowGpsDisabledAlert shouldShowGpsDisabledAlert,
                 @NonNull ShouldShowZoomDialog shouldShowZoomDialog,
                 @NonNull ShouldZoom shouldZoom, @NonNull ClearLocation clearLocation,
                 @NonNull SaveAnswer saveAnswer,
                 @NonNull InfoText infoText,
                 @NonNull StatusText statusText,
                 @NonNull ShouldMarkLocation shouldMarkLocation,
                 @NonNull AddLocation addLocation,
                 @IsReadOnly boolean isReadOnly,
                 @InitialLocation @Nullable LatLng initialLocation) {

        this.watchPosition = watchPosition;
        this.currentPosition = currentPosition;
        this.selectedLocation = selectedLocation;
        this.zoomDialog = zoomDialog;
        this.showGpsDisabledAlert = showGpsDisabledAlert;
        this.shouldShowGpsDisabledAlert = shouldShowGpsDisabledAlert;
        this.shouldShowZoomDialog = shouldShowZoomDialog;
        this.shouldZoom = shouldZoom;
        this.clearLocation = clearLocation;
        this.saveAnswer = saveAnswer;
        this.infoText = infoText;
        this.statusText = statusText;
        this.shouldMarkLocation = shouldMarkLocation;
        this.addLocation = addLocation;

        this.initialLocation = initialLocation;
        this.isReadOnly = isReadOnly;
        this.hasInitialLocation = initialLocation != null;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        shouldShowZoomDialog.observe()
                .compose(bindToLifecycle())
                .subscribe(zoomDialog::show, Timber::e);

        shouldShowGpsDisabledAlert.observe()
                .compose(bindToLifecycle())
                .subscribe(showGpsDisabledAlert::show, Timber::e);

        shouldMarkLocation.observe()
                .compose(bindToLifecycle())
                .flatMapCompletable(this::selectLocation)
                .subscribe(Rx::noop, Timber::e);
    }

    // UI state:
    @NonNull
    @Override
    public Observable<String> locationInfoText() {
        return infoText.observeText();
    }

    @NonNull
    @Override
    public Observable<String> locationStatusText() {
        return statusText.observeText();
    }

    @NonNull
    @Override
    public Observable<Integer> locationInfoVisibility() {
        return infoText.observeVisibility();
    }

    @NonNull
    @Override
    public Observable<Integer> locationStatusVisibility() {
        return statusText.observeVisibility();
    }

    @NonNull
    @Override
    public Observable<Integer> pauseButtonVisibility() {
        return Observable.just(View.GONE);
    }

    @NonNull
    @Override
    public Observable<Boolean> isAddLocationEnabled() {
        return currentPosition.observePresence()
                .map(hasCurrentPosition -> !isReadOnly && hasCurrentPosition);
    }

    @NonNull
    @Override
    public Observable<Boolean> isShowLocationEnabled() {
        return Observable.combineLatest(
                currentPosition.observePresence(),
                selectedLocation.observePresence(),
                Rx::or
        );
    }

    @NonNull
    @Override
    public Observable<Boolean> isClearLocationEnabled() {
        return selectedLocation.observePresence()
                .map(hasSelectedLocation ->
                        !isReadOnly && (hasInitialLocation || hasSelectedLocation)
                );
    }

    @NonNull
    @Override
    public Observable<LatLng> locationSelected() {
        return selectedLocation.observe()
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @NonNull
    @Override
    public Observable<LatLng> shouldZoomToLocation() {
        return shouldZoom.observe();
    }

    @NonNull
    @Override
    public Observable<Object> locationCleared() {
        return selectedLocation.cleared();
    }

    @NonNull
    @Override
    public Completable addLocation() {
        return addLocation.add();
    }

    @NonNull
    @Override
    public Completable pause() {
        return Completable.complete();
    }

    @NonNull
    @Override
    public Completable showLocation() {
        return shouldShowZoomDialog.show();
    }

    @NonNull
    @Override
    public Completable clearLocation() {
        return clearLocation.clear();
    }

    @NonNull
    @Override
    public Completable saveLocation() {
        return saveAnswer.save();
    }

    @NonNull
    @Override
    public Completable mapLongPressed(@NonNull LatLng latLng) {
        return selectedLocation.select(latLng);
    }

    @NonNull
    @Override
    public Completable markerMoved(@NonNull LatLng latLng) {
        return selectedLocation.select(latLng);
    }

    @NonNull
    @Override
    public Observable<Object> watchLocation() {
        return Observable.never()
                .doOnSubscribe(__ -> watchPosition.startWatching())
                .doOnDispose(watchPosition::stopWatching);
    }

    private Completable selectLocation(@NonNull LatLng latLng) {
        return selectedLocation.select(latLng);
    }
}
