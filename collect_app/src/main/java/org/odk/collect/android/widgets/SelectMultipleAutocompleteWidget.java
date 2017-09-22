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

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.listeners.AudioPlayListener;
import org.odk.collect.android.logic.FormController;

import java.util.List;

@SuppressLint("ViewConstructor")
public class SelectMultipleAutocompleteWidget extends SelectMultiWidget implements CompoundButton.OnCheckedChangeListener, AudioPlayListener {

    public SelectMultipleAutocompleteWidget(@NonNull Context context,
                                            @NonNull FormEntryPrompt prompt,
                                            @NonNull FormController formController) {

        super(context, prompt, formController);
    }

    @Override
    protected void addButtonsToLayout(List<Integer> tagList) {
        List<CheckBox> checkBoxes = getCheckBoxes();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (tagList == null || tagList.contains(i)) {
                getAnswerLayout().addView(checkBoxes.get(i));
            }
        }
    }

    @Override
    public void setFocus(Context context) {
        // Put focus on text input field and display soft keyboard if appropriate.
        getSearchString().requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(getSearchString(), 0);
    }

    @Override
    protected void createLayout() {
        for (int i = 0; i < getItems().size(); i++) {
            getCheckBoxes().add(createCheckBox(i));
        }

        setUpSearchBox();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }
}