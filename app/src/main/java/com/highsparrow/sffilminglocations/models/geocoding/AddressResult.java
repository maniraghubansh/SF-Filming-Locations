package com.highsparrow.sffilminglocations.models.geocoding;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class AddressResult implements Parcelable{

    private Geometry geometry;

    protected AddressResult(Parcel in) {
        geometry = in.readParcelable(Geometry.class.getClassLoader());
    }

    public static final Creator<AddressResult> CREATOR = new Creator<AddressResult>() {
        @Override
        public AddressResult createFromParcel(Parcel in) {
            return new AddressResult(in);
        }

        @Override
        public AddressResult[] newArray(int size) {
            return new AddressResult[size];
        }
    };

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(geometry, flags);
    }
}
