package com.ym.volley;

import com.android.volley.VolleyError;

/**
 * Created by Yakiv M. on 04.02.14
 */

public interface RequestObserver<ResponseType, ResultType> {

    public ResultType doInBackground(ResponseType response);

    public void onPostExecute(ResultType result);

    public void onError(VolleyError error);

}
