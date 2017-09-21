package org.odk.collect.android.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.FontUtil;

public class NotificationActivity extends AppCompatActivity {

    public static final String NOTIFICATION_KEY = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

        String note = this.getIntent().getStringExtra(NOTIFICATION_KEY);
        if (note == null) {
            note = getString(R.string.notification_error);
        }

        TextView notificationText = (TextView) findViewById(R.id.notification);

        FontUtil fontUtil = new FontUtil();

        notificationText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontUtil.getQuestionFontSize());
        notificationText.setTypeface(null, Typeface.BOLD);
        notificationText.setPadding(0, 0, 0, 7);
        notificationText.setText(note);
    }

}
