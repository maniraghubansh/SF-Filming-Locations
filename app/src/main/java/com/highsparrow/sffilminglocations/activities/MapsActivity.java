package com.highsparrow.sffilminglocations.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.highsparrow.sffilminglocations.R;
import com.highsparrow.sffilminglocations.adapters.CustomInfoWindowAdapter;
import com.highsparrow.sffilminglocations.adapters.SearchAdapter;
import com.highsparrow.sffilminglocations.database.DBOpenHelper;
import com.highsparrow.sffilminglocations.database.DBQueryHelper;
import com.highsparrow.sffilminglocations.database.SFFilmingLocationsContract;
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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private static final String DATA_DOWNLOADED = "data_downloaded";
    private GoogleMap mMap;
    private ArrayList<FilmingLocation> mFilmingLocations = new ArrayList<>();
    private String mCurrentSearchFilter;
    private String[] mSearchFilters = new String[]{"Movie", "Location", "Director"};
    private Map<String, String> mQueryFilterMap = new HashMap<>(3);
    private AppCompatEditText mSearchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupSearchEditText();
        setupSpinner();
    }

    private void downloadData() {
        mProgressDialog = ProgressDialog.show(this, "Downloading Data", "Please wait while the data is downloaded", true, false);
        Map<String, String> header = new HashMap<>();
        header.put(Constants.X_APP_TOKEN, Constants.SODA_API_KEY);
        Map<String, String> params = new HashMap<>();
        Uri  uri = Uri.parse(Constants.BASE_URL).buildUpon().appendQueryParameter("$limit", "1241").build();

        StringRequest request = new StringRequest(Request.Method.GET, uri.toString(), header, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                        mProgressDialog.dismiss();
                        saveDataInDB(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        request.setTag(this);
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void saveDataInDB(String response) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        FilmingLocation[] searchResults = gson.fromJson(response, FilmingLocation[].class);
        mFilmingLocations = new ArrayList<>(Arrays.asList(searchResults));
        for (int i = 0; i < mFilmingLocations.size(); i++) {
            getLatLngFromMapsApi(mFilmingLocations.get(i));
        }
        ContentValues[] values = new ContentValues[mFilmingLocations.size()];
        for (int i = 0; i < mFilmingLocations.size(); i++) {
            ContentValues value = DBQueryHelper.putDataInContentValues(mFilmingLocations.get(i));
            values[i] = value;
        }
        if(getContentResolver().bulkInsert(SFFilmingLocationsContract.FilmingLocationEntry.CONTENT_URI, values)>0)
            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putBoolean(DATA_DOWNLOADED, true).commit();
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
                if(count>Constants.SEARCH_QUERY_MIN_LENGTH) {   // string length should be at least 3 for a meaningful search
                    searchInDb(mQueryFilterMap.get(mCurrentSearchFilter), String.valueOf(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        SearchAdapter adapter = new SearchAdapter(this, new ArrayList<FilmingLocation>());
//        mRecyclerView.setAdapter(adapter);
    }
    private void setupSpinner() {
        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.item_spinner, mSearchFilters);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentSearchFilter = mSearchFilters[position];
                mSearchEditText.setHint("Search a " + mCurrentSearchFilter.toLowerCase());          // change the hint showing in the edittext
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mSearchEditText.setShowSoftInputOnFocus(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void searchInDb(String searchOver, String searchQuery) {
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll(this);
        mFilmingLocations.clear();
        mMap.clear();
        Cursor cursor;
        if(TextUtils.isEmpty(searchQuery)){                                                         // show all locations
            cursor = getContentResolver().query(SFFilmingLocationsContract.FilmingLocationEntry.CONTENT_URI, null, null, null, null);
        } else {
            cursor = new DBOpenHelper(this).getReadableDatabase().query(SFFilmingLocationsContract.FilmingLocationEntry.TABLE_NAME, null, searchOver + " LIKE ?",
                    new String[]{"%" + searchQuery + "%"}, null, null, null, null);
        }
        while (cursor.moveToNext()){
            FilmingLocation filmingLocation = DBQueryHelper.populateFilmingLocationFromCursor(cursor);
            mFilmingLocations.add(filmingLocation);
        }
        for (int i = 0; i < mFilmingLocations.size(); i++){
            if( mFilmingLocations.get(i).getLatitude()==0.0 || mFilmingLocations.get(i).getLongitude()==0.0)
                getLatLngFromMapsApi(mFilmingLocations.get(i));
            else
                addMarkerOnMap(mFilmingLocations.get(i));
        }
    }

    private void getLatLngFromMapsApi(final FilmingLocation filmingLocation) {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        String locations = filmingLocation.getLocations();
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
                            filmingLocation.setLatitude(addressResult.getGeometry().getLocation().getLat());
                            filmingLocation.setLongitude(addressResult.getGeometry().getLocation().getLng());
                            addMarkerOnMap(filmingLocation);
                            updateDataInDb(filmingLocation);
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

    private void updateDataInDb(FilmingLocation filmingLocation) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SFFilmingLocationsContract.FilmingLocationEntry.LATITUDE, filmingLocation.getLatitude());
        contentValues.put(SFFilmingLocationsContract.FilmingLocationEntry.LONGITUDE, filmingLocation.getLongitude());
        getContentResolver().update(SFFilmingLocationsContract.FilmingLocationEntry.CONTENT_URI, contentValues,
                SFFilmingLocationsContract.FilmingLocationEntry.LOCATIONS + " = ?", new String[]{filmingLocation.getLocations()});
    }


    private void addMarkerOnMap(FilmingLocation filmingLocation) {
        mMap.addMarker(new MarkerOptions().
                position(new LatLng(filmingLocation.getLatitude() + (Math.random()-0.5)/2000, filmingLocation.getLongitude() + (Math.random()-0.5)/2000)).
                draggable(false).title(filmingLocation.getTitle()).
                snippet(filmingLocation.getDescription()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        LatLng sf = new LatLng(37.7749, -122.4194);
        LatLng sfSouthWest = new LatLng(37.5424472,-122.5698084);
        LatLng sfNorthEast = new LatLng(37.8528105,-122.3906773);
        try{
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(sfSouthWest, sfNorthEast), 0));
        } catch (IllegalStateException e){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sf));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            Log.e(TAG, e.toString());
        }

        if(!getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(DATA_DOWNLOADED, false))
            downloadData();
        else
            searchInDb("", "");
    }
}
