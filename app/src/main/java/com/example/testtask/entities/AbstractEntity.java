package com.example.testtask.entities;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractEntity {
    private JSONObject json;

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public abstract void display(LinearLayout mainLayout, Context context) throws JSONException;
}

