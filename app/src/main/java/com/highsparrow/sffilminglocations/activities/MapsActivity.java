package com.highsparrow.sffilminglocations.activities;

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
import com.highsparrow.sffilminglocations.R;
import com.highsparrow.sffilminglocations.models.SearchResponse;
import com.highsparrow.sffilminglocations.models.geocoding.GeoCodeResult;
import com.highsparrow.sffilminglocations.networking.GsonRequest;
import com.highsparrow.sffilminglocations.networking.VolleySingleton;
import com.highsparrow.sffilminglocations.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private AppCompatEditText mSearchEditText;
    private ArrayList<MarkerOptions> mMarkerOptions;
    private SearchResponse searchResponse;
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
//        setupAutoCompleteFragment();
        setupSearchEditText();
        makeApiCall("", "");
    }

    private void makeApiCall(String searchOver, String searchQuery) {
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll(this);
        Map<String, String> header = new HashMap<>();
        header.put(Constants.X_APP_TOKEN, Constants.SODA_API_KEY);
        Map<String, String> params = new HashMap<>();
        params.put("$limit", String.valueOf(LIMIT));
        params.put("$offset", String.valueOf(offset));
        params.put("$order", ":id");
        if(!TextUtils.isEmpty(searchQuery)) {
            String queryParamValue = searchOver + Constants.LIKE + "%" + searchQuery + "%";
            params.put(Constants.SOQL_WHERE, queryParamValue);
        }
        GsonRequest<SearchResponse> gsonRequest = new GsonRequest<>(Request.Method.GET, Constants.BASE_URL, SearchResponse.class, header, params,
                new Response.Listener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse response) {
                        searchResponse = response;
                        offset += LIMIT;
                        if(response.getFilmingLocations().size()<LIMIT) {
                            makeApiCall("", "");
                            dataDownloadFinished = true;
                        }
                        updateMarkers();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        gsonRequest.setTag(this);
        VolleySingleton.getInstance(this).getRequestQueue().add(gsonRequest);
    }

    private void updateMarkers() {
        mMarkerOptions.clear();
        for (int i = 0; i < searchResponse.getFilmingLocations().size(); i++) {
            getLatLngFromMapsApi(i, searchResponse.getFilmingLocations().get(i).getLocations());
        }
    }

    private void getLatLngFromMapsApi(final int position, String locations) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAM_ADDRESS, locations);
        params.put(Constants.PARAM_MAPS_API_KEY, Constants.MAPS_API_KEY);
        GsonRequest<GeoCodeResult> geoCodeResultGsonRequest = new GsonRequest<>(Request.Method.GET, Constants.GEOCODING_URL, GeoCodeResult.class, null, params, new Response.Listener<GeoCodeResult>() {
            @Override
            public void onResponse(GeoCodeResult response) {
                searchResponse.getFilmingLocations().get(position).setLatitude(response.getAddressResults().get(0).getGeometry().getLocation().getLat());
                searchResponse.getFilmingLocations().get(position).setLongitude(response.getAddressResults().get(0).getGeometry().getLocation().getLng());
                mMap.addMarker(new MarkerOptions().
                        position(new LatLng(searchResponse.getFilmingLocations().get(position).getLatitude(),
                                searchResponse.getFilmingLocations().get(position).getLongitude())).
                        draggable(false).title(searchResponse.getFilmingLocations().get(position).getTitle()).
                        snippet(searchResponse.getFilmingLocations().get(position).getDescription()));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(this).getRequestQueue().add(geoCodeResultGsonRequest);
    }

//    private void setupAutoCompleteFragment() {
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });
//    }

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
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
