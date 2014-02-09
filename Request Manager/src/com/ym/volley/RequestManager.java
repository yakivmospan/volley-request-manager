package com.ym.volley;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yakiv M. on 26.01.14
 */

public class RequestManager {

    public static final String QUEUE_DEFAULT = "com.ym.volley.QUEUE_DEFAULT";
    public static final String QUEUE_BACKGROUND = "com.ym.volley.QUEUE_BACKGROUND";

    public static final String LOADER_DEFAULT = "com.ym.volley.LOADER_DEFAULT";

    private static RequestManager instance;

    private RequestBuilder mRequestBuilder;
    private ImageLoaderBuilder mImageLoaderBuilder;

    private RequestManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        mRequestBuilder = new RequestBuilder(applicationContext);
        mImageLoaderBuilder = new ImageLoaderBuilder(applicationContext);
    }

    public static synchronized QueueBuilder initializeWith(Context context) {
        if (instance == null) {
            instance = new RequestManager(context);
        }

        return instance.getRequestBuilder().mQueueBuilder;
    }

    public static synchronized QueueBuilder queue() {
        if (instance == null) {
            throw new IllegalStateException(RequestManager.class.getSimpleName() +
                    " is not initialized, call initializeWith(..) method first.");
        }
        return instance.getRequestBuilder().mQueueBuilder;
    }

    //Not done yet
    private static synchronized ImageQueueBuilder loader() {
        if (instance == null) {
            throw new IllegalStateException(RequestManager.class.getSimpleName() +
                    " is not initialized, call initializeWith(..) method first.");
        }
        return instance.getImageLoaderBuilder().mImageQueueBuilder;
    }

    private RequestBuilder getRequestBuilder() {
        return mRequestBuilder;
    }
    private ImageLoaderBuilder getImageLoaderBuilder() {
        return mImageLoaderBuilder;
    }

    public class RequestBuilder {

        private QueueBuilder mQueueBuilder;

        public RequestBuilder(Context context) {
            this.mQueueBuilder = new QueueBuilder(context);
        }

        public RequestBuilder addRequest(RequestInterface volleyRequest) {
            mQueueBuilder.getRequestQueue().add(volleyRequest.create());
            return this;
        }

        public void start() {
            this.mQueueBuilder.getRequestQueue().start();
        }

        public void stop() {
            this.mQueueBuilder.getRequestQueue().stop();
        }

        public void cancelAll(Object tag) {
            this.mQueueBuilder.getRequestQueue().cancelAll(tag);
        }

        public void cancelAll(RequestQueue.RequestFilter requestFilter) {
            this.mQueueBuilder.getRequestQueue().cancelAll(requestFilter);
        }
    }


    public class ImageLoaderBuilder {

        private ImageQueueBuilder mImageQueueBuilder;

        public ImageLoaderBuilder(Context context) {
            this.mImageQueueBuilder = new ImageQueueBuilder(context);
        }
    }

    public class QueueBuilder {

        private Context mContext;

        private Map<String, RequestQueue> mRequestQueue = new HashMap<String, RequestQueue>();

        private String mCurQueue;

        public QueueBuilder(Context context) {
            this.mContext = context;
        }

        public RequestBuilder using(String queueName) {
            validateQueue(queueName);
            mCurQueue = queueName;
            return mRequestBuilder;
        }

        public RequestBuilder usingDefaultQueue() {
            return using(RequestManager.QUEUE_DEFAULT);
        }

        public RequestBuilder usingBackgroundQueue() {
            return using(RequestManager.QUEUE_BACKGROUND);
        }

        public void register(String queueName, RequestQueue requestQueue) {
            if (this.mRequestQueue.containsKey(queueName)) {
                throw new IllegalArgumentException(
                        "RequestQueue - \"" + queueName + "\" already exists!");
            }
            this.mRequestQueue.put(queueName, requestQueue);
        }

        private void validateQueue(String queueName) {
            if (!this.mRequestQueue.containsKey(queueName)) {
                final RequestQueue queue = RequestQueueFabric.getQueue(mContext, queueName);
                if (queue != null) {
                    this.mRequestQueue.put(queueName, queue);
                } else {
                    throw new IllegalArgumentException(
                            "RequestQueue - \"" + queueName + "\" doesn't exists!");
                }
            }
        }

        private RequestQueue getRequestQueue() {
            RequestQueue result = mRequestQueue.get(mCurQueue);
            return result;
        }
    }

    public class ImageQueueBuilder {

        private Context mContext;

        private Map<String, ImageLoader> mLoaders = new HashMap<String, ImageLoader>();
        private Map<String, BitmapLruCache> mCaches = new HashMap<String, BitmapLruCache>();

        private String mCurLoader;

        public ImageQueueBuilder(Context context) {
            this.mContext = context;
        }

        public ImageLoaderBuilder loadUsing(String loaderName) {
            mCurLoader = loaderName;
            return mImageLoaderBuilder;
        }

        public void register(String loaderName, ImageLoader imageLoader) {
            if (this.mLoaders.containsKey(loaderName)) {
                throw new IllegalArgumentException(
                        "ImageLoader - \"" + loaderName + "\" already exists!");
            }

            this.mLoaders.put(loaderName, imageLoader);
        }

        public void register(String loaderName, RequestQueue queue, BitmapLruCache bitmapLruCache) {
            if (this.mLoaders.containsKey(loaderName)) {
                throw new IllegalArgumentException(
                        "ImageLoader - \"" + loaderName + "\" already exists!");
            }

            this.mCaches.put(loaderName, bitmapLruCache);
            this.mLoaders.put(loaderName, new ImageLoader(queue, bitmapLruCache));
        }

//        private void validateQueue(String loader) {
//            if (!this.mLoaders.containsKey(loader)) {
//                final ImageLoader imageLoader = ImageLoadersFabric.getLoader(mContext, loader);
//                if (imageLoader != null) {
//                    this.mRequestQueue.put(queueName, queue);
//                } else {
//                    throw new IllegalArgumentException(
//                            "ImageLoader - \"" + loader + "\" doesn't exists!");
//                }
//            }
//        }
    }
}