package com.example.testtask.entities;


import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

public class TextEntity extends AbstractEntity {

    @Override
    public void display(LinearLayout mainLayout, Context context) throws JSONException {
        mainLayout.removeAllViews();
        TextView textView = new TextView(context);
        textView.setText(getJson().getString("contents"));
        LinearLayout.LayoutParams textViewLayoutParams
                = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textViewLayoutParams);
        mainLayout.addView(textView);
    }
}
