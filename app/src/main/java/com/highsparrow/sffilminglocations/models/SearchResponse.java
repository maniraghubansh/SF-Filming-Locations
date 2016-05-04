package com.highsparrow.sffilminglocations.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by High Sparrow on 03-05-2016.
 */
public class SearchResponse implements Parcelable{
    private ArrayList<FilmingLocation> filmingLocations;

    protected SearchResponse(Parcel in) {
        filmingLocations = in.createTypedArrayList(FilmingLocation.CREATOR);
    }

    public static final Creator<SearchResponse> CREATOR = new Creator<SearchResponse>() {
        @Override
        public SearchResponse createFromParcel(Parcel in) {
            return new SearchResponse(in);
        }

        @Override
        public SearchResponse[] newArray(int size) {
            return new SearchResponse[size];
        }
    };

    public ArrayList<FilmingLocation> getFilmingLocations() {
        return filmingLocations;
    }

    public void setFilmingLocations(ArrayList<FilmingLocation> filmingLocations) {
        this.filmingLocations = filmingLocations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(filmingLocations);
    }
}
