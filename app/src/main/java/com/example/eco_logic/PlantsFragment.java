package com.example.eco_logic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PlantsFragment extends Fragment implements View.OnClickListener {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;

    SwipeRefreshLayout refreshLayout;

    View view;

    String userkey = "05037193ee4a460eb2e5ba8bc1e91a45";

    String humidity, envHumidity, envTemperature;

    String name;

    String lastDate, lastTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plants, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", MODE_PRIVATE);

        name = preferences.getString("name", "Unknown");

        Button btnWater = view.findViewById(R.id.btn_water);
        btnWater.setOnClickListener(this);

        Bundle bundle = getArguments();
        humidity = bundle.getString("humidity", "%");
        envHumidity = bundle.getString("envHumidity", "%");
        envTemperature = bundle.getString("envTemperature", "°C");
        lastDate = bundle.getString("lastDate");
        lastTime = bundle.getString("lastTime");

        buttonStatus(humidity);

        setLastInfo(lastDate, lastTime);
        setHumidity(humidity);
        setEnvironmentHumidity(envHumidity);
        setEnvironmentTemperature(envTemperature);

        refreshLayout = view.findViewById(R.id.sw_plants);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlantHumidity();
                getEnvironmentHumidity();
                getEnvironmentTemperature();
                getLastWater();

                setLastInfo(lastDate, lastTime);
                buttonStatus(humidity);
                setHumidity(humidity);
                setEnvironmentHumidity(envHumidity);
                setEnvironmentTemperature(envTemperature);

                refreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volley = VolleySingleton.getInstance(getActivity().getApplicationContext());
        fRequestQueue = volley.getRequestQueue();
    }

    private void setLastInfo(String lastDate, String lastTime) {
        //TextView tvDate = view.findViewById(R.id.last_date);
        TextView tvTime = view.findViewById(R.id.last_time);

        String dateString = lastDate + " " + lastTime;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        Date convertedDate = new Date();
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("dd-MM-yyyy", new Locale("es", "ES")).format(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String time = hour + ":" + minute;

        String current = day + " " + time;

        try {
            convertedDate = dateFormat.parse(dateString);
            currentDate = dateFormat.parse(current);

            long different  = currentDate.getTime() - convertedDate.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if(elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes > 0) {
                tvTime.setText(elapsedMinutes + " min.");
            } else if(elapsedDays > 0) {
                tvTime.setText(elapsedDays + " dias, " + elapsedHours + " h");
            } else if(elapsedDays == 0 && elapsedHours > 0) {
                tvTime.setText(elapsedHours + " h, " + elapsedMinutes + "min.");
            } else {
                tvTime.setText("Ahora");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setHumidity(String humidity) {
        TextView tvPlantHumidity = view.findViewById(R.id.tv_humidity_value);
        tvPlantHumidity.setText(humidity);
    }

    private void setEnvironmentHumidity(String humidity) {
        TextView tvEnvironmentHumidity = view.findViewById(R.id.tv_env_humidity);
        tvEnvironmentHumidity.setText(humidity);
    }

    private void setEnvironmentTemperature(String temperature) {
        TextView tvEnvironmentTemperature = view.findViewById(R.id.tv_env_temperature);
        tvEnvironmentTemperature.setText(temperature);
    }

    @Override
    public void onClick(View view) {
        water();
        registerWater();
    }

    private void registerWater() {
        String url = "http://ecologic.uttics.com/api/report";
        final JSONObject data = new JSONObject();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("dd-MM-yyyy", new Locale("es", "ES")).format(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String time = hour + ":" + minute;

        try {
            data.put("date", day);
            data.put("time", time);
            data.put("user", name);
            data.put("humidity", envHumidity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OnErrorResponse: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Content-Type", "application/json");

                return params;
            }
        };

        fRequestQueue.add(request);
    }

    private void water() {
        String url = "https://io.adafruit.com/api/v2/Cesar_utt/feeds/bomba/data";
        final JSONObject data = new JSONObject();

        try {
            data.put("value", "ON");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                disableWaterButton();
                Toast.makeText(getActivity().getApplicationContext(), "Regando...", Toast.LENGTH_SHORT).show();
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

                params.put("Content-Type", "application/json");
                params.put("X-AIO-Key", userkey);

                return params;
            }
        };

        fRequestQueue.add(request);
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

    private void buttonStatus(String strHumidity) {
        strHumidity = strHumidity.substring(0, strHumidity.length() - 1);
        int humidity = Integer.parseInt(strHumidity);

        if(humidity < 20) {
            disableWaterButton();
        }

        if(humidity > 40) {
            CardView cardView = view.findViewById(R.id.cv_humidity);
            cardView.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorAlert));
        }
    }

    private void disableWaterButton() {
        Button btnWater = view.findViewById(R.id.btn_water);

        btnWater.setBackgroundResource(R.drawable.btn_secondary);
        btnWater.setEnabled(false);
        btnWater.setClickable(false);
    }

    public void getLastWater() {
        String url = "http://ecologic.uttics.com/api/last";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray reports = response.getJSONArray("reports");
                    JSONObject obj = reports.getJSONObject(0);

                    lastDate = obj.getString("date");
                    lastTime = obj.getString("time");
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
