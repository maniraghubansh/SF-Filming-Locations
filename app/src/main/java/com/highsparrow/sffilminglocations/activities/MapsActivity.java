package com.highsparrow.sffilminglocations.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.highsparrow.sffilminglocations.R;
import com.highsparrow.sffilminglocations.models.FilmingLocation;
import com.highsparrow.sffilminglocations.models.SearchResponse;
import com.highsparrow.sffilminglocations.models.geocoding.AddressResult;
import com.highsparrow.sffilminglocations.models.geocoding.GeoCodeResult;
import com.highsparrow.sffilminglocations.networking.GsonRequest;
import com.highsparrow.sffilminglocations.networking.StringRequest;
import com.highsparrow.sffilminglocations.networking.VolleySingleton;
import com.highsparrow.sffilminglocations.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private AppCompatEditText mSearchEditText;
    private ArrayList<MarkerOptions> mMarkerOptions;
//    private SearchResponse searchResponse;
    private FilmingLocation[] filmingLocations;
    private boolean dataDownloadFinished;
    private static final int LIMIT = 10;
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchEditText = (AppCompatEditText) findViewById(R.id.edit_search_query);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupSearchEditText();
    }

    private String loadDataFromAssets() {
        String json;
        try {
            InputStream is = getAssets().open("list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.i(TAG, json);
        return json;
    }

    private void makeApiCall(String searchOver, String searchQuery) {
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll(this);
        Map<String, String> header = new HashMap<>();
        header.put(Constants.X_APP_TOKEN, Constants.SODA_API_KEY);
        Map<String, String> params = new HashMap<>();
        params.put("$limit", String.valueOf(LIMIT));
        params.put("$offset", String.valueOf(offset));
//        params.put("$order", ":id");
        if(!TextUtils.isEmpty(searchQuery)) {
            String queryParamValue = searchOver + Constants.LIKE + "%" + searchQuery + "%";
            params.put(Constants.SOQL_WHERE, queryParamValue);
        }
        String url = Constants.BASE_URL + "?$limit=1241";

        StringRequest request = new StringRequest(Request.Method.GET, url, header, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
//                        searchResponse = response;
//                        offset += LIMIT;

//                        if(response.getFilmingLocations().size()<LIMIT) {
//                            makeApiCall("", "");
//                            dataDownloadFinished = true;
//                        }
                        updateMarkers(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        request.setTag(this);
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void updateMarkers(String json) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        filmingLocations = gson.fromJson(json, FilmingLocation[].class);

//        mMarkerOptions.clear();
        for (int i = 0; i < filmingLocations.length; i++) {
            if(!TextUtils.isEmpty(filmingLocations[i].getLocations()))
                getLatLngFromMapsApi(i, filmingLocations[i].getLocations());
        }
    }

    private void getLatLngFromMapsApi(final int position, String locations) {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAM_ADDRESS, locations);
        params.put(Constants.PARAM_MAPS_API_KEY, Constants.MAPS_API_KEY);
        locations.replaceAll("\\(", "+");
        locations.replaceAll("\\)", "+");
        String url = Constants.GEOCODING_URL + "?address=" + locations.replaceAll(" ", "+") +
                "&bounds=36.8,-122.75|37.8,-121.75" + "&key=" + Constants.MAPS_GEOCODING_SERVER_KEY;

        GsonRequest<GeoCodeResult> geoCodeResultGsonRequest = new GsonRequest<>(Request.Method.GET, url, GeoCodeResult.class, headers, params, new Response.Listener<GeoCodeResult>() {
            @Override
            public void onResponse(GeoCodeResult response) {
                if(response.getAddressResults().size()>0) {
                    for (int i = 0; i < response.getAddressResults().size(); i++) {
                        AddressResult addressResult = response.getAddressResults().get(i);
                        if(addressResult.belongsToSanFrancisco()){
                            filmingLocations[position].setLatitude(addressResult.getGeometry().getLocation().getLat());
                            filmingLocations[position].setLongitude(addressResult.getGeometry().getLocation().getLng());
                            mMap.addMarker(new MarkerOptions().
                                    position(new LatLng(filmingLocations[position].getLatitude(),
                                            filmingLocations[position].getLongitude())).
                                    draggable(false).title(filmingLocations[position].getTitle()).
                                    snippet(filmingLocations[position].getDescription()));
                            break;
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
            }
        });
        VolleySingleton.getInstance(this).getRequestQueue().add(geoCodeResultGsonRequest);
    }

    private void setupSearchEditText() {
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                makeApiCall(Constants.LOCATIONS, String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sf = new LatLng(37.7749, -122.4194);
//        mMap.addMarker(new MarkerOptions().position(sf).title("Marker in San Francisco"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sf));
//        new DownloadDataTask().execute(Constants.BASE_URL);
//        updateMarkers(loadDataFromAssets());
        makeApiCall("", "");
    }

    private class DownloadDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);
            filmingLocations = new Gson().fromJson(result, FilmingLocation[].class);
            updateMarkers(result);
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 50000;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(100000 /* milliseconds */);
                conn.setConnectTimeout(150000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }

}
