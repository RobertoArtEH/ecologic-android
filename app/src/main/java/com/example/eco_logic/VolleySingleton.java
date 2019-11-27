package com.example.eco_logic;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mVolleySingleton = null;
    private RequestQueue mRequestQueue;

    private VolleySingleton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static VolleySingleton getInstance(Context context) {
        if(mVolleySingleton == null) {
            mVolleySingleton = new VolleySingleton(context);
        }

        return mVolleySingleton;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
