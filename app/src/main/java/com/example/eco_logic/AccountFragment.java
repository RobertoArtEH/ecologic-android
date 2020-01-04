package com.example.eco_logic;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;

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

import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment implements View.OnClickListener {
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;

    View view;

    String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        volley = VolleySingleton.getInstance(getActivity().getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        loadPreferences();

        LinearLayout btnReports = view.findViewById(R.id.btn_reports);
        LinearLayout btnLogout = view.findViewById(R.id.btn_logout);
        btnReports.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        return view;
    }

    public void loadPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", MODE_PRIVATE);

        String name = preferences.getString("name", "Unknown");
        String email = preferences.getString("email", "Sin email");
        token = preferences.getString("token", "null");

        TextView tvName = view.findViewById(R.id.tv_account_name);
        TextView tvEmail = view.findViewById(R.id.tv_account_email);
        tvName.setText(name);
        tvEmail.setText(email);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                logout();
                break;
            case R.id.btn_reports:
                Intent intent = new Intent(getActivity().getApplicationContext(), ReportActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void logout() {
        String url = "http://ecologic.uttics.com/api/logout";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OnErrorResponse1: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Bearer " + token);

                SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", "null");
                editor.apply();

                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();

                return params;
            }
        };

        fRequestQueue.add(jsonObjectRequest);
    }
}
