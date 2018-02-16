package org.odk.collect.android.location.domain.actions;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.database.ActivityLogger;
import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.domain.state.SelectedLocation;

import javax.inject.Inject;

import io.reactivex.Completable;

import static android.app.Activity.RESULT_OK;

@PerActivity
public class SaveAnswer {

    @NonNull
    private final Activity activity;

    @NonNull
    private final SelectedLocation selectedLocation;

    @NonNull
    private final ActivityLogger activityLogger;

    @Inject
    SaveAnswer(@NonNull Activity activity,
               @NonNull SelectedLocation selectedLocation,
               @NonNull ActivityLogger activityLogger) {
        this.activity = activity;
        this.selectedLocation = selectedLocation;
        this.activityLogger = activityLogger;
    }


    public Completable save() {
        return selectedLocation.get()
                .flatMapCompletable(latLngOptional -> {

                    String answer = "";
                    if (latLngOptional.isPresent()) {
                        LatLng latLng = latLngOptional.get();
                        answer = latLng.latitude + " " + latLng.longitude + " "
                                + 0 + " " + 0;
                    }

                    activityLogger.logInstanceAction(activity, "acceptLocation", "OK");

                    Intent i = new Intent();
                    i.putExtra(FormEntryActivity.LOCATION_RESULT, answer);

                    activity.setResult(RESULT_OK, i);
                    activity.finish();

                    return Completable.complete();
                });
    }
}
