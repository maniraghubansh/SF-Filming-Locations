package com.highsparrow.sffilminglocations.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.highsparrow.sffilminglocations.models.FilmingLocation;

import java.util.ArrayList;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<FilmingLocation> mFilmingLocations;

    public SearchAdapter(Context context, ArrayList<FilmingLocation> filmingLocations){
        this.mContext = context;
        this.mFilmingLocations = filmingLocations;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void updateAdapter(ArrayList<FilmingLocation> filmingLocations){
        this.mFilmingLocations = filmingLocations;
        notifyDataSetChanged();
    }
}
