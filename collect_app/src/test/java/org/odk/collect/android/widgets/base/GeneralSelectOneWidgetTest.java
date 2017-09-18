package org.odk.collect.android.widgets.base;

import android.support.annotation.NonNull;
import android.widget.RadioButton;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.helper.Selection;
import org.junit.Test;
import org.odk.collect.android.widgets.SelectOneWidget;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author James Knight
 */

public abstract class GeneralSelectOneWidgetTest<W extends SelectOneWidget>
        extends SelectWidgetTest<W, SelectOneData> {

    @NonNull
    @Override
    public SelectOneData getNextAnswer() {
        List<SelectChoice> selectChoices = getSelectChoices();

        int selectedIndex = Math.abs(random.nextInt()) % selectChoices.size();
        SelectChoice selectChoice = selectChoices.get(selectedIndex);

        Selection selection = new Selection(selectChoice);
        return new SelectOneData(selection);
    }

    @Test
    public void getAnswerShouldReflectTheCurrentlyCheckedCheckbox() {
        W widget = getWidget();
        assertNull(widget.getAnswer());

        List<SelectChoice> selectChoices = getSelectChoices();
        List<RadioButton> buttons = widget.getButtons();

        for (int i = 0; i < buttons.size(); i++) {
            RadioButton button = buttons.get(i);
            button.setChecked(true);

            SelectChoice selectChoice = selectChoices.get(i);
            IAnswerData answer = widget.getAnswer();

            assertEquals(selectChoice.getValue(), answer.getDisplayText());
        }
    }
}
