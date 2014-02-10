package com.ym.model.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ym.volley.RequestInterface;

import android.net.Uri;

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
