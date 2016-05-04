package com.highsparrow.sffilminglocations.models.geocoding;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class Location implements Parcelable{

    private double lat;
    private double lng;

    public double getLat()
    {
        return this.lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLng()
    {
        return this.lng;
    }

    public void setLng(double lng)
    {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
    }

    protected Location(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

}
