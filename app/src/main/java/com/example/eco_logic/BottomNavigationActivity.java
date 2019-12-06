package com.example.eco_logic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class BottomNavigationActivity extends AppCompatActivity {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;

    String temp, description, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        // Volley
        volley = VolleySingleton.getInstance(this.getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        // Weather data
        // Localización estatica, temporal
        Double lat = 25.5260303;
        Double lon = -103.3989277;
        getCurrentInfo(lat, lon);

        // Bottom nav listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Initial fragment
        if (savedInstanceState == null) {

            Fragment plantsFragment = new PlantsFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, plantsFragment).commit();
        }

        // Change status bar color to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_plants:
                            selectedFragment = new PlantsFragment();
                            break;
                        case R.id.nav_weather:
                            Bundle bundle = new Bundle();
                            bundle.putString("description", description);
                            bundle.putString("temp", temp);
                            bundle.putString("location", location);

                            selectedFragment = new WeatherFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.nav_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    private void getCurrentInfo(Double lat, Double lon) {
        String apikey = "627adac7108a365bdfa7df5853cb7e08";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("weather")
                .appendQueryParameter("lat", lat.toString())
                .appendQueryParameter("lon", lon.toString())
                .appendQueryParameter("appid", apikey);

        String url = builder.build().toString();

        // OpenWeather API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Weather background
                    JSONArray weather = response.getJSONArray("weather");
                    JSONObject weatherObj = weather.getJSONObject(0);
                    description = weatherObj.getString("description");

                    // Location
                    location = response.getString("name");

                    // Temperature
                    JSONObject main = response.getJSONObject("main");
                    double getTemp = main.getDouble("temp");
                    getTemp -= 273.15;

                    int finalTemp = (int) getTemp;
                    temp = finalTemp + "°C";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OnErrorResponse: ", error.toString());
            }
        });

        fRequestQueue.add(request);
    }
}
