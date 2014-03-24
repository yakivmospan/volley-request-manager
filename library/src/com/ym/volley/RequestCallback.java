package com.ym.volley;

import com.android.volley.VolleyError;

public abstract class RequestCallback<ResponseType, ResultType> {

    public ResultType doInBackground(ResponseType response)
    {
        return null;
    }

    public void onPostExecute (ResultType result)
    {

    }

    public void onError(VolleyError error)
    {

    }

}
