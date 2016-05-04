package com.highsparrow.sffilminglocations.models.geocoding;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by High Sparrow on 04-05-2016.
 */
public class AddressResult implements Parcelable{

    @SerializedName("address_components")
    private ArrayList<AddressComponent> addressComponents;

    private Geometry geometry;


    protected AddressResult(Parcel in) {
        addressComponents = in.createTypedArrayList(AddressComponent.CREATOR);
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


    public ArrayList<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(ArrayList<AddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(addressComponents);
        dest.writeParcelable(geometry, flags);
    }

    public boolean belongsToSanFrancisco() {
        for (int i = 0; i < addressComponents.size(); i++) {
            if(addressComponents.get(i).getLongName().contains("Francisco"))
                return true;
        }
        return false;
    }
}
