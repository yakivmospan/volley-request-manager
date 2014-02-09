package com.ym.volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Yakiv M. on 12.01.14
 */

public abstract class RequestInterface<DataObject, ResponseType, ResultType>
        implements Response.Listener<ResponseType>, Response.ErrorListener {

    protected Handler mHandler;

    protected DataObject mDataObject;

    private RequestObserver<ResponseType, ResultType> mRequestObserver;

    private Response.Listener<ResponseType> mResponseListener;
    private Response.ErrorListener mErrorListener;

    public abstract Request create();

    public RequestInterface() {
    }

    public RequestInterface(DataObject dataObject) {
        mDataObject = dataObject;
    }

    public RequestInterface(RequestObserver<ResponseType, ResultType> requestObserver) {
        mHandler = new Handler(Looper.getMainLooper());
        mRequestObserver = requestObserver;
    }

    public RequestInterface(DataObject dataObject,
            RequestObserver<ResponseType, ResultType> requestObserver) {
        this(requestObserver);
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
        } else if (mRequestObserver != null) {
            final ResultType resultType = mRequestObserver.doInBackground(response);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mRequestObserver.onPostExecute(resultType);
                }
            });
        }
    }

    @Override
    public final void onErrorResponse(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        } else if (mRequestObserver != null) {
            mRequestObserver.onError(error);
        }
    }
}
