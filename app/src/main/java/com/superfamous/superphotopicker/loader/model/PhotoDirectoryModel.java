package com.superfamous.superphotopicker.loader.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by donglua on 15/6/28.
 */
public class PhotoDirectoryModel implements Parcelable {

    private String id;
    private String coverPath;
    private String name;
    private long dateAdded;
    private List<PhotoModel> photos = new ArrayList<>();
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator<PhotoDirectoryModel>() {
                @Override
                public PhotoDirectoryModel createFromParcel(Parcel source) {
                    return new PhotoDirectoryModel(source);
                }

                @Override
                public PhotoDirectoryModel[] newArray(int size) {
                    return new PhotoDirectoryModel[size];
                }
            };

    public PhotoDirectoryModel(Parcel source) {
        id = source.readString();
        coverPath = source.readString();
        name = source.readString();
        dateAdded = source.readLong();
        photos = source.readArrayList(PhotoModel.class.getClassLoader());
    }

    public PhotoDirectoryModel(){
        ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoDirectoryModel)) return false;

        PhotoDirectoryModel directory = (PhotoDirectoryModel) o;

        if (!id.equals(directory.id)) return false;
        return name.equals(directory.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoModel> photos) {
        this.photos = photos;
    }

    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<>(photos.size());
        for (PhotoModel photo : photos) {
            paths.add(photo.getPath());
        }
        return paths;
    }

    public void addPhoto(int id, String path) {
        photos.add(new PhotoModel(id, path));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(coverPath);
        dest.writeString(name);
        dest.writeLong(dateAdded);
        dest.writeTypedList(photos);
    }

}