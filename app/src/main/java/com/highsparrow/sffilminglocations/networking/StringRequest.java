package com.highsparrow.sffilminglocations.networking;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class StringRequest extends Request<String> {
    private static final String TAG = StringRequest.class.getSimpleName();
    private final Response.Listener<String> mListener;
    private final Map<String, String> mHttpHeaders;
    private final Map<String, String> mQueryParams;
    private final Response.ErrorListener mErrorListener;

    public StringRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(method, url, errorListener);
        Log.d(TAG, url);
        mHttpHeaders = headers;
        mQueryParams = params;
        mListener = listener;
        mErrorListener = errorListener;
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mQueryParams != null ? mQueryParams : super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHttpHeaders != null ? mHttpHeaders : super.getHeaders();
    }
}
