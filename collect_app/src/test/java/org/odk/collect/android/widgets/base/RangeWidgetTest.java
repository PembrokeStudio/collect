package org.odk.collect.android.widgets.base;

import org.javarosa.core.model.RangeQuestion;
import org.javarosa.core.model.data.IAnswerData;
import org.mockito.Mock;
import org.odk.collect.android.widgets.RangeWidget;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

/**
 * @author James Knight
 */

public abstract class RangeWidgetTest<W extends RangeWidget, A extends IAnswerData> extends QuestionWidgetTest<W, A> {

    @Mock
    private RangeQuestion rangeQuestion;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(formEntryPrompt.getQuestion()).thenReturn(rangeQuestion);
        when(rangeQuestion.getAppearanceAttr()).thenReturn("picker");


        when(rangeQuestion.getRangeStart()).thenReturn(new BigDecimal(1));
        when(rangeQuestion.getRangeEnd()).thenReturn(new BigDecimal(10));
        when(rangeQuestion.getRangeStep()).thenReturn(new BigDecimal(1));
    }
}
