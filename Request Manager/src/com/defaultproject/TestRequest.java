package com.defaultproject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ym.volley.RequestInterface;
import com.ym.volley.RequestObserver;

import android.net.Uri;

/**
 * Created by Jakob on 12.01.14
 */
public class TestRequest extends RequestInterface<Object, String, Void> {


    public TestRequest(Response.Listener<String> responseListener,
            Response.ErrorListener errorListener) {
        super(responseListener, errorListener);
    }
    @Override
    public Request create() {

        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("httpbin.org");
        uri.path("delay/5");
        String url = uri.build().toString();

        Request request = new StringRequest(
                Request.Method.POST,
                url,
                this,
                this);

        return request;
    }

}
