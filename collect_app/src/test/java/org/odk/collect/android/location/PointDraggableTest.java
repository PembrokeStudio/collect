package org.odk.collect.android.location;

import android.location.Location;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Optional;

import net.bytebuddy.utility.RandomString;

import org.junit.Test;
import org.odk.collect.android.R;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PointDraggableTest extends PointTest {

    @Test
    public void statusTextShouldUpdateAfterLocationReceived() {
        geoViewModel.onCreate();

        String status = RandomString.make();
        Location location = randomLocation();

        when(locationFormatter.getStringForLocation(location))
                .thenReturn(status);

        locationRelay.accept(Optional.of(location));

        String locationStatus = geoViewModel.locationStatusText()
                .blockingFirst();

        assertEquals(locationStatus, status);
    }


    @Nullable
    @Override
    protected LatLng initialLocation() {
        return null;
    }

    @Override
    protected boolean isDraggable() {
        return true;
    }

    @Override
    protected boolean isReadOnly() {
        return false;
    }
}
