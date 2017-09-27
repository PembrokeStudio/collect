package org.odk.collect.android.utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.PreferenceKeys;

public class FontUtil {

    private static final int DEFAULT_FONT_SIZE = 21;

    private final Collect collect;

    public FontUtil() {
        this(Collect.getInstance());
    }

    private FontUtil(Collect collect) {
        this.collect = collect;
    }

    public int getQuestionFontSize() {
        if (collect == null) {
            return DEFAULT_FONT_SIZE;
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(collect);
        if (settings == null) {
            return DEFAULT_FONT_SIZE;
        }

        String questionFont = settings.getString(PreferenceKeys.KEY_FONT_SIZE, "");

        return !questionFont.isEmpty()
                ? Integer.valueOf(questionFont)
                : DEFAULT_FONT_SIZE;
    }

    public String getDefaultFontSizeString() {
        return Integer.toString(DEFAULT_FONT_SIZE);
    }
}
