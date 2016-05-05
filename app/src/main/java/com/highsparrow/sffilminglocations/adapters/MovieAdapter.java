package com.highsparrow.sffilminglocations.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highsparrow.sffilminglocations.R;
import com.highsparrow.sffilminglocations.models.FilmingLocation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by High Sparrow on 05-05-2016.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<FilmingLocation> mFilmingLocations;
    private Context mContext;

    public MovieAdapter(Context context, ArrayList<FilmingLocation> filmingLocations){
        this.mContext = context;
        this.mFilmingLocations = filmingLocations;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_info_window, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, int position) {
        Picasso.with(mContext).load(mFilmingLocations.get(position).getPosterUrl()).into(holder.imageView);
        holder.detailsView.setText(String.format("%s : %s", mFilmingLocations.get(position).getTitle(), mFilmingLocations.get(position).getDescription()));
    }

    @Override
    public int getItemCount() {
        return mFilmingLocations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatImageView imageView;
        public AppCompatTextView detailsView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.image_movie_poster);
            detailsView = (AppCompatTextView) itemView.findViewById(R.id.text_description);
        }
    }
}
