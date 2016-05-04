package com.highsparrow.sffilminglocations.models.geocoding;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class AddressComponent implements Parcelable{
    public static final String LOCALITY = "locality";
    public static final String ADMINISTRATIVE_AREA_2 = "administrative_area_level_2";

    @SerializedName("long_name")
    private String longName;

    @SerializedName("short__name")
    private String shortName;

    private ArrayList<String> types;


    protected AddressComponent(Parcel in) {
        longName = in.readString();
        shortName = in.readString();
        types = in.createStringArrayList();
    }

    public static final Creator<AddressComponent> CREATOR = new Creator<AddressComponent>() {
        @Override
        public AddressComponent createFromParcel(Parcel in) {
            return new AddressComponent(in);
        }

        @Override
        public AddressComponent[] newArray(int size) {
            return new AddressComponent[size];
        }
    };

    public ArrayList<String> getTypes()
    {
        return this.types;
    }

    public void setTypes(ArrayList<String> types)
    {
        this.types = types;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(longName);
        dest.writeString(shortName);
        dest.writeStringList(types);
    }

    public AddressComponent() {
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
