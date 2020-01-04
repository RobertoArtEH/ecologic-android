package com.example.eco_logic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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

import com.android.volley.AuthFailureError;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class BottomNavigationActivity extends AppCompatActivity {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;

    public static ArrayList<Clima> weatherList;

    String userkey = "05037193ee4a460eb2e5ba8bc1e91a45";

    String temp, description, location;

    String humidity, envHumidity, envTemperature;

    String lastDate, lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        // Volley
        volley = VolleySingleton.getInstance(this.getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        // Weather data
        // Localización estatica, temporal
        String id = "3981254";
        getCurrentInfo(id);
        getNextDaysInfo(id);

        // Bottom nav listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Initial fragment
        if (savedInstanceState == null) {
            // Get data from login
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                humidity = extras.getString("humidity");
                envHumidity = extras.getString("envHumidity");
                envTemperature = extras.getString("envTemperature");
                lastDate = extras.getString("lastDate");
                lastTime = extras.getString("lastTime");
            }

            // Send data to plants fragment
            Bundle bundlePlants = new Bundle();
            bundlePlants.putString("humidity", humidity);
            bundlePlants.putString("envHumidity", envHumidity);
            bundlePlants.putString("envTemperature", envTemperature);
            bundlePlants.putString("lastDate", lastDate);
            bundlePlants.putString("lastTime", lastTime);

            Fragment plantsFragment = new PlantsFragment();

            plantsFragment.setArguments(bundlePlants);

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
                            Bundle bundlePlants = new Bundle();
                            bundlePlants.putString("humidity", humidity);
                            bundlePlants.putString("envHumidity", envHumidity);
                            bundlePlants.putString("envTemperature", envTemperature);
                            bundlePlants.putString("lastDate", lastDate);
                            bundlePlants.putString("lastTime", lastTime);
                            selectedFragment = new PlantsFragment();
                            selectedFragment.setArguments(bundlePlants);
                            break;
                        case R.id.nav_weather:
                            Bundle bundleWeather = new Bundle();
                            bundleWeather.putString("description", description);
                            bundleWeather.putString("temp", temp);
                            bundleWeather.putString("location", location);

                            selectedFragment = new WeatherFragment();
                            selectedFragment.setArguments(bundleWeather);
                            break;
                        case R.id.nav_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

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

    private void getCurrentInfo(String id) {
        String apikey = "627adac7108a365bdfa7df5853cb7e08";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("weather")
                .appendQueryParameter("id", id)
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

    private void getNextDaysInfo(String id) {
        String apikey = "627adac7108a365bdfa7df5853cb7e08";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendQueryParameter("id", id)
                .appendQueryParameter("appid", apikey);

        String url = builder.build().toString();

        // OpenWeather API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Weather background
                    JSONArray list = response.getJSONArray("list");
                    weatherList = new ArrayList<>();
                    int dayCount = 1;

                    for(int i = 5; i < 45; i += 8) {
                        JSONObject listObj = list.getJSONObject(i);

                        // Description
                        JSONArray weather = listObj.getJSONArray("weather");
                        JSONObject weatherInfo = weather.getJSONObject(0);
                        String desc = weatherInfo.getString("description");

                        // Temperature
                        JSONObject main = listObj.getJSONObject("main");
                        double getTemp = main.getDouble("temp");
                        getTemp -= 273.15;

                        int finalTemp = (int) getTemp;
                        String temperature = finalTemp + "°C";

                        // Date
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, dayCount);
                        Date currentDate = cal.getTime();

                        String month = new SimpleDateFormat("MMM", new Locale("es", "ES")).format(currentDate.getTime());
                        month = month.substring(0, month.length() - 1);
                        month = month.substring(0,1).toUpperCase() + month.substring(1);

                        String dayNumber = new SimpleDateFormat("d", new Locale("es", "ES")).format(currentDate.getTime());

                        String date = dayNumber + " " + month;

                        weatherList.add(new Clima(desc, temperature, date));
                        dayCount++;
                    }
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
