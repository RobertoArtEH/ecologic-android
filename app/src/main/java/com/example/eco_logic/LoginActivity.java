package com.example.eco_logic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;

    String humidity, envHumidity, envTemperature;

    String userkey = "05037193ee4a460eb2e5ba8bc1e91a45";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        volley = VolleySingleton.getInstance(this.getApplicationContext());

        fRequestQueue = volley.getRequestQueue();

        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

        getPlantHumidity();
        getEnvironmentHumidity();
        getEnvironmentTemperature();
    }

    String loginUrl = "http://ecologic.uttics.com/api/login";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                //Intent loginIntent = new Intent(LoginActivity.this, BottomNavigationActivity.class);
                //startActivity(loginIntent);
                break;
            case R.id.tv_register:
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
        }
    }

    private void login() {
        final JSONObject data = new JSONObject();

        EditText tvEmail = findViewById(R.id.et_email);
        EditText tvPassword = findViewById(R.id.et_password);

        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();

        try {
            data.put("email", email);
            data.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ha ocurrido un error.", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("token");

                    Bundle bundlePlants = new Bundle();
                    bundlePlants.putString("humidity", humidity);
                    bundlePlants.putString("envHumidity", envHumidity);
                    bundlePlants.putString("envTemperature", envTemperature);

                    Intent intent = new Intent(LoginActivity.this, BottomNavigationActivity.class);
                    intent.putExtras(bundlePlants);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OnErrorResponse1: ", error.toString());
                Toast.makeText(LoginActivity.this, "Correo o contraseña incorrecta", Toast.LENGTH_LONG).show();
            }
        });

        fRequestQueue.add(jsonObjectRequest);
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
