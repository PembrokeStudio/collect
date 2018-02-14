package org.odk.collect.android.location;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.architecture.rx.RxViewModelActivity;
import org.odk.collect.android.location.domain.SaveAnswer;
import org.odk.collect.android.utilities.Rx;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class GeoActivity
        extends RxViewModelActivity<GeoViewModel> {

    public static final String MAP_TYPE = "map_type";
    public static final String MAP_FUNCTION = "map_function";

    // Use cases:
    @Inject
    protected SaveAnswer saveAnswer;

    // Outputs:
    @BindView(R.id.location_info)
    protected TextView locationInfoText;

    @BindView(R.id.location_status)
    protected TextView locationStatusText;

    @BindView(R.id.add_button)
    protected ImageButton addButton;

    @BindView(R.id.show_button)
    protected ImageButton showButton;

    @BindView(R.id.pause_button)
    protected ImageButton pauseButton;

    @BindView(R.id.clear_button)
    protected ImageButton clearButton;

    @BindView(R.id.save_button)
    protected ImageButton saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        GeoViewModel viewModel = getViewModel();

        // Bind Location Info:
        viewModel.locationInfoVisibility()
                .compose(bindToLifecycle())
                .subscribe(locationInfoText::setVisibility, Timber::e);

        viewModel.locationInfoText()
                .compose(bindToLifecycle())
                .subscribe(locationInfoText::setText, Timber::e);

        // Bind Location Status:
        viewModel.locationStatusVisibility()
                .compose(bindToLifecycle())
                .subscribe(locationStatusText::setVisibility, Timber::e);

        viewModel.locationStatusText()
                .compose(bindToLifecycle())
                .subscribe(locationStatusText::setText, Timber::e);

        // Bind Button Visibility:
        viewModel.pauseButtonVisibility()
                .compose(bindToLifecycle())
                .subscribe(pauseButton::setVisibility, Timber::e);

        // Bind Button Enable Status:
        viewModel.isAddLocationEnabled()
                .compose(bindToLifecycle())
                .subscribe(addButton::setEnabled);

        viewModel.isShowLocationEnabled()
                .compose(bindToLifecycle())
                .subscribe(showButton::setEnabled);

        viewModel.isClearLocationEnabled()
                .compose(bindToLifecycle())
                .subscribe(clearButton::setEnabled);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getViewModel().watchLocation()
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);
    }

    // Inputs:
    @OnClick(R.id.add_button)
    protected void onAddClick() {
        getViewModel().addLocation()
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);
    }

    @OnClick(R.id.pause_button)
    protected void onPauseClick() {
        getViewModel().pause()
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);
    }

    @OnClick(R.id.show_button)
    protected void onShowClick() {
        getViewModel().showLocation()
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);
    }

    @OnClick(R.id.layers_button)
    protected void onLayersClick() {
        getViewModel().showLayers()
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);
    }

    @OnClick(R.id.clear_button)
    protected void onClearClick() {
        getViewModel().clearLocation()
                .compose(bindToLifecycle())
                .subscribe(Rx::noop, Timber::e);
    }

    @OnClick(R.id.save_button)
    protected void onSaveClick() {
        getViewModel().saveLocation()
                .compose(bindToLifecycle())
                .subscribe(saveAnswer::save, Timber::e);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_geo;
    }
}
