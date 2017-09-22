/*
 * Copyright 2017 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.widgets;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.xpath.expr.XPathFuncExpr;
import org.odk.collect.android.external.ExternalDataUtil;
import org.odk.collect.android.external.ExternalSelectChoice;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.views.MediaLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SelectWidget extends QuestionWidget {

    @NonNull
    private List<SelectChoice> items;
    private ArrayList<MediaLayout> playList;
    private LinearLayout answerLayout;
    private EditText searchString;

    private int playCounter = 0;

    public SelectWidget(@NonNull Context context,
                        @NonNull FormEntryPrompt prompt,
                        @NonNull FormController formController) {

        super(context, prompt, formController);

        answerLayout = new LinearLayout(context);
        answerLayout.setOrientation(LinearLayout.VERTICAL);
        playList = new ArrayList<>();

        // SurveyCTO-added support for dynamic select content (from .csv files)
        XPathFuncExpr xpathFuncExpr = ExternalDataUtil.getSearchXPathExpression(
                prompt.getAppearanceHint());
        if (xpathFuncExpr != null) {
            List<SelectChoice> selectChoices = ExternalDataUtil.populateExternalChoices(prompt, xpathFuncExpr);
            if (selectChoices != null) {
                items = selectChoices;
            } else {
                items = new ArrayList<>();
            }

        } else {
            items = prompt.getSelectChoices();
        }
    }

    @NonNull
    public List<SelectChoice> getItems() {
        return items;
    }

    public LinearLayout getAnswerLayout() {
        return answerLayout;
    }

    public EditText getSearchString() {
        return searchString;
    }

    @Override
    public abstract IAnswerData getAnswer();

    @Override
    public abstract void clearAnswer();

    @Override
    public void setFocus(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
    }

    @Override
    public void resetQuestionTextColor() {
        super.resetQuestionTextColor();
        for (MediaLayout layout : playList) {
            layout.resetTextFormatting();
        }
    }

    @Override
    public void playAllPromptText() {
        // set up to play the items when the
        // question text is finished
        getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                resetQuestionTextColor();
                mediaPlayer.reset();
                playNextSelectItem();
            }

        });
        // plays the question text
        super.playAllPromptText();
    }

    private void playNextSelectItem() {
        if (isShown()) {
            // if there's more, set up to play the next item
            if (playCounter < playList.size()) {
                getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        resetQuestionTextColor();
                        mediaPlayer.reset();
                        playNextSelectItem();
                    }
                });
                // play the current item
                playList.get(playCounter).playAudio();
                playCounter++;

            } else {
                playCounter = 0;
                getMediaPlayer().setOnCompletionListener(null);
                getMediaPlayer().reset();
            }
        }
    }

    protected MediaLayout createMediaLayout(int index, TextView textView) {
        String audioURI = getPrompt().getSpecialFormSelectChoiceText(items.get(index), FormEntryCaption.TEXT_FORM_AUDIO);

        String imageURI;
        if (items.get(index) instanceof ExternalSelectChoice) {
            imageURI = ((ExternalSelectChoice) items.get(index)).getImage();
        } else {
            imageURI = getPrompt().getSpecialFormSelectChoiceText(items.get(index),
                    FormEntryCaption.TEXT_FORM_IMAGE);
        }

        String videoURI = getPrompt().getSpecialFormSelectChoiceText(items.get(index), "video");
        String bigImageURI = getPrompt().getSpecialFormSelectChoiceText(items.get(index), "big-image");

        MediaLayout mediaLayout = new MediaLayout(getContext(), getMediaPlayer());
        mediaLayout.setAVT(getPrompt().getIndex(), "." + Integer.toString(index), textView, audioURI,
                imageURI, videoURI, bigImageURI);

        mediaLayout.setAudioListener(this);
        mediaLayout.setPlayTextColor(getPlayColor());
        playList.add(mediaLayout);

        if (index != items.size() - 1) {
            ImageView divider = new ImageView(getContext());
            divider.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
            mediaLayout.addDivider(divider);
        }

        return mediaLayout;
    }

    protected void doSearch(String searchStr) {
        // First check if there is nothing on search
        if (searchStr == null || searchStr.trim().length() == 0) {
            createFilteredOptions(items, null);
        } else { // Create a List with items that are relevant to the search text
            List<SelectChoice> searchedItems = new ArrayList<>();
            List<Integer> tagList = new ArrayList<>();
            searchStr = searchStr.toLowerCase(Locale.US);
            for (int i = 0; i < items.size(); i++) {
                String choiceText = getPrompt().getSelectChoiceText(items.get(i)).toLowerCase(Locale.US);
                if (choiceText.contains(searchStr)) {
                    searchedItems.add(items.get(i));
                    tagList.add(i);
                }
            }
            createFilteredOptions(searchedItems, tagList);
        }
    }

    private void setupChangeListener() {
        searchString.addTextChangedListener(new TextWatcher() {
            private String oldText = "";

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(oldText)) {
                    doSearch(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    protected void setUpSearchBox() {
        searchString = new EditText(getContext());
        searchString.setId(newUniqueId());
        searchString.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAnswerFontSize());

        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.setMargins(7, 5, 7, 5);
        searchString.setLayoutParams(params);
        setupChangeListener();
        addAnswerView(searchString);

        doSearch("");
    }

    private void createFilteredOptions(List<SelectChoice> searchedItems, List<Integer> tagList) {
        removeView(answerLayout);
        answerLayout.removeAllViews();

        if (searchedItems != null && !searchedItems.isEmpty()) {
            addButtonsToLayout(tagList);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, searchString.getId());
        params.setMargins(10, 0, 10, 0);
        addView(answerLayout, params);
    }

    protected void addButtonsToLayout(List<Integer> tagList) {
    }
}
