package com.superfamous.superphotopicker.loader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by donglua on 15/6/30.
 */
public class PhotoModel implements Parcelable {

    private int id;
    private String path;
    public boolean isChecked = false;
    public boolean doAnimation = false;
    public boolean filterAnimation = false;


    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator<PhotoModel>() {
                @Override
                public PhotoModel createFromParcel(Parcel source) {
                    return new PhotoModel(source);
                }

                @Override
                public PhotoModel[] newArray(int size) {
                    return new PhotoModel[size];
                }
            };


    public PhotoModel(Parcel source){
        id = source.readInt();
        path = source.readString();
        isChecked = source.readByte() == (byte)1 ? true : false;
    }


    public PhotoModel(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public PhotoModel() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoModel)) return false;

        PhotoModel photo = (PhotoModel) o;

        return id == photo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeByte(isChecked ? (byte)1 : (byte)0);
    }
}
