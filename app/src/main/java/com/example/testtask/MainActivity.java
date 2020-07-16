package com.example.testtask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.testtask.entities.AbstractEntity;
import com.example.testtask.entities.TextEntity;
import com.example.testtask.entities.WebViewEntity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private List<String> entityUrls;
    private int position = 0;
    private final Map<String, Class<? extends AbstractEntity>> classMap = new HashMap<>();
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entityUrls = getListOfUrls();
        mainLayout = (LinearLayout)findViewById(R.id.mainlayout);
        Button button = findViewById(R.id.Button1);

        classMap.put("text", TextEntity.class);
        classMap.put("webview", WebViewEntity.class);

        entityDisplay(mainLayout, MainActivity.this);
        button.setOnClickListener(view -> entityDisplay(mainLayout, MainActivity.this));
    }

    private void entityDisplay(LinearLayout mainLayout, Context context) {
        if (entityUrls.size() == 0) {
            return;
        }
        if (position >= entityUrls.size()) {
            position = 0;
        }
        try {
            AbstractEntity entity = getEntity(entityUrls.get(position++));
            if (entity != null) {
                entity.display(mainLayout, context);
            } else {
                entityDisplay(mainLayout, context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private AbstractEntity getEntity (String url) {
        final AbstractEntity[] abstractEntity = {null};
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        abstractEntity[0]
                                = Objects.requireNonNull(
                                        classMap.get(jsonObject.getString("type")))
                                .newInstance();
                        abstractEntity[0].setJson(jsonObject);
                    } catch (JSONException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return abstractEntity[0];
    }

    private List<String> getListOfUrls() {
        List<String> urls = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        String trendingUrl = "https://demo0040494.mockable.io/api/v1/trending";
        String urlGetOneEntityById
                = "https://demo0040494.mockable.io/api/v1/object/";
        Request request = new Request.Builder()
                .url(trendingUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            urls.add(urlGetOneEntityById + jsonArray.getJSONObject(i).getInt("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return urls;
    }
}
