package com.superfamous.superphotopicker.loader.callbacks;

/**
 * Created by josongmin on 2016-03-24.
 */


import com.superfamous.superphotopicker.loader.model.PhotoDirectoryModel;

import java.util.List;

/**사진불러오고 난다음 콜백*/
public interface PhotosResultCallback {
    void onResultCallback(List<PhotoDirectoryModel> listAlbum);
}