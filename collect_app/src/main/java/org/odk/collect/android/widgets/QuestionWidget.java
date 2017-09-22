/*
 * Copyright (C) 2011 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.exception.JavaRosaException;
import org.odk.collect.android.listeners.AudioPlayListener;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.utilities.FontUtil;
import org.odk.collect.android.utilities.TextUtils;
import org.odk.collect.android.utilities.ViewUtil;
import org.odk.collect.android.views.MediaLayout;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import timber.log.Timber;

public abstract class QuestionWidget
        extends RelativeLayout
        implements Widget, AudioPlayListener {

    //region Constants

    private static final int DEFAULT_PLAY_COLOR = Color.BLUE;
    //endregion

    //region Attributes

    @NonNull
    private final FormEntryPrompt prompt;

    @NonNull
    private final FormController formController;

    @NonNull
    private final MediaLayout questionMediaLayout;

    @NonNull
    private final MediaPlayer mediaPlayer;

    @NonNull
    private final TextView helpTextView;

    private IAnswerData answer;

    private final int questionFontSize;

    private int playColor = DEFAULT_PLAY_COLOR;
    //endregion

    //region Constructors

    public QuestionWidget(@NonNull Context context,
                          @NonNull FormEntryPrompt prompt,
                          @NonNull FormController formController) {

        this(context, prompt, formController, new FontUtil());
    }

    public QuestionWidget(@NonNull Context context,
                          @NonNull FormEntryPrompt prompt,
                          @NonNull FormController formController,
                          @NonNull FontUtil fontUtil) {
        super(context);
        setAnswer(prompt.getAnswerValue());

        this.prompt = prompt;
        this.formController = formController;

        this.questionFontSize = fontUtil.getQuestionFontSize();

        this.mediaPlayer = createMediaPlayer();
        this.questionMediaLayout = createQuestionMediaLayout(prompt);
        this.helpTextView = createHelpText(prompt);

        configureView();
    }

    //endregion

    //region Accessors

    @Override
    public void waitForData() {
        formController.setIndexWaitingForData(getIndex());
    }

    @NonNull
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @NonNull
    public MediaLayout getQuestionMediaLayout() {
        return questionMediaLayout;
    }

    public int getQuestionFontSize() {
        return questionFontSize;
    }

    public int getAnswerFontSize() {
        return getQuestionFontSize() + 2;
    }

    public int getPlayColor() {
        return playColor;
    }

    @NonNull
    public FormEntryPrompt getPrompt() {
        return prompt;
    }

    @NonNull
    public FormController getFormController() {
        return formController;
    }

    @NonNull
    public TextView getHelpTextView() {
        return helpTextView;
    }

    @NonNull
    public FormIndex getIndex() {
        return getPrompt().getIndex();
    }

    @NonNull
    public QuestionDef getQuestion() {
        return getPrompt().getQuestion();
    }

    @Nullable
    public IAnswerData getPromptAnswer() {
        return getPrompt().getAnswerValue();
    }

    public boolean isReadOnly() {
        return getPrompt().isReadOnly();
    }

    @Override
    public final IAnswerData getAnswer() {
        return answer;
    }

    @Override
    public void clearAnswer() {
        this.answer = null;
    }

    @OverridingMethodsMustInvokeSuper
    public void setAnswer(IAnswerData answer) {
        this.answer = answer;
    }

    @Override
    public boolean isWaitingForData() {
        return getIndex().equals(formController.getIndexWaitingForData());
    }

    @Override
    public void cancelWaitingForData() {
        formController.setIndexWaitingForData(null);
    }

    //endregion

    //region Media Player
    public void playVideo() {
        questionMediaLayout.playVideo();
    }

    public void playAudio() {
        playAllPromptText();
    }

    public void stopAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    /**
     * Prompts with items must override this.
     */
    public void playAllPromptText() {
        questionMediaLayout.playAudio();
    }
    //endregion

    //region View interaction
    // http://code.google.com/p/android/issues/detail?id=8488
    public void recycleDrawables() {
        List<ImageView> images = new ArrayList<>();
        // collect all the image views
        recycleDrawablesRecursive(this, images);

        for (ImageView imageView : images) {
            imageView.destroyDrawingCache();
            Drawable d = imageView.getDrawable();

            if (d != null && d instanceof BitmapDrawable) {
                imageView.setImageDrawable(null);
                BitmapDrawable bd = (BitmapDrawable) d;
                Bitmap bmp = bd.getBitmap();
                if (bmp != null) {
                    bmp.recycle();
                }
            }
        }
    }

    public void resetQuestionTextColor() {
        questionMediaLayout.resetTextFormatting();
    }

    /**
     * Override this to implement fling gesture suppression (e.g. for embedded WebView treatments).
     *
     * @return true if the fling gesture should be suppressed
     */
    public boolean suppressFlingGesture(MotionEvent e1, MotionEvent e2, float velocityX,
                                        float velocityY) {
        return false;
    }
    //endregion

    //region View class overrides

    /**
     * Every subclassed widget should override this, adding any views they may contain, and calling
     * super.cancelLongPress()
     */
    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        questionMediaLayout.cancelLongPress();
        helpTextView.cancelLongPress();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == INVISIBLE || visibility == GONE) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
    }
    //endregion

    //region View IDs

    protected int newUniqueId() {
        return ViewUtil.generateViewId();
    }

    //endregion

    //region View positioning

    /**
     * Adds a View containing the question text, audio (if applicable), and image (if applicable).
     *
     * To satisfy the RelativeLayout constraints, we add the audio first if it exists, then the
     * TextView to fit the rest of the space, then the image if applicable.
     *
     * Defaults to adding questionMediaLayout to the top of the screen.
     * Overwrite to reposition.
     */
    protected void addQuestionMediaLayout(View v) {
        if (v == null) {
            Timber.e("cannot add a null view as questionMediaLayout");
            return;
        }

        // Defaults for questionMediaLayout:
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.setMargins(10, 0, 10, 0);

        addView(v, params);
    }


    /**
     * Adds a TextView containing the help text to the default location.
     * Override to reposition.
     */
    protected void addHelpTextView(View v) {
        if (v == null) {
            Timber.e("cannot add a null view as helpTextView");
            return;
        }

        // default for helptext
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, questionMediaLayout.getId());
        params.setMargins(10, 0, 10, 0);
        addView(v, params);
    }

    /**
     * Default place to put the answer View: below the help text or
     * question text if there is no help text.
     *
     * If you have many elements, use this first and use the standard
     * addView(view, params) to place the rest of the Views.
     */
    protected void addAnswerView(View v) {
        if (v == null) {
            Timber.e("cannot add a null view as an answerView");
            return;
        }

        // default place to add answer
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        if (helpTextView.getVisibility() == View.VISIBLE) {
            params.addRule(RelativeLayout.BELOW, helpTextView.getId());
        } else {
            params.addRule(RelativeLayout.BELOW, questionMediaLayout.getId());
        }

        params.setMargins(10, 0, 10, 0);
        addView(v, params);
    }
    //endregion

    //region View creation helper methods

    protected Button getSimpleButton(String text) {
        Button button = new Button(getContext());

        button.setId(newUniqueId());
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAnswerFontSize());
        button.setPadding(20, 20, 20, 20);

        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.setMargins(7, 5, 7, 5);

        button.setLayoutParams(params);

        return button;
    }

    protected TextView getCenteredAnswerTextView() {
        TextView textView = getAnswerTextView();
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    protected TextView getAnswerTextView() {
        TextView textView = new TextView(getContext());

        textView.setId(newUniqueId());
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAnswerFontSize());
        textView.setPadding(20, 20, 20, 20);

        return textView;
    }
    //endregion

    //region View configuration

    private void configureView() {
        setId(newUniqueId());
        setGravity(Gravity.TOP);
        setPadding(0, 7, 0, 0);

        addQuestionMediaLayout(questionMediaLayout);
        addHelpTextView(helpTextView);
    }
    //endregion

    //region View creation

    @NonNull
    private MediaPlayer createMediaPlayer() {
        MediaPlayer player = new MediaPlayer();
        player.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                questionMediaLayout.resetTextFormatting();
                mediaPlayer.reset();
            }

        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Timber.e("Error occured in MediaPlayer. what = %d, extra = %d",
                        what, extra);
                return false;
            }
        });

        return player;
    }

    @NonNull
    private MediaLayout createQuestionMediaLayout(FormEntryPrompt prompt) {

        String promptText = prompt.getLongText();

        // Add the text view. TextView always exists, regardless of whether there's text.
        TextView questionText = new TextView(getContext());

        questionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, questionFontSize);
        questionText.setTypeface(null, Typeface.BOLD);
        questionText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
        questionText.setPadding(0, 0, 0, 7);
        questionText.setText(promptText != null ? TextUtils.textToHtml(promptText) : "");
        questionText.setMovementMethod(LinkMovementMethod.getInstance());
        questionText.setHorizontallyScrolling(false); // Wrap to the size of the parent view

        if (promptText == null || promptText.length() == 0) {
            questionText.setVisibility(GONE);
        }

        String imageURI = prompt.getImageText();
        String audioURI = prompt.getAudioText();
        String videoURI = prompt.getSpecialFormQuestionText("video");

        // shown when image is clicked
        String bigImageURI = prompt.getSpecialFormQuestionText("big-image");

        // Create the layout for audio, image, text
        MediaLayout questionMediaLayout = new MediaLayout(getContext(), mediaPlayer);

        questionMediaLayout.setId(newUniqueId()); // assign random id
        questionMediaLayout.setAVT(prompt.getIndex(), "", questionText, audioURI, imageURI, videoURI,
                bigImageURI);
        questionMediaLayout.setAudioListener(this);

        // Set the media layout's play text color:
        String playColorString = prompt.getFormElement().getAdditionalAttribute(null, "playColor");
        if (playColorString != null) {
            try {
                playColor = Color.parseColor(playColorString);
            } catch (IllegalArgumentException e) {
                Timber.e(e, "Argument %s is incorrect", playColorString);
            }
        }
        questionMediaLayout.setPlayTextColor(playColor);

        return questionMediaLayout;
    }

    @SuppressWarnings("ResourceType")
    @NonNull
    private TextView createHelpText(FormEntryPrompt prompt) {
        TextView helpText = new TextView(getContext());
        String s = prompt.getHelpText();

        if (s != null && !s.equals("")) {

            helpText.setId(ViewUtil.generateViewId());
            helpText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, questionFontSize - 3);
            helpText.setPadding(0, -5, 0, 7);

            helpText.setHorizontallyScrolling(false);
            helpText.setTypeface(null, Typeface.ITALIC);
            helpText.setText(TextUtils.textToHtml(s));
            helpText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
            helpText.setMovementMethod(LinkMovementMethod.getInstance());

            return helpText;

        } else {
            helpText.setVisibility(View.GONE);
            return helpText;
        }
    }
    //endregion

    //region Misc.
    /**
     * It's needed only for external choices. Everything works well and
     * out of the box when we use internal choices instead
     */
    protected void clearNextLevelsOfCascadingSelect() {
        FormController formController = Collect.getInstance().getFormController();
        if (formController == null) {
            return;
        }

        if (formController.currentCaptionPromptIsQuestion()) {
            try {
                FormIndex startFormIndex = formController.getQuestionPrompt().getIndex();
                formController.stepToNextScreenEvent();
                while (formController.currentCaptionPromptIsQuestion()
                        && formController.getQuestionPrompt().getFormElement().getAdditionalAttribute(null, "query") != null) {
                    formController.saveAnswer(formController.getQuestionPrompt().getIndex(), null);
                    formController.stepToNextScreenEvent();
                }
                formController.jumpToIndex(startFormIndex);

            } catch (JavaRosaException e) {
                Timber.e(e);
            }
        }
    }

    // http://code.google.com/p/android/issues/detail?id=8488
    private void recycleDrawablesRecursive(ViewGroup viewGroup, List<ImageView> images) {

        int childCount = viewGroup.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = viewGroup.getChildAt(index);
            if (child instanceof ImageView) {
                images.add((ImageView) child);
            } else if (child instanceof ViewGroup) {
                recycleDrawablesRecursive((ViewGroup) child, images);
            }
        }

        viewGroup.destroyDrawingCache();
    }
    //endregion

    //region Abstract methods
    public abstract void setFocus(Context context);

    public abstract void setOnLongClickListener(OnLongClickListener l);
    //endregion
}
