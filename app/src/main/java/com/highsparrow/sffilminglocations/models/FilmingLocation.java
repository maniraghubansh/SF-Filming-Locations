package com.highsparrow.sffilminglocations.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by High Sparrow on 03-05-2016.
 */
public class FilmingLocation implements Parcelable{

    private double latitude;

    private double longitude;

    @SerializedName("actor_1")
    private String firstActor;

    @SerializedName("actor_2")
    private String secondActor;

    @SerializedName("actor_3")
    private String thirdActor;

    private String director;

    private String distributor;

    @SerializedName("fun_facts")
    private String funFacts;

    private String locations;

    @SerializedName("production_company")
    private String productionCompany;

    @SerializedName("release_year")
    private String releaseYear;

    private String title;

    private String writer;

    public String getDescription(){
        String actors = " Starring ";
        if(TextUtils.isEmpty(secondActor) && TextUtils.isEmpty(thirdActor))
            actors = actors + firstActor;
        else if(TextUtils.isEmpty(thirdActor))
            actors = actors + firstActor + "and " + secondActor;
        else if(TextUtils.isEmpty(secondActor))
            actors = actors + firstActor + "and " + thirdActor;
        else
            actors = actors + firstActor + ", " + secondActor + "and " + thirdActor;

        String description = "Directed by " + director + ", released in " + releaseYear + "." + actors + ".";
        if(!TextUtils.isEmpty(funFacts))
            description += funFacts + ".";
        return description;
    }

    public String getFirstActor() {
        return firstActor;
    }

    public void setFirstActor(String firstActor) {
        this.firstActor = firstActor;
    }

    public String getSecondActor() {
        return secondActor;
    }

    public void setSecondActor(String secondActor) {
        this.secondActor = secondActor;
    }

    public String getThirdActor() {
        return thirdActor;
    }

    public void setThirdActor(String thirdActor) {
        this.thirdActor = thirdActor;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getProductionCompany() {
        return productionCompany;
    }

    public void setProductionCompany(String productionCompany) {
        this.productionCompany = productionCompany;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public static final Creator<FilmingLocation> CREATOR = new Creator<FilmingLocation>() {
        @Override
        public FilmingLocation createFromParcel(Parcel in) {
            return new FilmingLocation(in);
        }

        @Override
        public FilmingLocation[] newArray(int size) {
            return new FilmingLocation[size];
        }
    };


    public FilmingLocation(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getFunFacts() {
        return funFacts;
    }

    public void setFunFacts(String funFacts) {
        this.funFacts = funFacts;
    }
}
