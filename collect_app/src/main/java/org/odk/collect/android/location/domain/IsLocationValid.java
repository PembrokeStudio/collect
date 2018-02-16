package org.odk.collect.android.location.domain;

import android.location.Location;
import android.support.annotation.NonNull;

import org.odk.collect.android.injection.scopes.PerActivity;

import java.util.Date;

import javax.inject.Inject;

@PerActivity
public class IsLocationValid {

    private final double validIfWithinMillis;

    @Inject
    IsLocationValid(double validIfWithinMillis) {
        this.validIfWithinMillis = validIfWithinMillis;
    }

    public boolean isValid(@NonNull Location location) {
        long millis = new Date().getTime() - location.getTime();
        return millis <= validIfWithinMillis;
    }
}
