package edu.uw.tcss450.groupchat.io;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * The Request Queue for the application's network requests.
 *
 * @version November 19, 2020
 */
public class RequestQueueSingleton {

    private static RequestQueueSingleton instance;
    private static Context context;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private RequestQueueSingleton(Context context) {
        RequestQueueSingleton.context = context;
        mRequestQueue = getmRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Return current instance of the Request Queue.
     *
     * @param context the current context of application
     * @return instance
     */
    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    /**
     * Get the Request Queue
     *
     * @return the queue
     */
    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Append to the Request queue.
     *
     * @param req request
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getmRequestQueue().add(req);
    }

    /**
     * Return the image loader.
     *
     * @return image loader
     */
    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }
}
