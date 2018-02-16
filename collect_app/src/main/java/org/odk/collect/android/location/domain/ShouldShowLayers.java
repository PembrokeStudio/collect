package org.odk.collect.android.location.domain;


import org.odk.collect.android.injection.scopes.PerActivity;

import javax.inject.Inject;

import io.reactivex.Completable;

@PerActivity
public class ShouldShowLayers {

    private final LoadMapView loadMapView;

    @Inject
    ShouldShowLayers(LoadMapView loadMapView) {
        this.loadMapView = loadMapView;
    }

    public Completable show() {
        return null;
    }
}
