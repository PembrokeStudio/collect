package org.odk.collect.android.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;

import net.bytebuddy.utility.RandomString;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.odk.collect.android.R;
import org.odk.collect.android.location.domain.LoadMap;
import org.odk.collect.android.location.domain.LocationFormatter;
import org.odk.collect.android.location.domain.SaveAnswer;
import org.odk.collect.android.location.domain.ShowGpsDisabledAlert;
import org.odk.collect.android.location.domain.WatchPosition;
import org.odk.collect.android.location.domain.ZoomDialog;
import org.odk.collect.android.location.mapviewmodel.MapViewModel;
import org.odk.collect.android.location.model.MapFunction;
import org.odk.collect.android.location.model.ZoomData;
import org.odk.collect.android.location.viewmodel.MockMapViewModel;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public abstract class GeoViewModelTest {

    @NonNull
    protected abstract MapFunction mapFunction();

    @Nullable
    protected abstract LatLng initialLocation();

    protected abstract boolean isDraggable();

    protected abstract boolean isReadOnly();

    @Mock
    protected Context context;

    @Mock
    protected WatchPosition watchPosition;

    @Mock
    private LoadMap loadMap;

    @Mock
    protected LocationFormatter locationFormatter;

    @Mock
    protected ZoomDialog zoomDialog;

    @Mock
    protected ShowGpsDisabledAlert showGpsDisabledAlert;

    @Mock
    protected SaveAnswer saveAnswer;

    protected MapViewModel mapViewModel = spy(new MockMapViewModel());

    protected BehaviorRelay<Optional<Location>> locationRelay =
            BehaviorRelay.createDefault(Optional.absent());

    protected BehaviorRelay<Boolean> availabilityRelay =
            BehaviorRelay.create();

    protected PublishRelay<LatLng> zoomRelay = PublishRelay.create();

    protected GeoViewModel geoViewModel;

    @Before
    public void setupViewModel() {
        geoViewModel = spy(new GeoViewModel(
                context,
                watchPosition,
                loadMap,
                locationFormatter,
                zoomDialog,
                showGpsDisabledAlert,
                saveAnswer,
                mapFunction(),
                isDraggable(),
                isReadOnly(),
                initialLocation()
        ));

        when(watchPosition.observeLocation())
                .thenReturn(locationRelay.hide());

        when(loadMap.load())
                .thenReturn(Single.just(mapViewModel));

        when(watchPosition.observeAvailability())
                .thenReturn(availabilityRelay);

        when(zoomDialog.zoomToLocation())
                .thenReturn(zoomRelay.hide());
    }

    private void expectedInititalInfo() {
        switch (mapFunction()) {
            case POINT:
                if (isDraggable()) {

                }
                break;
            case SHAPE:
                break;
            case TRACE:
                break;
        }
    }

    @Test
    public void initialStateShouldBeCorrect() {
        geoViewModel.onCreate();

        String info = RandomString.make();
        when(context.getString(R.string.geopoint_instruction))
                .thenReturn(info);

        String locationInfo = geoViewModel.locationInfoText()
                .blockingFirst();

        assertEquals(locationInfo, info);

        String status = RandomString.make();
        when(context.getString(R.string.please_wait_long))
                .thenReturn(status);

        String locationStatus = geoViewModel.locationStatusText()
                .blockingFirst();

        assertEquals(locationStatus, status);

        int pauseVisibility = geoViewModel.pauseButtonVisibility()
                .blockingFirst();

        int expectedPauseVisibility = mapFunction() != MapFunction.POINT
                ? View.VISIBLE
                : View.GONE;
        assertEquals(pauseVisibility, expectedPauseVisibility);

        int infoVisibility = geoViewModel.locationInfoVisibility()
                .blockingFirst();

        int expectedTextVisibility = isReadOnly() || initialLocation() != null
                ? View.GONE
                : View.VISIBLE;

        assertEquals(infoVisibility, expectedTextVisibility);

        int statusVisibility = geoViewModel.locationStatusVisibility()
                .blockingFirst();

        assertEquals(statusVisibility, expectedTextVisibility);

        boolean isAddEnabled = geoViewModel.isAddLocationEnabled()
                .blockingFirst();

        assertFalse(isAddEnabled);

        boolean isShowEnabled = geoViewModel.isShowLocationEnabled()
                .blockingFirst();

        boolean expectedShowEnabled = initialLocation() != null;
        assertEquals(isShowEnabled, expectedShowEnabled);

        boolean isClearEnabled = geoViewModel.isClearLocationEnabled()
                .blockingFirst();

        boolean expectedClearEnabled = initialLocation() != null
                && !isReadOnly();
        assertEquals(isClearEnabled, expectedClearEnabled);


    }

    @Test
    public void shouldShowGpsDisabledAlertWhenLocationIsUnavailable() {
        geoViewModel.onCreate();

        verify(showGpsDisabledAlert, never())
                .show(any());

        availabilityRelay.accept(true);
        verify(showGpsDisabledAlert, never())
                .show(any());

        availabilityRelay.accept(false);
        verify(showGpsDisabledAlert, times(1))
                .show(any());
    }

    @Test
    public void shouldCallWatchLocation() {
        geoViewModel.onCreate();

        verify(watchPosition, never()).startWatching();

        Disposable disposable = geoViewModel.watchLocation().subscribe();
        verify(watchPosition, times(1)).startWatching();
        verify(watchPosition, never()).stopWatching();

        disposable.dispose();
        verify(watchPosition, times(1)).startWatching();
        verify(watchPosition, times(1)).stopWatching();
    }

    @Test
    public void showLayersShouldCallShowLayers() {
        geoViewModel.onCreate();
        geoViewModel.showLayers()
                .subscribe();

        verify(mapViewModel, times(1)).showLayers();
    }

    @Test
    public void showLocationShouldCallZoomIfWeHaveCurrentPosition() {
        geoViewModel.onCreate();

        Location location = randomLocation();
        locationRelay.accept(Optional.of(location));

        geoViewModel.showLocation()
                .subscribe();

        verify(zoomDialog, times(1)).show(new ZoomData(location, initialLocation()));
    }

    @NonNull
    protected LatLng randomLatLng() {
        return new LatLng(Math.random(), Math.random());
    }

    @NonNull
    protected Location randomLocation() {
        Location location = mock(Location.class);

        when(location.getLatitude()).thenReturn(Math.random());
        when(location.getLongitude()).thenReturn(Math.random());

        return location;
    }
}
