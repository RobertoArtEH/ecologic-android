package com.example.eco_logic;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WeatherFragment extends Fragment implements View.OnClickListener {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;
    private FusedLocationProviderClient client;

    RecyclerView recyclerView;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather, container, false);

        ImageView refresh = view.findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        Bundle bundle = getArguments();
        String temp = bundle.getString("temp", "°C");
        String description = bundle.getString("description", "clear sky");
        String location = bundle.getString("location", "México");

        // RecylerView
        ArrayList<Clima> weatherList = BottomNavigationActivity.weatherList;
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        AdapterWeather adapter = new AdapterWeather(weatherList);
        recyclerView.setAdapter((adapter));

        // Load info
        setBackground(description);
        setTemperature(temp);
        setLocation(location);
        setDate();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volley = VolleySingleton.getInstance(getActivity().getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        requestPermission();

        // Check location permission
        if (ContextCompat.checkSelfPermission( getActivity(), ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("EEEE, d MMM", new Locale("es", "ES")).format(date.getTime());
        day = day.substring(0,1).toUpperCase() + day.substring(1);

        TextView tvDate = view.findViewById(R.id.tv_date);
        tvDate.setText(day);
    }

    private void setLocation(String location) {
        TextView tvLocation = view.findViewById(R.id.tv_location);
        tvLocation.setText(location);
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

        // OpenWeather API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Weather background
                    JSONArray weather = response.getJSONArray("weather");
                    JSONObject weatherObj = weather.getJSONObject(0);
                    String description = weatherObj.getString("description");

                    Toast.makeText(getActivity().getApplicationContext(), description, Toast.LENGTH_SHORT).show();

                    setBackground(description);

                    // Location
                    String location = response.getString("name");

                    setLocation(location);

                    // Temperature
                    JSONObject main = response.getJSONObject("main");
                    double getTemp = main.getDouble("temp");
                    getTemp -= 273.15;

                    int finalTemp = (int) getTemp;
                    String temp = finalTemp + "°C";

                    setTemperature(temp);
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

    private void setTemperature(String temp) {
        TextView tvTemperature = view.findViewById(R.id.tv_temperature);
        tvTemperature.setText(temp);
    }

    private void setBackground(String description) {
        LinearLayout linearLayout = view.findViewById(R.id.ll_weather);
        ImageView icWeather = view.findViewById(R.id.ic_today);
        TextView tvWeather = view.findViewById(R.id.tv_today_desc);

        switch (description) {
            case "dust":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.dust));
                icWeather.setImageResource(R.drawable.ic_mist);
                tvWeather.setText(R.string.dust);
                break;
            case "overcast clouds":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.overcast));
                icWeather.setImageResource(R.drawable.ic_clouds);
                tvWeather.setText(R.string.overcast_clouds);
                break;
            case "scattered clouds":
            case "broken clouds":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.clouds));
                icWeather.setImageResource(R.drawable.ic_broken_clouds);
                tvWeather.setText(R.string.broken_clouds);
                break;
            case "shower rain":
            case "rain":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.rain));
                icWeather.setImageResource(R.drawable.ic_rain);
                tvWeather.setText(R.string.rain);
                break;
            case "thunderstorm":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.thunderstorm));
                icWeather.setImageResource(R.drawable.ic_rain);
                tvWeather.setText(R.string.thunderstorm);
                break;
            case "clear sky":
            case "few clouds":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.clearsky));
                icWeather.setImageResource(R.drawable.ic_sun);
                tvWeather.setText(R.string.clearsky);
                break;
            case "snow":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.snow));
                icWeather.setImageResource(R.drawable.ic_snow);
                tvWeather.setText(R.string.snow);
                break;
            case "mist":
            case "haze":
                linearLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.mist));
                icWeather.setImageResource(R.drawable.ic_mist);
                tvWeather.setText(R.string.mist);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        // Get location
        client = LocationServices.getFusedLocationProviderClient(getActivity());

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
}
