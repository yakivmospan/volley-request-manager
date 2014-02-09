package com.ym.volley;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import android.content.Context;

/**
 * Created by Jakob on 12.01.14
 */

//Not done yet
class ImageLoadersFabric {

    public static final int DEFAULT_DISK_USAGE_BYTES = 50 * 1024 * 1024;

    public static ImageLoader getLoader(Context context, String name) {
        ImageLoader result = null;

        if (RequestManager.LOADER_DEFAULT.equals(name)) {
            result = getDefault(context);
        }

        return result;
    }



    public static ImageLoader getDefault(Context context) {
        return newLoader(RequestQueueFabric.getDefault(context), new BitmapLruCache());
    }

    public static ImageLoader newLoader(RequestQueue queue, BitmapLruCache cache) {
        return new ImageLoader(queue, cache);
    }
}
