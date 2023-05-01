package com.example.weatherforecast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherforecast.R;
import com.example.weatherforecast.databinding.ActivityMainBinding;
import com.example.weatherforecast.network.Network;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Suppress;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using the binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Extract the latitude and longitude values from the intent
        currentLocation = intent.getStringExtra("LATITUDE") + "," + intent.getStringExtra("LONGITUDE");

        // Create an instance of OkHttpClient to make HTTP requests
        final OkHttpClient client = new OkHttpClient();

        // Create a new HTTP request using the OpenWeather API and the current location
        final Request request = new Request.Builder()
                .url(Network.openWeatherAPI + "current.json?key=" + Network.openWeatherAPIKey + "&aqi=no&q=" + currentLocation)
                .build();

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            // This method runs on a background thread and executes the network request using the OkHttp client to fetch weather data.
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Executes the request and returns the response
                    Response response = client.newCall(request).execute();
                    // If the response is not successful, return null.
                    if (!response.isSuccessful()) {
                        return null;
                    }
                    // If the response is successful, return the response body as a string.
                    return response.body().string();
                } catch (Exception e) {
                    // If there's an exception, print the stack trace and return null.
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    // Parse JSON response
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(s);

                        // Get location data from the response and update UI
                        JSONObject locationObject = jsonResponse.getJSONObject("location");
                        binding.locationText.setText(locationObject.getString("name"));

                        // Get current weather data from the response and update UI
                        JSONObject currentObject = jsonResponse.getJSONObject("current");
                        String temperature = currentObject.getString("temp_c");
                        binding.currentText.setText(temperature + "°C");

                        // Get forecast data from the response and update UI
                        JSONObject forecastObject = jsonResponse.getJSONObject("forecast");
                        JSONArray forecastdayObject = forecastObject.getJSONArray("forecastday");
                        JSONObject dayObject = forecastdayObject.getJSONObject(0).getJSONObject("day");
                        String minTemp = String.valueOf(Math.round(Double.parseDouble(dayObject.getString("mintemp_c"))));
                        String maxTemp = String.valueOf(Math.round(Double.parseDouble(dayObject.getString("maxtemp_c"))));
                        binding.minimumText.setText("min: " + minTemp + "°C");
                        binding.maximumText.setText("max: " + maxTemp + "°C");

                        // Get weather icon URL from the response and use Picasso library to load the image and update UI
                        JSONObject conditionObject = currentObject.getJSONObject("condition");
                        String image_url =  "https:" + conditionObject.getString("icon");
                        Picasso.get().load(image_url).resize(100, 100).into(binding.weatherImage);

                    } catch (JSONException e) {
                        // Throw a RuntimeException if there is an error while parsing the JSON response
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        asyncTask.execute();
    }
}
