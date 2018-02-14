package org.odk.collect.android.location.mapviewmodel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Optional;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;

import org.odk.collect.android.spatial.MapHelper;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class GoogleMapViewModel implements MapViewModel, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener {

    @NonNull
    private final Context context;

    @NonNull
    private final GoogleMap googleMap;

    private final boolean isDraggable;

    // Internal state:
    @NonNull
    private final PublishRelay<Optional<LatLng>> markLocationRelay =
            PublishRelay.create();

    @NonNull
    private final Observable<Optional<LatLng>> markLocation =
            markLocationRelay.hide();

    @NonNull
    private final BehaviorRelay<Optional<Marker>> markerRelay =
            BehaviorRelay.createDefault(Optional.absent());

    @NonNull
    private final Observable<Optional<Marker>> observeMarker =
            markerRelay.hide();

    public GoogleMapViewModel(@NonNull Context context,
                              @NonNull GoogleMap googleMap,
                              boolean isDraggable) {
        this.context = context;

        this.googleMap = googleMap;
        this.googleMap.setOnMarkerDragListener(this);
        this.googleMap.setOnMapLongClickListener(this);

        this.isDraggable = isDraggable;
    }

    @NonNull
    @Override
    public Observable<Optional<LatLng>> observeMarkedLocation() {
        return markLocation.withLatestFrom(observeMarker, (latLngOptional, markerOptional) -> {

            if (latLngOptional.isPresent()) {
                // Adding/updating location:
                Marker marker;
                if (markerOptional.isPresent()) {
                    marker = markerOptional.get();
                    marker.setPosition(latLngOptional.get());

                } else {
                    MarkerOptions options = new MarkerOptions()
                            .position(latLngOptional.get());
                    marker = googleMap.addMarker(options);
                }

                marker.setDraggable(isDraggable);
                markerRelay.accept(Optional.of(marker));

            } else {
                // Removing location:
                if (markerOptional.isPresent()) {
                    Marker marker = markerOptional.get();
                    marker.remove();
                }

                markerRelay.accept(Optional.absent());
            }

            return latLngOptional;
        });
    }


    @NonNull
    @Override
    public Completable markLocation(@NonNull LatLng latLng) {
        return Completable.defer(() -> {
            mark(latLng);
            return Completable.complete();
        });
    }

    @NonNull
    @Override
    public Completable clearMarkedLocation() {
        return Completable.defer(() -> {
            clear();
            return Completable.complete();
        });
    }

    private void mark(@NonNull LatLng latLng) {
        markLocationRelay.accept(Optional.of(latLng));
    }

    private void clear() {
        markLocationRelay.accept(Optional.absent());
    }

    @NonNull
    @Override
    public Completable zoomToLocation(@NonNull LatLng latLng) {
        return Completable.defer(() -> {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
            googleMap.animateCamera(update);

            return Completable.complete();
        });
    }

    @NonNull
    @Override
    public Completable showLayers() {
        return Completable.defer(() -> {
            new MapHelper(context, googleMap)
                    .showLayersDialog(context);

            return Completable.complete();
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (isDraggable) {
            mark(latLng);
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mark(marker.getPosition());
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        // Do nothing.
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // Do nothing.
    }
}
