package com.highsparrow.sffilminglocations.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.highsparrow.sffilminglocations.R;
import com.highsparrow.sffilminglocations.adapters.SearchAdapter;
import com.highsparrow.sffilminglocations.models.FilmingLocation;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private AppCompatEditText mSearchEditText;
    private RecyclerView mRecyclerView;
    private ArrayList<MarkerOptions> mMarkerOptions;
//    private SearchResponse searchResponse;
    private ArrayList<FilmingLocation> mFilmingLocations;
    private static final int LIMIT = 10;
    private String mCurrentSearchFilter;
    private String[] mSearchFilters = new String[]{"Movie", "Location", "Director"};
    private Map<String, String> mQueryFilterMap = new HashMap<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupSearchEditText();
        setupSpinner();
    }

    private void setupSearchEditText() {
        mQueryFilterMap.put(mSearchFilters[0], Constants.TITLE);
        mQueryFilterMap.put(mSearchFilters[1], Constants.LOCATIONS);
        mQueryFilterMap.put(mSearchFilters[2], Constants.DIRECTOR);
        mSearchEditText = (AppCompatEditText) findViewById(R.id.edit_search_query);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                makeApiCall(mQueryFilterMap.get(mCurrentSearchFilter), String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchAdapter adapter = new SearchAdapter(this, new ArrayList<FilmingLocation>());
        mRecyclerView.setAdapter(adapter);
    }

    private void setupSpinner() {
        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, mSearchFilters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentSearchFilter = mSearchFilters[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        mMap.clear();
        Map<String, String> header = new HashMap<>();
        header.put(Constants.X_APP_TOKEN, Constants.SODA_API_KEY);
        Map<String, String> params = new HashMap<>();
        Uri  uri;
        if(!TextUtils.isEmpty(searchQuery)) {
            String queryParamValue = searchOver + " " + Constants.LIKE + " '%" + searchQuery + "%'";
//            url = url + "?$where=" + queryParamValue;
            params.put(Constants.SOQL_WHERE, queryParamValue);
            uri = Uri.parse(Constants.BASE_URL).buildUpon().appendQueryParameter(Constants.SOQL_WHERE, params.get(Constants.SOQL_WHERE)).build();

        } else {
            params.put("$limit", "1241");
            uri = Uri.parse(Constants.BASE_URL).buildUpon().appendQueryParameter("$limit", "1241").build();
//            url = url + "?$limit=1241";
        }

        StringRequest request = new StringRequest(Request.Method.GET, uri.toString(), header, params,
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
        FilmingLocation[] searchResults = gson.fromJson(json, FilmingLocation[].class);
        mFilmingLocations = new ArrayList<>(Arrays.asList(searchResults));
//        mMarkerOptions.clear();
        for (int i = 0; i < searchResults.length; i++) {
            if(!TextUtils.isEmpty(searchResults[i].getLocations()))
                getLatLngFromMapsApi(i, searchResults[i].getLocations());
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
                            mFilmingLocations.get(position).setLatitude(addressResult.getGeometry().getLocation().getLat());
                            mFilmingLocations.get(position).setLongitude(addressResult.getGeometry().getLocation().getLng());
                            mMap.addMarker(new MarkerOptions().
                                    position(new LatLng(mFilmingLocations.get(position).getLatitude(),
                                            mFilmingLocations.get(position).getLongitude())).
                                    draggable(false).title(mFilmingLocations.get(position).getTitle()).
                                    snippet(mFilmingLocations.get(position).getDescription()));
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
        geoCodeResultGsonRequest.setTag(this);
        VolleySingleton.getInstance(this).getRequestQueue().add(geoCodeResultGsonRequest);
    }

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
//            mFilmingLocations = new Gson().fromJson(result, FilmingLocation[].class);
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
