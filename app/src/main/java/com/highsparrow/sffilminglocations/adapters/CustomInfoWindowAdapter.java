package com.highsparrow.sffilminglocations.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.highsparrow.sffilminglocations.R;
import com.highsparrow.sffilminglocations.models.FilmingLocation;

import java.util.ArrayList;

/**
 * Created by High Sparrow on 05-05-2016.
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private ArrayList<FilmingLocation> mFilmingLocations;
    private Context mContext;

    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.info_window, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        MovieAdapter movieAdapter = new MovieAdapter(mContext, mFilmingLocations);
        recyclerView.setAdapter(movieAdapter);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
