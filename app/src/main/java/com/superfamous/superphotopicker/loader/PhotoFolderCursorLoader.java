package com.superfamous.superphotopicker.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

/**
 * Created by josongmin on 2016-03-24.
 */
public class PhotoFolderCursorLoader extends CursorLoader {

    private final static String IMAGE_JPEG = "image/jpeg";
    private final static String IMAGE_PNG  = "image/png";
    private final static String IMAGE_GIF  = "image/gif";

    final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
    };

    public PhotoFolderCursorLoader(Context context) {
        super(context);

        setProjection(IMAGE_PROJECTION);
        setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");

        setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? " + (true ? ("or " + MIME_TYPE + "=?") : ""));
        String[] selectionArgs;
        if (true) {  //gif본다
            selectionArgs = new String[] { IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF };
        } else {
            selectionArgs = new String[] { IMAGE_JPEG, IMAGE_PNG };
        }
        setSelectionArgs(selectionArgs);
    }


    private PhotoFolderCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }


}