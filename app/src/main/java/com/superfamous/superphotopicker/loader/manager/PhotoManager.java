package com.superfamous.superphotopicker.loader.manager;

import android.support.v4.app.FragmentActivity;

import com.superfamous.superphotopicker.loader.callbacks.PhotoDirLoaderCallbacks;
import com.superfamous.superphotopicker.loader.callbacks.PhotosResultCallback;


/**
 * Created by josongmin on 2016-03-24.
 */
public class PhotoManager {
    public static void loadAlbumsWithImages(final FragmentActivity activity, final PhotosResultCallback callback){
        activity.getSupportLoaderManager().initLoader(0, null, new PhotoDirLoaderCallbacks(activity, callback));
    }
}
