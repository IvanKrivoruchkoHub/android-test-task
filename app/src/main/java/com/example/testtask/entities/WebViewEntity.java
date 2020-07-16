package com.example.testtask.entities;

import android.content.Context;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.json.JSONException;

public class WebViewEntity extends AbstractEntity {

    @Override
    public void display(LinearLayout mainLayout, Context context) throws JSONException {
        mainLayout.removeAllViews();
        WebView webView = new WebView(context);
        webView.loadUrl(getJson().getString("url"));
        LinearLayout.LayoutParams webViewLayoutParams
                = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        webView.setLayoutParams(webViewLayoutParams);
        mainLayout.addView(webView);
    }
}
