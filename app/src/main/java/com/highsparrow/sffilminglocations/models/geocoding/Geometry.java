package com.highsparrow.sffilminglocations.models.geocoding;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class Geometry implements Parcelable {

    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Geometry() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.location, 0);
    }

    protected Geometry(Parcel in) {
        this.location = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<Geometry> CREATOR = new Parcelable.Creator<Geometry>() {
        public Geometry createFromParcel(Parcel source) {
            return new Geometry(source);
        }

        public Geometry[] newArray(int size) {
            return new Geometry[size];
        }
    };
}
