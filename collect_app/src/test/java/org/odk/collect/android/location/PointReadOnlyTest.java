package org.odk.collect.android.location;

import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class PointReadOnlyTest extends PointTest {

    private LatLng initialLocation = new LatLng(Math.random(), Math.random());

    @Test
    public void shouldNotShowZoomDialogOnFirstLocation() {
        geoViewModel.onCreate();
        verify(zoomDialog, never()).show(any());
    }

    @Test
    public void shouldMarkAndZoomToInitialLocationOnStart() {
        geoViewModel.onCreate();

        verify(mapViewModel, times(1)).markLocation(initialLocation);
        verify(mapViewModel, times(1)).zoomToLocation(initialLocation);
    }

    @Test
    public void buttonsShouldBeProperlyEnabledAtStart() {
        geoViewModel.onCreate();

        int pauseVisibility = geoViewModel.locationInfoVisibility()
                .blockingFirst();

        assertEquals(pauseVisibility, View.GONE);

        int infoVisibility = geoViewModel.locationInfoVisibility()
                .blockingFirst();

        assertEquals(infoVisibility, View.GONE);

        int statusVisibility = geoViewModel.locationStatusVisibility()
                .blockingFirst();

        assertEquals(statusVisibility, View.GONE);

        boolean isAddEnabled = geoViewModel.isAddLocationEnabled()
                .blockingFirst();

        assertFalse(isAddEnabled);

        boolean isShowEnabled = geoViewModel.isShowLocationEnabled()
                .blockingFirst();

        assertTrue(isShowEnabled);

        boolean isClearEnabled = geoViewModel.isClearLocationEnabled()
                .blockingFirst();

        assertFalse(isClearEnabled);
    }

    @Test
    public void shouldNotBeAbleToModifyLocation() {
        geoViewModel.onCreate();

        Optional<LatLng> initialLatLng = geoViewModel.selectedLocation()
                .blockingFirst();

        assertTrue(initialLatLng.isPresent());
        assertEquals(initialLatLng.get(), initialLocation);

        geoViewModel.addLocation()
                .subscribe();

        Optional<LatLng> updatedLatLng = geoViewModel.selectedLocation()
                .blockingFirst();

        assertTrue(updatedLatLng.isPresent());
        assertEquals(updatedLatLng.get(), initialLocation);

        geoViewModel.clearLocation()
                .subscribe();

        Optional<LatLng> clearedLatLng = geoViewModel.selectedLocation()
                .blockingFirst();

        assertTrue(clearedLatLng.isPresent());
        assertEquals(clearedLatLng.get(), initialLocation);

        // Pause should do nothing:
        geoViewModel.pause()
                .subscribe();

        Optional<LatLng> pausedLatLng = geoViewModel.selectedLocation()
                .blockingFirst();

        assertTrue(pausedLatLng.isPresent());
        assertEquals(pausedLatLng.get(), initialLocation);
    }

    @Test
    public void showLocationShouldCallZoom() {
        geoViewModel.onCreate();
        geoViewModel.showLocation()
                .subscribe();

        verify(zoomDialog, times(1)).show(any());
    }

    @Test
    public void showLayersShouldCallShowLayers() {
        geoViewModel.onCreate();
        geoViewModel.showLayers()
                .subscribe();

        verify(mapViewModel, times(1)).showLayers();
    }

    @Test
    public void saveAnswerShouldSaveExistingAnswer() {
        geoViewModel.onCreate();

        geoViewModel.saveLocation()
                .subscribe();

        String answer = initialLocation.latitude + " " + initialLocation.longitude + " "
                + 0 + " " + 0;
        verify(saveAnswer, times(1)).save(answer);
    }

    @Nullable
    @Override
    protected LatLng initialLocation() {
        return initialLocation;
    }

    @Override
    protected boolean isDraggable() {
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }
}
