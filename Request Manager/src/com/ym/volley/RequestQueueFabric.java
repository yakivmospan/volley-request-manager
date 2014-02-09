package com.ym.volley;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * Created by Jakob on 12.01.14
 */

public class RequestQueueFabric {

    public static final String REQUEST_CACHE_PATH = "com/ym/volley/request";
    public static final int DEFAULT_POOL_SIZE = 4;

    public static RequestQueue getQueue(Context context, String name) {
        RequestQueue result = null;

        if (RequestManager.QUEUE_DEFAULT.equals(name)) {
            result = getDefault(context);
        } if (RequestManager.QUEUE_BACKGROUND.equals(name)) {
            result = newBackgroundQueue(context);
        }

        return result;
    }

    public static RequestQueue getDefault(Context context) {
        return Volley.newRequestQueue(context.getApplicationContext());
    }

    public static RequestQueue newBackgroundQueue(Context context) {
        return newBackgroundQueue(context, null, DEFAULT_POOL_SIZE);
    }

    public static RequestQueue newBackgroundQueue(Context context, HttpStack stack, int threadPoolSize) {
        File cacheDir = new File(context.getCacheDir(), REQUEST_CACHE_PATH);

        String userAgent = "com/ym/volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        // important part
//        int threadPoolSize = 10; // number of network dispatcher threads to create
        // pass Executor to constructor of ResponseDelivery object
        ResponseDelivery delivery = new ExecutorDelivery(
                Executors.newFixedThreadPool(threadPoolSize));

        Network network = new BasicNetwork(stack);

        // pass ResponseDelivery object as a 4th parameter for RequestQueue constructor
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, threadPoolSize,
                delivery);
        queue.start();

        return queue;
    }
}
