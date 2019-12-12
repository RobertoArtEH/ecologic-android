package com.example.eco_logic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;

    ArrayList<Reporte> reportList;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        getReports();
    }

    private void getReports() {
        String url = "http://ecologic.uttics.com/api/reports";
        reportList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_reports_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Reporte>>(){}.getType();
                String string = response.toString();

                reportList = gson.fromJson(string, type);

                AdapterReport adapterReport = new AdapterReport(reportList);
                recyclerView.setAdapter(adapterReport);

                int size = reportList.size();
                String numReports = size + " reportes";
                TextView tv_size = findViewById(R.id.reports_size);
                tv_size.setText(numReports);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        fRequestQueue.add(request);
    }
}
