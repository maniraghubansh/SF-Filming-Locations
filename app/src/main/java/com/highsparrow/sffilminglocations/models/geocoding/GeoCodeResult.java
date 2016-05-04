package com.highsparrow.sffilminglocations.models.geocoding;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class GeoCodeResult implements Parcelable{

    private String status;

    @SerializedName("results")
    private ArrayList<AddressResult> addressResults;

    protected GeoCodeResult(Parcel in) {
        status = in.readString();
        addressResults = in.createTypedArrayList(AddressResult.CREATOR);
    }

    public static final Creator<GeoCodeResult> CREATOR = new Creator<GeoCodeResult>() {
        @Override
        public GeoCodeResult createFromParcel(Parcel in) {
            return new GeoCodeResult(in);
        }

        @Override
        public GeoCodeResult[] newArray(int size) {
            return new GeoCodeResult[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<AddressResult> getAddressResults() {
        return addressResults;
    }

    public void setAddressResults(ArrayList<AddressResult> addressResults) {
        this.addressResults = addressResults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeTypedList(addressResults);
    }
}
