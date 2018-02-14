package org.odk.collect.android.location;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;

import org.odk.collect.android.R;
import org.odk.collect.android.architecture.rx.RxViewModel;
import org.odk.collect.android.location.domain.LoadMap;
import org.odk.collect.android.location.domain.LocationFormatter;
import org.odk.collect.android.location.domain.SaveAnswer;
import org.odk.collect.android.location.domain.ShowGpsDisabledAlert;
import org.odk.collect.android.location.domain.WatchPosition;
import org.odk.collect.android.location.domain.ZoomDialog;
import org.odk.collect.android.location.mapviewmodel.MapViewModel;
import org.odk.collect.android.location.model.MapFunction;
import org.odk.collect.android.location.model.ZoomData;
import org.odk.collect.android.utilities.Rx;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;


public class GeoViewModel
        extends RxViewModel
        implements GeoViewModelType {

    // Inputs:
    @NonNull
    private final Context context;

    @NonNull
    private final WatchPosition watchPosition;

    @NonNull
    private final LoadMap loadMap;

    @NonNull
    private final ZoomDialog zoomDialog;

    @NonNull
    private final ShowGpsDisabledAlert showGpsDisabledAlert;

    @NonNull
    private final LocationFormatter locationFormatter;

    @NonNull
    private final SaveAnswer saveAnswer;

    // Variables:

    @NonNull
    private final BehaviorRelay<Optional<LatLng>> selectedLocationRelay =
            BehaviorRelay.createDefault(Optional.absent());

    @NonNull
    private final Observable<Optional<LatLng>> selectedLocation =
            selectedLocationRelay.hide();

    @NonNull
    private final BehaviorRelay<Optional<Location>> currentPositionRelay =
            BehaviorRelay.createDefault(Optional.absent());

    @NonNull
    private final Observable<Optional<Location>> currentPosition =
            currentPositionRelay.hide();

    @NonNull
    private final PublishRelay<Object> showLocation = PublishRelay.create();

    @NonNull
    private final PublishRelay<Object> clearLocation = PublishRelay.create();

    @NonNull
    private final PublishRelay<LatLng> shouldMarkLocationRelay = PublishRelay.create();

    @NonNull
    private final Observable<LatLng> shouldMarkLocation = shouldMarkLocationRelay.hide();

    @NonNull
    private final Observable<Object> onClearLocation = clearLocation.hide();

    @NonNull
    private final BehaviorRelay<Boolean> hasBeenCleared =
            BehaviorRelay.createDefault(false);

    @NonNull
    private final BehaviorRelay<MapViewModel> mapViewModelRelay =
            BehaviorRelay.create();

    @NonNull
    private final Observable<MapViewModel> observeMapViewModel =
            mapViewModelRelay.hide();

    @NonNull
    private final MapFunction mapFunction;

    @Nullable
    private final LatLng initialLocation;

    private final boolean isDraggable;
    private final boolean isReadOnly;
    private final boolean hasInitialLocation;

    @Inject
    GeoViewModel(@NonNull Context context,
                 @NonNull WatchPosition watchPosition,
                 @NonNull LoadMap loadMap,
                 @NonNull LocationFormatter locationFormatter,
                 @NonNull ZoomDialog zoomDialog,
                 @NonNull ShowGpsDisabledAlert showGpsDisabledAlert,
                 @NonNull SaveAnswer saveAnswer,
                 @NonNull MapFunction mapFunction,
                 @Named("isDraggable") boolean isDraggable,
                 @Named("isReadOnly") boolean isReadOnly,
                 @Nullable LatLng initialLocation) {

        this.context = context;
        this.watchPosition = watchPosition;
        this.loadMap = loadMap;
        this.zoomDialog = zoomDialog;
        this.showGpsDisabledAlert = showGpsDisabledAlert;
        this.saveAnswer = saveAnswer;
        this.mapFunction = mapFunction;

        this.initialLocation = initialLocation;
        this.isDraggable = isDraggable;
        this.isReadOnly = isReadOnly;
        this.hasInitialLocation = initialLocation != null;

        this.locationFormatter = locationFormatter;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        watchPosition.observeLocation()
                .filter(Optional::isPresent)
                .compose(bindToLifecycle())
                .subscribe(currentPositionRelay, Timber::e);

        loadMap.load()
                .compose(bindToLifecycle())
                .subscribe(mapViewModelRelay, Timber::e);

        observeMapViewModel
                .compose(bindToLifecycle())
                .subscribe(this::bindMapViewModel, Timber::e);

        currentPosition
                .map(Optional::isPresent)
                .filter(Rx::isTrue)
                .distinctUntilChanged()
                .flatMapSingle(__ -> currentPosition.firstOrError())
                .filter(__ -> !isDraggable && !hasInitialLocation && !isReadOnly)
                .map(Optional::get)
                .map(this::locationToLatLng)
                .subscribe(shouldMarkLocationRelay, Timber::e);

        Observable<Object> shouldZoomOnFirstLocation = hasSelectedLocation()
                .filter(Rx::isTrue)
                .distinctUntilChanged()
                .map(Rx::toEvent)
                .map(__ -> hasInitialLocation || isDraggable)
                .filter(Rx::isFalse)
                .map(Rx::toEvent);

        Observable.merge(showLocation.hide(), shouldZoomOnFirstLocation)
                .flatMapSingle(__ -> Single.zip(
                        currentPosition.firstOrError(),
                        selectedLocation.firstOrError(),
                        (current, selected) -> new ZoomData(current.orNull(), selected.orNull())
                ))
                .filter(zoomData -> !zoomData.isEmpty())
                .compose(bindToLifecycle())
                .subscribe(zoomDialog::show, Timber::e);

        watchPosition.observeAvailability()
                .filter(isAvailable -> !isAvailable)
                .compose(bindToLifecycle())
                .subscribe(showGpsDisabledAlert::show, Timber::e);
    }

    private void bindMapViewModel(@NonNull MapViewModel mapViewModel) {
        mapViewModel.observeMarkedLocation()
                .compose(bindToLifecycle())
                .subscribe(markedLocation -> {
                    if (markedLocation.isPresent()) {
                        selectLocation(markedLocation.get());
                    } else {
                        clearSelectedLocation();
                    }

                }, Timber::e);

        Observable.merge(
                initialLocation != null
                        ? Observable.just(initialLocation)
                        : Observable.empty()
                , zoomDialog.zoomToLocation())
                .flatMapCompletable(mapViewModel::zoomToLocation)
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);

        shouldMarkLocation
                .flatMapCompletable(mapViewModel::markLocation)
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);

        onClearLocation
                .flatMapCompletable(__ -> mapViewModel.clearMarkedLocation())
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);

        if (initialLocation != null) {
            shouldMarkLocationRelay.accept(initialLocation);
        }
    }

    // UI state:
    @NonNull
    @Override
    public Observable<String> locationInfoText() {
        return Observable.just(isDraggable ? R.string.geopoint_instruction : R.string.geopoint_no_draggable_instruction)
                .map(context::getString);
    }

    @NonNull
    @Override
    public Observable<String> locationStatusText() {
        return watchPosition.observeLocation()
                .map(currentLocation -> currentLocation.isPresent()
                        ? locationFormatter.getStringForLocation(currentLocation.get())
                        : context.getString(R.string.please_wait_long));
    }

    @NonNull
    @Override
    public Observable<Integer> locationInfoVisibility() {
        return hasBeenCleared.map(
                wasCleared ->
                        isReadOnly || (hasInitialLocation && !wasCleared)
        ).map(shouldHide -> shouldHide ? View.GONE : View.VISIBLE);
    }

    @NonNull
    @Override
    public Observable<Integer> locationStatusVisibility() {
        return hasBeenCleared.map(wasCleared ->
                isReadOnly || (hasInitialLocation && !wasCleared)

        ).map(shouldHide -> shouldHide ? View.GONE : View.VISIBLE);
    }

    @NonNull
    @Override
    public Observable<Integer> pauseButtonVisibility() {
        return Observable.just(View.GONE);
    }

    @NonNull
    @Override
    public Observable<Boolean> isAddLocationEnabled() {
        return currentPosition.map(Optional::isPresent)
                .map(hasCurrentPosition ->
                        !isReadOnly && hasCurrentPosition
                );
    }

    @NonNull
    @Override
    public Observable<Boolean> isShowLocationEnabled() {
        return Observable.combineLatest(
                currentPosition.map(Optional::isPresent),
                hasSelectedLocation(),
                Rx::or
        );
    }

    @NonNull
    @Override
    public Observable<Boolean> isClearLocationEnabled() {

        return hasSelectedLocation().map(hasSelectedLocation ->
                !isReadOnly && (hasInitialLocation || hasSelectedLocation)
        );
    }

    @NonNull
    @Override
    public Completable addLocation() {
        return Completable.defer(() -> {
            if (isReadOnly) {
                return Completable.complete();
            }

            return watchPosition.currentLocation()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(this::locationToLatLng)
                    .doOnSuccess(shouldMarkLocationRelay)
                    .flatMapCompletable(__ -> Completable.complete());
        });
    }

    @NonNull
    @Override
    public Completable pause() {
        return Completable.complete();
    }

    @NonNull
    @Override
    public Completable showLocation() {
        return Completable.defer(() -> {
            showLocation.accept(this);
            return Completable.complete();
        });
    }

    @NonNull
    @Override
    public Completable showLayers() {
        return observeMapViewModel.firstOrError()
                .flatMapCompletable(MapViewModel::showLayers);
    }

    @NonNull
    @Override
    public Completable clearLocation() {
        return Completable.defer(() -> {

            if (!isReadOnly) {
                clearLocation.accept(this);
            }

            return Completable.complete();
        });
    }

    @NonNull
    @Override
    public Completable saveLocation() {
        return selectedLocation.flatMapCompletable(latLngOptional -> {
            String answer = "";
            if (latLngOptional.isPresent()) {
                LatLng latLng = latLngOptional.get();
                answer = latLng.latitude + " " + latLng.longitude + " "
                        + 0 + " " + 0;
            }

            return saveAnswer.save(answer);
        });
    }

    @NonNull
    @Override
    public Observable<Object> watchLocation() {
        return Observable.never()
                .doOnSubscribe(__ -> watchPosition.startWatching())
                .doOnDispose(watchPosition::stopWatching);
    }

    Observable<Optional<LatLng>> selectedLocation() {
        return selectedLocation;
    }

    Observable<Boolean> hasSelectedLocation() {
        return selectedLocation()
                .map(Optional::isPresent)
                .distinctUntilChanged();
    }

    private void selectLocation(@NonNull LatLng latLng) {
        if (isReadOnly) {
            if (initialLocation != null && initialLocation.equals(latLng)) {
                selectedLocationRelay.accept(Optional.of(latLng));
            }

            return;
        }

        selectedLocationRelay.accept(Optional.of(latLng));
    }

    private void clearSelectedLocation() {
        selectedLocationRelay.accept(Optional.absent());
        hasBeenCleared.accept(true);
    }

    @NonNull
    private LatLng locationToLatLng(@NonNull Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
