package com.superfamous.superphotopicker.loader.callbacks;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.superfamous.superphotopicker.loader.PhotoFolderCursorLoader;
import com.superfamous.superphotopicker.loader.model.PhotoDirectoryModel;
import com.superfamous.superphotopicker.util.Util;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

/**
 * Created by josongmin on 2016-03-24.
 */
public class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private PhotosResultCallback photosResultCallback;
    public PhotoDirLoaderCallbacks(Context context, PhotosResultCallback photosResultCallback) {
        this.context = context;
        this.photosResultCallback = photosResultCallback;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new PhotoFolderCursorLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data == null) return;
        List<PhotoDirectoryModel> listDirectory = getDataFromCursor(data, false);
//        data.close();

        photosResultCallback.onResultCallback(listDirectory);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //포토 정보 가져오고 디렉토리별로 분류해서 리턴
    public static List<PhotoDirectoryModel> getDataFromCursor(Cursor data, boolean checkImageStatus){
        List<PhotoDirectoryModel> directories = new ArrayList<>();
        PhotoDirectoryModel photoDirectoryAll = new PhotoDirectoryModel();
        photoDirectoryAll.setName("전체사진");
        photoDirectoryAll.setId("ALL");

        while (data.moveToNext()) {

            int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
            String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
            String path = data.getString(data.getColumnIndexOrThrow(DATA));

            if(name.equals("Camera")) name = "카메라";
            if(name.equals("Screenshots")) name = "스크린샷";
            if(name.equals("Download")) name = "다운로드";


            if (checkImageStatus) {
                if (!Util.checkImgCorrupted(path)) {
                    PhotoDirectoryModel photoDirectory = new PhotoDirectoryModel();
                    photoDirectory.setId(bucketId);
                    photoDirectory.setName(name);

                    if (!directories.contains(photoDirectory)) {
                        photoDirectory.setCoverPath(path);
                        photoDirectory.addPhoto(imageId, path);
                        photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                        directories.add(photoDirectory);
                    } else {
                        directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path);
                    }

                    photoDirectoryAll.addPhoto(imageId, path);
                }
            } else {

                PhotoDirectoryModel photoDirectory = new PhotoDirectoryModel();
                photoDirectory.setId(bucketId);
                photoDirectory.setName(name);

                if (!directories.contains(photoDirectory)) {
                    photoDirectory.setCoverPath(path);
                    photoDirectory.addPhoto(imageId, path);
                    photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                    directories.add(photoDirectory);
                } else {
                    directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path);
                }
                photoDirectoryAll.addPhoto(imageId, path);
            }
        }

        if (photoDirectoryAll.getPhotoPaths().size() > 0) {
            photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
        }

        directories.add(0, photoDirectoryAll);
        return directories;
    }
}
