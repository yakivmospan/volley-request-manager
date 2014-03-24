package com.ym.volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.os.Handler;
import android.os.Looper;

public abstract class RequestInterface<ResponseType, ResultType> {

    protected Handler mHandler;
    private RequestCallback<ResponseType, ResultType> mRequestCallback;
    private Response.Listener<ResponseType> mResponseListener;
    private Response.ErrorListener mErrorListener;

    public RequestInterface() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public abstract Request create();

    private Response.Listener<ResponseType> mInterfaceListener
            = new Response.Listener<ResponseType>() {
        @Override
        public void onResponse(ResponseType response) {
            if (mResponseListener != null) {
                mResponseListener.onResponse(response);
            } else if (mRequestCallback != null) {
                final ResultType resultType = mRequestCallback.doInBackground(response);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRequestCallback.onPostExecute(resultType);
                    }
                });
            }
        }
    };

    private Response.ErrorListener mInterfaceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (mErrorListener != null) {
                mErrorListener.onErrorResponse(error);
            } else if (mRequestCallback != null) {
                mRequestCallback.onError(error);
            }
        }
    };

    public final Response.Listener<ResponseType> useInterfaceListener() {
        return mInterfaceListener;
    }

    public final Response.ErrorListener useInterfaceErrorListener() {
        return mInterfaceErrorListener;
    }

    final void setRequestCallback(RequestCallback<ResponseType, ResultType> requestCallback) {
        mRequestCallback = requestCallback;
    }

    final void setResponseListener(Response.Listener<ResponseType> responseListener) {
        mResponseListener = responseListener;
    }

    final void setErrorListener(Response.ErrorListener errorListener) {
        mErrorListener = errorListener;
    }
}
