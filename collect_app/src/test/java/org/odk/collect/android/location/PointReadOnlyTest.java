package org.odk.collect.android.location;

import android.location.Location;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;

import org.junit.Test;
import org.odk.collect.android.location.model.ZoomData;

import io.reactivex.Completable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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

        verify(zoomDialog, times(1)).show(new ZoomData(null, initialLocation));

        Location location = randomLocation();
        locationRelay.accept(Optional.of(location));

        geoViewModel.showLocation()
                .subscribe();

        verify(zoomDialog, times(1)).show(new ZoomData(location, initialLocation));
    }

    @Test
    public void saveAnswerShouldSaveExistingAnswer() {
        when(saveAnswer.save(any()))
                .thenReturn(Completable.complete());

        geoViewModel.onCreate();

        geoViewModel.saveLocation()
                .subscribe();

        String answer = initialLocation.latitude + " " + initialLocation.longitude + " "
                + 0 + " " + 0;
        verify(saveAnswer, times(1))
                .save(answer);
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
