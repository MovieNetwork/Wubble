package com.proxima.Wubble.network;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.proxima.Wubble.AppGlobal;

import org.json.JSONObject;

import static com.proxima.Wubble.Constants.TMDB_API_KEY;

/**
 * Created by Epokhe on 08.03.2015.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance = null;

    private boolean isTransferComplete = true;

    private JSONObject mTMDBConfig;

    private RequestQueue mRequestQueue;

    private VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(AppGlobal.getAppContext());
        JsonObjectRequest configRequest = new JsonObjectRequest(Request.Method.GET, "http://api.themoviedb.org/3/configuration?api_key=" + TMDB_API_KEY, (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mTMDBConfig = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AppGlobal.getAppContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mRequestQueue.add(configRequest);
    }

    public static VolleySingleton getInstance() {
        if (sInstance == null) {
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }


    public boolean getTransferState() {
        return isTransferComplete;
    }

    public void setTransferState(boolean newState) {
        isTransferComplete = newState;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
