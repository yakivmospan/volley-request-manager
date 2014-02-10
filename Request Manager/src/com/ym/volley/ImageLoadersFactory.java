package com.ym.volley;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import android.content.Context;

//Not done yet
class ImageLoadersFactory {

    public static ImageLoader getLoader(Context context, String name) {
        ImageLoader result = null;

        if (RequestOptions.DEFAULT_LOADER.equals(name)) {
            result = getDefault(context);
        }

        return result;
    }

    public static ImageLoader getDefault(Context context) {
        return newLoader(RequestQueueFactory.getDefault(context), new BitmapLruCache());
    }

    public static ImageLoader newLoader(RequestQueue queue, BitmapLruCache cache) {
        return new ImageLoader(queue, cache);
    }
}
