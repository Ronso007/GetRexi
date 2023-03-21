package com.ronsapir.getRexi.auth.data;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.auth.data.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DogApi {
    private static final DogApi instance = new DogApi();
    private OkHttpClient client;
    private final String apiKey = "live_SrBT4v5Y02emFdlSrLKr7t12AQ8uomXOIx4poV3J4OJQZh9rKg17P5kuiCFCCena";

    private DogApi() {
        client = new OkHttpClient();
    }

    public static DogApi instance() {
        return instance;
    }

    public void getBreedOptions(Model.Listener<List> callback) {
        Request request = new Request.Builder()
                .url("https://api.thedogapi.com/v1/breeds")
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONArray results = responseObject.getJSONArray("results");
                        List<String> optionsList = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            try {
                                JSONObject jsonObject = results.getJSONObject(i);
                                String option = jsonObject.getString("name");
                                optionsList.add(option);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        callback.onComplete(optionsList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getDogTemperament(String dogBreed, Model.Listener<String> callback) {
        Request request = new Request.Builder()
                .url("https://api.thedogapi.com/v1/breeds/search?name=" + dogBreed)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String temperament = json.getString("temperament");
                        callback.onComplete(temperament);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void getLifeSpan(String dogBreed, Model.Listener<String> callback) {
        Request request = new Request.Builder()
                .url("https://api.thedogapi.com/v1/breeds/search?name=" + dogBreed)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String temperament = json.getString("temperament");
                        callback.onComplete(temperament);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
