package com.superfamous.superphotopicker.photofilter.renderer;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by josongmin on 2016-04-07.
 */
public abstract class MultiImageFilterListener {

    private List<Filter> listFilter;

    public List<Filter> getListFilter() {
        return listFilter;
    }

    public MultiImageFilterListener(List<Filter> listFilter) {
        this.listFilter = listFilter;
    }

    public abstract void onMultiSnapShotCreated(Bitmap... arrBitmap);

}
