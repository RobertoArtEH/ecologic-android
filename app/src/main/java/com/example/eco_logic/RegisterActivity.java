package com.example.eco_logic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    protected RequestQueue fRequestQueue;
    private VolleySingleton volley;

    String humidity, envHumidity, envTemperature;

    String lastDate, lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            humidity = extras.getString("humidity");
            envHumidity = extras.getString("envHumidity");
            envTemperature = extras.getString("envTemperature");
            lastDate = extras.getString("lastDate");
            lastTime = extras.getString("lastTime");
        }

        volley = VolleySingleton.getInstance(this.getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        Button btnregister = findViewById(R.id.btn_register);

        btnregister.setOnClickListener(this);

    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                btn_register();
                break;

        }

    }
    private void btn_register() {
        final JSONObject data = new JSONObject();
        EditText name = findViewById(R.id.btn_user);
        EditText email = findViewById(R.id.btn_email);
        EditText pass = findViewById(R.id.btn_pass);
        try {
            data.put("name", name.getText());
            data.put("email", email.getText());
            data.put("password", pass.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://ecologic.uttics.com/api/registro";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Bundle bundlePlants = new Bundle();
                bundlePlants.putString("humidity", humidity);
                bundlePlants.putString("envHumidity", envHumidity);
                bundlePlants.putString("envTemperature", envTemperature);
                bundlePlants.putString("lastDate", lastDate);
                bundlePlants.putString("lastTime", lastTime);

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtras(bundlePlants);
                startActivity(intent);
                Toast.makeText(RegisterActivity.this, "¡Registro éxitoso!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OnErrorResponse1: ", error.toString());
            }
        });
        fRequestQueue.add(jsonObjectRequest);

    }
}

