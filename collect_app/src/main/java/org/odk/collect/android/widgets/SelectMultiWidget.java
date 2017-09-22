/*
 * Copyright (C) 2009 University of Washington
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.utilities.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SelctMultiWidget handles multiple selection fields using checkboxes.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */
@SuppressLint("ViewConstructor")
public class SelectMultiWidget extends SelectWidget implements MultiChoiceWidget {

    private final List<CheckBox> checkBoxes;
    private final List<Selection> selections;

    private boolean checkboxInit = true;

    public SelectMultiWidget(@NonNull Context context,
                             @NonNull FormEntryPrompt prompt,
                             @NonNull FormController formController) {

        super(context, prompt, formController);

        checkBoxes = new ArrayList<>();

        if (getPrompt().getAnswerValue() != null) {
            //noinspection unchecked
            selections = (List<Selection>) getPrompt().getAnswerValue().getValue();
        } else {
            selections = new ArrayList<>();
        }

        createLayout();
    }

    @Override
    public void clearAnswer() {
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                c.setChecked(false);
            }
        }
    }

    @Override
    public IAnswerData getAnswer() {
        List<Selection> vc = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); ++i) {
            CheckBox c = checkBoxes.get(i);
            if (c.isChecked()) {
                vc.add(new Selection(getItems().get(i)));
            }
        }

        return vc.size() == 0 ? null : new SelectMultiData(vc);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        for (CheckBox c : checkBoxes) {
            c.setOnLongClickListener(l);
        }
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        for (CheckBox c : checkBoxes) {
            c.cancelLongPress();
        }
    }

    protected List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    protected CheckBox createCheckBox(int index) {
        String choiceName = getPrompt().getSelectChoiceText(getItems().get(index));
        CharSequence choiceDisplayName;
        if (choiceName != null) {
            choiceDisplayName = TextUtils.textToHtml(choiceName);
        } else {
            choiceDisplayName = "";
        }
        // no checkbox group so id by answer + offset
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setTag(index);
        checkBox.setId(newUniqueId());
        checkBox.setText(choiceDisplayName);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAnswerFontSize());
        checkBox.setFocusable(!getPrompt().isReadOnly());
        checkBox.setEnabled(!getPrompt().isReadOnly());

        for (int vi = 0; vi < selections.size(); vi++) {
            // match based on value, not key
            if (getItems().get(index).getValue().equals(selections.get(vi).getValue())) {
                checkBox.setChecked(true);
                break;
            }
        }

        // when clicked, check for readonly before toggling
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!checkboxInit && isReadOnly()) {
                    if (buttonView.isChecked()) {
                        buttonView.setChecked(false);
                    } else {
                        buttonView.setChecked(true);
                    }
                }
            }
        });

        return checkBox;
    }

    protected void createLayout() {
        for (int i = 0; i < getItems().size(); i++) {
            CheckBox checkBox = createCheckBox(i);
            checkBoxes.add(checkBox);
            getAnswerLayout().addView(createMediaLayout(i, checkBox));
        }

        addAnswerView(getAnswerLayout());
        checkboxInit = false;
    }

    @Override
    public int getChoiceCount() {
        return checkBoxes.size();
    }

    @Override
    public void setChoiceSelected(int choiceIndex, boolean isSelected) {
        checkBoxes.get(choiceIndex).setChecked(isSelected);
    }

}
