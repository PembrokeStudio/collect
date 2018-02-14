package org.odk.collect.android.location.domain;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.database.ActivityLogger;
import org.odk.collect.android.injection.scopes.PerActivity;
import org.odk.collect.android.location.GeoActivity;

import javax.inject.Inject;

import io.reactivex.Completable;

import static android.app.Activity.RESULT_OK;

@PerActivity
public class SaveAnswer {

    private final ActivityLogger activityLogger;
    private final GeoActivity activity;

    @Inject
    SaveAnswer(@NonNull ActivityLogger activityLogger,
               @NonNull GeoActivity activity) {
        this.activityLogger = activityLogger;
        this.activity = activity;
    }

    public Completable save(@NonNull String answer) {
        return Completable.defer(() -> {
            activityLogger.logInstanceAction(activity, "acceptLocation", "OK");

            Intent i = new Intent();
            i.putExtra(FormEntryActivity.LOCATION_RESULT, answer);

            activity.setResult(RESULT_OK, i);
            activity.finish();

            return Completable.complete();
        });
    }
}
