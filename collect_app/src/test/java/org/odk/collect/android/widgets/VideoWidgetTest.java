package org.odk.collect.android.widgets;


import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.odk.collect.android.BuildConfig;
import org.odk.collect.android.utilities.FileUtil;
import org.odk.collect.android.utilities.MediaUtil;
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
public class VideoWidgetTest extends BinaryNameWidgetTest<VideoWidget> {

    @Mock
    Uri uri;

    @Mock
    MediaUtil mediaUtil;

    @Mock
    FileUtil fileUtil;

    @Mock
    File file;

    private String destinationName = null;

    public VideoWidgetTest() {
        super(VideoWidget.class);
    }

    @NonNull
    @Override
    public VideoWidget createWidget() {
        VideoWidget videoWidget = new VideoWidget(RuntimeEnvironment.application, formEntryPrompt);

        videoWidget.setMediaUtil(mediaUtil);
        videoWidget.setFileUtil(fileUtil);

        return videoWidget;
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(destinationName);
    }

    @Override
    public Object createBinaryData(StringData answerData) {
        return uri;
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();
        destinationName = RandomString.make();
    }

    @Override
    public void settingANewAnswerShouldCallDeleteMediaToRemoveTheOldFile() {
        prepareForSetAnswer();
        super.settingANewAnswerShouldCallDeleteMediaToRemoveTheOldFile();
    }

    @Override
    public void getAnswerShouldReturnCorrectAnswerAfterBeingSet() {
        prepareForSetAnswer();
        super.getAnswerShouldReturnCorrectAnswerAfterBeingSet();
    }

    @Override
    public void settingANewAnswerShouldRemoveTheOldAnswer() {
        prepareForSetAnswer();
        super.settingANewAnswerShouldRemoveTheOldAnswer();
    }

    public void prepareForSetAnswer() {
        when(formEntryPrompt.isReadOnly()).thenReturn(false);

        when(mediaUtil.getPathFromUri(
                RuntimeEnvironment.application,
                uri,
                MediaStore.Video.Media.DATA)

        ).thenReturn(String.format("%s.mp4", RandomString.make()));

        when(fileUtil.getRandomFilename()).thenReturn(destinationName);
        when(fileUtil.getFileAtPath(String.format("/%s.mp4", destinationName)))
                .thenReturn(file);

        when(file.getName()).thenReturn(destinationName);
    }
}