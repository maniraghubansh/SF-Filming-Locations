package com.highsparrow.sffilminglocations.networking;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkUrlFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class OkHttpClient extends HurlStack {

    private final OkUrlFactory mFactory;
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    public OkHttpClient() {
        this(new com.squareup.okhttp.OkHttpClient());
    }

    public OkHttpClient(com.squareup.okhttp.OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client must not be null.");
        }
//        client.interceptors().add(new LoggingInterceptor());
        mFactory = new OkUrlFactory(client);
    }

    @Override protected HttpURLConnection createConnection(URL url) throws IOException {
        return mFactory.open(url);
    }

    private static void addBodyIfExists(HttpURLConnection connection, Request<?> request)
            throws IOException, AuthFailureError {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }

}
