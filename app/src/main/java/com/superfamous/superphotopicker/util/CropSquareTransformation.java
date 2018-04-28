package com.superfamous.superphotopicker.util;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by josongmin on 2016-04-04.
 */
public class CropSquareTransformation implements Transformation {

    private int maxSizePixel = 0;
    public CropSquareTransformation(final int maxSizePixel) {
        this.maxSizePixel = maxSizePixel;
    }
    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        float ratio = width / (float)height;
        boolean isChanged = false;
        if(width > maxSizePixel){
            width = maxSizePixel;
            height = (int)(width / ratio);
            isChanged = true;
        }else if(height > maxSizePixel){
            height = maxSizePixel;
            width = (int)(width * ratio);
            isChanged = true;
        }else{
            isChanged = false;
        }

        if(isChanged){
            Bitmap result = Bitmap.createScaledBitmap(source, width, height, true);
            source.recycle();
            return result;
        }else{
            return source;
        }
    }

    @Override
    public String key() { return "square()"; }
}
