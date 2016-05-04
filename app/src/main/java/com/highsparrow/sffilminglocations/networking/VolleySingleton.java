package com.highsparrow.sffilminglocations.networking;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by High Sparrow on 03-05-2016.
 */
public class VolleySingleton {

    private static VolleySingleton mInstance;
    private static RequestQueue mRequestQueue;
    private static Context mContext;

    public VolleySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new OkHttpClient());
        return mRequestQueue;
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if(mInstance == null)
            mInstance = new VolleySingleton(context);
        return mInstance;
    }
}
