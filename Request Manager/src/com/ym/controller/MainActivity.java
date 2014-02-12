package com.ym.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.defaultproject.R;
import com.ym.model.request.TestJsonRequest;
import com.ym.model.request.TestRequest;
import com.ym.utils.L;
import com.ym.volley.RequestCallback;
import com.ym.volley.RequestManager;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity
        extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_main);

        RequestManager.initializeWith(getApplicationContext());

        //Queue use custom listener
        RequestManager.queue()
                .useBackgroundQueue()
                .addRequest(new TestJsonRequest(mRequestCallback))
                .start();

        //Queue use default volley Response and Error listener
        RequestManager
                .queue()
                .useBackgroundQueue()
                .addRequest(new TestJsonRequest(mListener, mErrorListener))
                .start();

        RequestManager
                .loader()
                .useDefaultLoader()
                .obtain()
                .get(
                        "http://farm6.staticflickr.com/5475/10375875123_75ce3080c6_b.jpg",
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response,
                                    boolean isImmediate) {

                                Toast.makeText(getApplicationContext(), "Bitmap loaded", Toast.LENGTH_SHORT).show();

                                BitmapDrawable bitmapDrawable = new BitmapDrawable(
                                        response.getBitmap());
                                findViewById(R.id.txtHello).setBackground(bitmapDrawable);
                            }
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                L.e(error.toString());
                            }
                        }
                );
    }

    private RequestCallback mRequestCallback = new RequestCallback<JSONObject, Void>() {
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

    private Response.Listener mListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject o) {

        }
    };

    private Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    };
}
