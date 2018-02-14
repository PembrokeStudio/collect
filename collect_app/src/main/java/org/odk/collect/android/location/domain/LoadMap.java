package org.odk.collect.android.location.domain;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.SupportMapFragment;

import org.odk.collect.android.R;
import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.GeoActivity;
import org.odk.collect.android.location.mapviewmodel.GoogleMapViewModel;
import org.odk.collect.android.location.mapviewmodel.MapViewModel;
import org.odk.collect.android.spatial.MapHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;

@PerActivity
public class LoadMap {

    @NonNull
    private final GeoActivity activity;

    @NonNull
    private final FragmentManager fragmentManager;

    @NonNull
    private final SupportMapFragment mapFragment;

    private final boolean isDraggable;

    @Inject
    LoadMap(@NonNull GeoActivity activity,
            @NonNull FragmentManager fragmentManager,
            @NonNull SupportMapFragment mapFragment,
            @Named("isDraggable") boolean isDraggable) {

        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.mapFragment = mapFragment;
        this.isDraggable = isDraggable;
    }

    public Single<MapViewModel> load() {
        return Single.create(emitter -> {
            mapFragment.getMapAsync(googleMap -> {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);

                MapHelper helper = new MapHelper(activity, googleMap);
                helper.setBasemap();

                emitter.onSuccess(new GoogleMapViewModel(activity, googleMap, isDraggable));
            });

            fragmentManager.beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        });
    }
}
