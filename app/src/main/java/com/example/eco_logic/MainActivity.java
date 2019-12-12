package com.example.eco_logic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;

    String humidity, envHumidity, envTemperature;

    String userkey = "05037193ee4a460eb2e5ba8bc1e91a45";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volley = VolleySingleton.getInstance(this.getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        getPlantHumidity();
        getEnvironmentHumidity();
        getEnvironmentTemperature();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundlePlants = new Bundle();
                bundlePlants.putString("humidity", humidity);
                bundlePlants.putString("envHumidity", envHumidity);
                bundlePlants.putString("envTemperature", envTemperature);

                SharedPreferences preferences = getSharedPreferences("credenciales", MODE_PRIVATE);

                String token = preferences.getString("token", "null");

                if(token.equals("null")) {
                    Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                    intentLogin.putExtras(bundlePlants);
                    startActivity(intentLogin);
                    finish();
                } else {
                    Intent intentHome = new Intent(MainActivity.this, BottomNavigationActivity.class);
                    intentHome.putExtras(bundlePlants);
                    startActivity(intentHome);
                    finish();
                }
            }
        }, 2000);
    }

    private void getPlantHumidity() {
        String url = "https://io.adafruit.com/api/v2/Cesar_utt/feeds/humedadplantas/data/last";

        // OpenWeather API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int getHumidity = response.getInt("value");
                    humidity = getHumidity + "%";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OnErrorResponse: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("X-AIO-Key", userkey);

                return params;
            }
        };

        fRequestQueue.add(request);
    }

    private void getEnvironmentHumidity() {
        String url = "https://io.adafruit.com/api/v2/Cesar_utt/feeds/humedad/data/last";

        // OpenWeather API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int getHumidity = response.getInt("value");
                    envHumidity = getHumidity + "%";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OnErrorResponse: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("X-AIO-Key", userkey);

                return params;
            }
        };

        fRequestQueue.add(request);
    }

    private void getEnvironmentTemperature() {
        String url = "https://io.adafruit.com/api/v2/Cesar_utt/feeds/temperatura/data/last";

        // OpenWeather API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int getTemperature = response.getInt("value");
                    envTemperature = getTemperature + "°C";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OnErrorResponse: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("X-AIO-Key", userkey);

                return params;
            }
        };

        fRequestQueue.add(request);
    }
}
