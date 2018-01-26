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

import org.odk.collect.android.spatial.MapHelper;
import org.odk.collect.android.utilities.Rx;

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
    private final BehaviorRelay<Optional<Marker>> markerRelay =
            BehaviorRelay.createDefault(Optional.absent());

    // Outputs:
    @NonNull
    private final Observable<Optional<Marker>> observeMarker =
            markerRelay.hide();

    GoogleMapViewModel(@NonNull Context context,
                       @NonNull GoogleMap googleMap,
                       boolean isDraggable) {
        this.context = context;
        this.googleMap = googleMap;
        this.isDraggable = isDraggable;
    }

    @NonNull
    @Override
    public Observable<LatLng> observeMarkedLocation() {
        return markerRelay
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Marker::getPosition);
    }

    @NonNull
    @Override
    public Observable<Object> observeClearedLocation() {
        return markerRelay
                .map(Optional::isPresent)
                .filter(Rx::isFalse)
                .skip(1)
                .map(Rx::toEvent);
    }

    @NonNull
    @Override
    public Completable markLocation(@NonNull LatLng latLng) {
        return observeMarker.firstOrError().map(markerOptional -> {
            Marker marker;
            if (markerOptional.isPresent()) {
                marker = markerOptional.get();
                marker.setPosition(latLng);

            } else {
                MarkerOptions options = new MarkerOptions()
                        .position(latLng);
                marker = googleMap.addMarker(options);
            }

            marker.setDraggable(isDraggable);
            return Optional.of(marker);

        }).flatMapCompletable(markerOptional -> {
            markerRelay.accept(markerOptional);
            return Completable.complete();
        });
    }

    @NonNull
    @Override
    public Completable clearMarkedLocation() {
        return observeMarker.firstOrError()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .doOnSuccess(Marker::remove)
                .map(__ -> Optional.<Marker>absent())
                .flatMapCompletable(markerOptional -> {
                    markerRelay.accept(markerOptional);
                    return Completable.complete();
                });
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
        markLocation(latLng);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        markerRelay.accept(Optional.of(marker));
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
