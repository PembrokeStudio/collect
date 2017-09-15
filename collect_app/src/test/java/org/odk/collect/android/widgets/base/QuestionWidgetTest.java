package org.odk.collect.android.widgets.base;

import android.support.annotation.NonNull;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.junit.Test;
import org.mockito.Mock;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.widgets.Widget;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public abstract class QuestionWidgetTest<W extends Widget, A extends IAnswerData>
        extends WidgetTest {

    protected Random random = new Random();

    private final Class<W> clazz;
    private W widget = null;

    @Mock
    public FormIndex formIndex;

    @Mock
    public FormController formController;

    // Needs to be public for JUnit:
    @SuppressWarnings("WeakerAccess")
    public QuestionWidgetTest(Class<W> clazz) {
        this.clazz = clazz;
    }

    @NonNull
    public abstract W createWidget();

    @NonNull
    public abstract A getNextAnswer();

    public A getInitialAnswer() {
        return getNextAnswer();
    }

    public W getWidget() {
        if (widget == null) {
            widget = spy(createWidget());
        }

        return widget;
    }

    public void setUp() throws Exception {
        super.setUp();

        when(formEntryPrompt.getIndex()).thenReturn(formIndex);

        Collect.getInstance().setFormController(formController);

        widget = null;
    }

    @Test
    public void getAnswerShouldReturnNullIfPromptDoesNotHaveExistingAnswer() {
        W widget = getWidget();
        assertNull(widget.getAnswer());
    }

    @Test
    public void getAnswerShouldReturnExistingAnswerIfPromptHasExistingAnswer() {
        A answer = getInitialAnswer();
        if (answer instanceof StringData) {
            when(formEntryPrompt.getAnswerText()).thenReturn((String) answer.getValue());
        } else {
            when(formEntryPrompt.getAnswerValue()).thenReturn(answer);
        }

        W widget = getWidget();
        IAnswerData newAnswer = widget.getAnswer();

        assertNotNull(newAnswer);
        assertEquals(newAnswer.getDisplayText(), answer.getDisplayText());
    }

    @Test
    public void callingClearShouldRemoveTheExistingAnswer() {
        A answer = getNextAnswer();
        if (clazz.isAssignableFrom(BinaryNameWidgetTest.class)) {
            when(formEntryPrompt.getAnswerText()).thenReturn((String) answer.getValue());
        }

        W widget = getWidget();
        widget.clearAnswer();

        assertNull(widget.getAnswer());
    }
}