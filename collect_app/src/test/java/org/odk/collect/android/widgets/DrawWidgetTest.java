package org.odk.collect.android.widgets;

import android.support.annotation.NonNull;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.odk.collect.android.BuildConfig;
import org.odk.collect.android.widgets.base.BinaryNameWidgetTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;

import static org.mockito.Mockito.when;


/**
 * @author James Knight
 */
@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class DrawWidgetTest extends BinaryNameWidgetTest<DrawWidget> {

    @Mock
    File file;

    private String fileName = null;

    public DrawWidgetTest() {
        super(DrawWidget.class);
    }

    @NonNull
    @Override
    public DrawWidget createWidget() {
        return new DrawWidget(RuntimeEnvironment.application, formEntryPrompt);
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(fileName);
    }

    @Override
    public Object createBinaryData(StringData answerData) {
        return file;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        fileName = RandomString.make();
    }

    @Override
    protected void prepareForSetAnswer() {

        when(file.exists()).thenReturn(true);
        when(file.getName()).thenReturn(fileName);
    }
}