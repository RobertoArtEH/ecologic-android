package com.example.eco_logic;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WeatherFragment extends Fragment {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;
    private FusedLocationProviderClient client;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather, container, false);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volley = VolleySingleton.getInstance(getActivity().getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        requestPermission();

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        // Check location permission
        if (ContextCompat.checkSelfPermission( getActivity(), ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Get location
        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    Double lat = location.getLatitude();
                    Double lon = location.getLongitude();

                    setCurrentInfo(lat, lon);
                }
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    private void setCurrentInfo(Double lat, Double lon) {
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

        // Set date
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("EEEE, d MMM", new Locale("es", "ES")).format(date.getTime());
        String upperDay = day.substring(0,1).toUpperCase() + day.substring(1);

        TextView tvDate = view.findViewById(R.id.tv_date);
        tvDate.setText(upperDay);

        // OpenWeather API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Weather background
                    JSONArray weather = response.getJSONArray("weather");
                    JSONObject weatherObj = weather.getJSONObject(0);
                    String description = weatherObj.getString("description");

                    // Toast.makeText(getActivity().getApplicationContext(), description, Toast.LENGTH_SHORT).show();

                    setBackground(description);

                    // Temperature
                    JSONObject main = response.getJSONObject("main");
                    double getTemp = main.getDouble("temp");
                    getTemp -= 273.15;

                    int finalTemp = (int) getTemp;
                    String temp = finalTemp + "°C";

                    TextView tvTemperature = view.findViewById(R.id.tv_temperature);
                    tvTemperature.setText(temp);
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

    private void setBackground(String description) {
        LinearLayout linearLayout = view.findViewById(R.id.ll_weather);

        switch (description) {
            case "few clouds":
            case "scattered clouds":
            case "broken clouds":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.clouds));
                break;
            case "shower rain":
            case "rain":
            case "thunderstorm":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.rain));
                break;
            case "clear sky":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.clearsky));
                break;
            case "snow":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.snow));
                break;
            case "mist":
            case "haze":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.mist));
                break;
        }
    }
}
