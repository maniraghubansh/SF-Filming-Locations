package com.highsparrow.sffilminglocations.networking;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by High Sparrow on 03-05-2016.
 */
public class GsonRequest<T> extends Request<T> {

    private static final String TAG = GsonRequest.class.getSimpleName();
    private final Gson mGson = new Gson();
    private final Class<T> mClazz;
    private final Response.Listener<T> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHttpHeaders;
    private final Map<String, String> mQueryParams;


    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers, Map<String, String> params, Response.Listener<T> listener, Response.ErrorListener errorListener){
        super(method, url, errorListener);
        Log.d(TAG, url);
        mClazz = clazz;
        mHttpHeaders = headers;
        mQueryParams = params;
        mListener = listener;
        mErrorListener = errorListener;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(mHttpHeaders, "UTF-8"));
            Log.d(TAG, json);
            return Response.success(mGson.fromJson(json, mClazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e){
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
