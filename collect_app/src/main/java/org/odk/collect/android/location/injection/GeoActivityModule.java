package org.odk.collect.android.location.injection;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.SupportMapFragment;

import org.odk.collect.android.injection.ActivityModule;
import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.GeoActivity;
import org.odk.collect.android.location.client.LocationClient;
import org.odk.collect.android.location.client.LocationClients;
import org.odk.collect.android.location.mapviewmodel.GoogleMapViewModel;
import org.odk.collect.android.location.mapviewmodel.MapViewModel;

import java.text.DecimalFormat;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public abstract class GeoActivityModule {

    @Binds
    abstract FragmentActivity provideFragmentActivity(GeoActivity geoActivity);

    @Provides
    @PerActivity
    static LocationClient provideLocationClient(FragmentActivity activity) {
        return LocationClients.clientForContext(activity);
    }

    @Provides
    @PerActivity
    static SupportMapFragment provideMapFragment() {
        return SupportMapFragment.newInstance();
    }

    @Provides
    @PerActivity
    static MapViewModel provideMapViewModel(GoogleMapViewModel googleMapViewModel) {
        return googleMapViewModel;
    }

    @Provides
    @PerActivity
    static DecimalFormat provideDecimalFormat() {
        return new DecimalFormat("#.##");
    }
}
