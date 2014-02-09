package com.defaultproject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ym.utils.L;
import com.ym.volley.RequestManager;
import com.ym.volley.RequestObserver;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity
        extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_main);

        RequestManager
                .initializeWith(this)
                .usingBackgroundQueue()
                .addRequest(new TestJsonRequest(mRequestObserver))
                .start();

        RequestManager
                .queue()
                .usingBackgroundQueue()
                .addRequest(new TestRequest(mListener, mErrorListener))
                .start();
    }

    private RequestObserver mRequestObserver = new RequestObserver<JSONObject, Void>() {
        @Override
        public Void doInBackground(JSONObject response) {
            L.e(response.toString());
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Toast from UI", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(VolleyError error) {
            L.e(error.toString());
        }
    };

    private Response.Listener mListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String o) {

        }
    };

    private Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    };
}
