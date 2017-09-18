package org.odk.collect.android.widgets;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.helper.Selection;
import org.odk.collect.android.widgets.base.QuestionWidgetTest;
import org.robolectric.RuntimeEnvironment;

/**
 * @author James Knight
 */

public class GridMultiWidgetTest extends QuestionWidgetTest<GridMultiWidget, SelectMultiData> {

    public GridMultiWidgetTest() {
        super();
    }

    @NonNull
    @Override
    public GridMultiWidget createWidget() {
        return new GridMultiWidget(RuntimeEnvironment.application, formEntryPrompt, 1);
    }

    @NonNull
    @Override
    public SelectMultiData getNextAnswer() {
        return new SelectMultiData(ImmutableList.<Selection>of());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

    }
}
