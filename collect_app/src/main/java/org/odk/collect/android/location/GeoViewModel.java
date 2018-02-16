package org.odk.collect.android.location;

import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.odk.collect.android.architecture.rx.RxViewModel;
import org.odk.collect.android.location.domain.CurrentLocation;
import org.odk.collect.android.location.domain.actions.AddLocation;
import org.odk.collect.android.location.domain.actions.ClearLocation;
import org.odk.collect.android.location.domain.viewstate.InfoText;
import org.odk.collect.android.location.domain.viewstate.IsAddEnabled;
import org.odk.collect.android.location.domain.viewstate.IsClearEnabled;
import org.odk.collect.android.location.domain.viewstate.IsShowEnabled;
import org.odk.collect.android.location.domain.actions.SaveAnswer;
import org.odk.collect.android.location.domain.SelectedLocation;
import org.odk.collect.android.location.domain.ShouldMarkLocationOnStart;
import org.odk.collect.android.location.domain.ShouldShowGpsDisabledAlert;
import org.odk.collect.android.location.domain.ShouldShowZoomDialog;
import org.odk.collect.android.location.domain.ShouldZoom;
import org.odk.collect.android.location.domain.ShowGpsDisabledAlert;
import org.odk.collect.android.location.domain.viewstate.StatusText;
import org.odk.collect.android.location.domain.ZoomDialog;
import org.odk.collect.android.utilities.Rx;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;


public class GeoViewModel
        extends RxViewModel
        implements GeoViewModelType {

    @NonNull
    private final InfoText infoText;

    @NonNull
    private final StatusText statusText;

    @NonNull
    private final IsAddEnabled isAddEnabled;

    @NonNull
    private final IsShowEnabled isShowEnabled;

    @NonNull
    private final IsClearEnabled isClearEnabled;

    @NonNull
    private final AddLocation addLocation;

    @NonNull
    private final ClearLocation clearLocation;

    @NonNull
    private final SaveAnswer saveAnswer;

    @NonNull
    private final ShouldZoom shouldZoom;

    @NonNull
    private final ZoomDialog zoomDialog;

    @NonNull
    private final ShowGpsDisabledAlert showGpsDisabledAlert;

    @NonNull
    private final ShouldShowGpsDisabledAlert shouldShowGpsDisabledAlert;

    @NonNull
    private final CurrentLocation currentLocation;

    @NonNull
    private final SelectedLocation selectedLocation;


    @NonNull
    private final ShouldShowZoomDialog shouldShowZoomDialog;


    @NonNull
    private final ShouldMarkLocationOnStart shouldMarkLocationOnStartOnStart;


    @Inject
    GeoViewModel(@NonNull InfoText infoText,
                 @NonNull StatusText statusText,
                 @NonNull IsAddEnabled isAddEnabled,
                 @NonNull IsShowEnabled isShowEnabled,
                 @NonNull IsClearEnabled isClearEnabled,
                 @NonNull AddLocation addLocation,
                 @NonNull ClearLocation clearLocation,
                 @NonNull SaveAnswer saveAnswer,
                 @NonNull ShouldZoom shouldZoom,
                 @NonNull ZoomDialog zoomDialog,
                 @NonNull ShowGpsDisabledAlert showGpsDisabledAlert,
                 @NonNull ShouldShowGpsDisabledAlert shouldShowGpsDisabledAlert,
                 @NonNull CurrentLocation currentLocation,
                 @NonNull SelectedLocation selectedLocation,
                 @NonNull ShouldShowZoomDialog shouldShowZoomDialog,
                 @NonNull ShouldMarkLocationOnStart shouldMarkLocationOnStartOnStart) {

        this.infoText = infoText;
        this.statusText = statusText;
        this.isAddEnabled = isAddEnabled;
        this.isShowEnabled = isShowEnabled;
        this.isClearEnabled = isClearEnabled;
        this.addLocation = addLocation;
        this.clearLocation = clearLocation;
        this.saveAnswer = saveAnswer;
        this.shouldZoom = shouldZoom;
        this.zoomDialog = zoomDialog;
        this.showGpsDisabledAlert = showGpsDisabledAlert;
        this.shouldShowGpsDisabledAlert = shouldShowGpsDisabledAlert;
        this.currentLocation = currentLocation;
        this.selectedLocation = selectedLocation;
        this.shouldShowZoomDialog = shouldShowZoomDialog;
        this.shouldMarkLocationOnStartOnStart = shouldMarkLocationOnStartOnStart;
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

        shouldMarkLocationOnStartOnStart.observe()
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
    public Observable<Integer> locationInfoVisibility() {
        return infoText.observeVisibility();
    }

    @NonNull
    @Override
    public Observable<String> locationStatusText() {
        return statusText.observeText();
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
        return isAddEnabled.observe();
    }

    @NonNull
    @Override
    public Observable<Boolean> isShowLocationEnabled() {
        return isShowEnabled.observe();
    }

    @NonNull
    @Override
    public Observable<Boolean> isClearLocationEnabled() {
        return isClearEnabled.observe();
    }

    @NonNull
    @Override
    public Observable<LatLng> onLocationSelected() {
        return selectedLocation.onSelected();
    }

    @NonNull
    @Override
    public Observable<LatLng> onZoomToLocation() {
        return shouldZoom.observe();
    }

    @NonNull
    @Override
    public Observable<Object> onLocationCleared() {
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
    public Observable<Object> enableLocation() {
        return currentLocation.enable();
    }

    private Completable selectLocation(@NonNull LatLng latLng) {
        return selectedLocation.select(latLng);
    }
}
