package org.odk.collect.android.location;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.odk.collect.android.location.model.MapFunction;


public abstract class PointTest extends GeoViewModelTest {

    @NonNull
    @Override
    protected MapFunction mapFunction() {
        return MapFunction.POINT;
    }
}
