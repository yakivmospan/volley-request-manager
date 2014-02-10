package com.ym.volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.os.Handler;
import android.os.Looper;

public abstract class RequestInterface<DataObject, ResponseType, ResultType>
        implements Response.Listener<ResponseType>, Response.ErrorListener {

    protected Handler mHandler;

    protected DataObject mDataObject;

    private RequestCallback<ResponseType, ResultType> mRequestCallback;

    private Response.Listener<ResponseType> mResponseListener;
    private Response.ErrorListener mErrorListener;

    public abstract Request create();

    public RequestInterface() {
    }

    public RequestInterface(DataObject dataObject) {
        mDataObject = dataObject;
    }

    public RequestInterface(RequestCallback<ResponseType, ResultType> requestCallback) {
        mHandler = new Handler(Looper.getMainLooper());
        mRequestCallback = requestCallback;
    }

    public RequestInterface(DataObject dataObject,
            RequestCallback<ResponseType, ResultType> requestCallback) {
        this(requestCallback);
        mDataObject = dataObject;
    }

    public RequestInterface(Response.Listener<ResponseType> responseListener,
            Response.ErrorListener errorListener) {
        mResponseListener = responseListener;
        mErrorListener = errorListener;
    }

    public RequestInterface(DataObject dataObject, Response.Listener<ResponseType> responseListener,
            Response.ErrorListener errorListener) {
        mDataObject = dataObject;
        mResponseListener = responseListener;
        mErrorListener = errorListener;
    }

    @Override
    public final void onResponse(ResponseType response) {
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

    @Override
    public final void onErrorResponse(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        } else if (mRequestCallback != null) {
            mRequestCallback.onError(error);
        }
    }
}
