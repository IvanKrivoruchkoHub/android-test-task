package com.example.testtask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.testtask.entities.AbstractEntity;
import com.example.testtask.entities.TextEntity;
import com.example.testtask.entities.WebViewEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        try {
            entityUrls = getListOfUrls();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        } catch (JSONException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private AbstractEntity getEntity (String url) throws IOException, InterruptedException {
        final AbstractEntity[] abstractEntity = {null};

        Runnable runnable =
                () -> { OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert response != null;
                    if (response.isSuccessful()) {
                        String jsonData = null;
                        try {
                            jsonData = Objects.requireNonNull(response.body()).string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);
                            Class<? extends AbstractEntity> aClass
                                    = classMap.get(jsonObject.getString("type"));
                            if (aClass== null) {
                                return;
                            }
                            abstractEntity[0]
                                    = aClass
                                    .newInstance();
                            abstractEntity[0].setJson(jsonObject);
                        } catch (JSONException | InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }};

        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
        return abstractEntity[0];
    }

    private List<String> getListOfUrls() throws InterruptedException {
        List<String> urls = new ArrayList<>();
        String trendingUrl = "https://demo0040494.mockable.io/api/v1/trending";
        String urlGetOneEntityById
                = "https://demo0040494.mockable.io/api/v1/object/";

        Runnable runnable =
                () -> {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(trendingUrl)
                            .build();
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert response != null;
                    if (response.isSuccessful()) {
                        String jsonData = null;
                        try {
                            jsonData = Objects.requireNonNull(response.body()).string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                        JSONArray jsonArray = new JSONArray(jsonData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            urls.add(urlGetOneEntityById + jsonArray.getJSONObject(i).getInt("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
        return urls;
    }
}
