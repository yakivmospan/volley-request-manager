package com.defaultproject;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ym.volley.RequestInterface;
import com.ym.volley.RequestObserver;

import org.json.JSONObject;

import android.net.Uri;

/**
 * Created by Jakob on 12.01.14
 */
public class TestJsonRequest extends RequestInterface<Object, JSONObject, Void> {

    public TestJsonRequest(RequestObserver<JSONObject, Void> requestObserver) {
        super(requestObserver);
    }

    @Override
    public Request create() {

        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("httpbin.org");
        uri.path("get");
        uri.appendQueryParameter("name", "Jon Doe");
        uri.appendQueryParameter("age", "21");
        String url = uri.build().toString();

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this,
                this);

        return request;
    }

}
