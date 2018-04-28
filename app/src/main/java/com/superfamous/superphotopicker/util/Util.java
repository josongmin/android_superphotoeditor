package com.superfamous.superphotopicker.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by josongmin on 2016-03-24.
 */
public class Util {
    public static Uri generatorUri(String fileUri, String scheme){
        Uri uri = new Uri.Builder().scheme(scheme).path(fileUri).build();
        return uri;
    }

    public static boolean checkImgCorrupted(String filePath) {
        BitmapFactory.Options options = null;
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);
        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
            return true;
        }
        return false;
    }

    public static int dpToPx(float dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px){
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static final Bitmap makeBitmapFromDrawable (Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static final Bitmap makeBitmapFromImageView(ImageView view) {

        try{


            view.buildDrawingCache(true);
            view.getDrawingCache(true);
            BitmapDrawable drawable = (BitmapDrawable)view.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                return drawable.getBitmap();
            }
            Bitmap bitmap = makeBitmapFromDrawable(drawable);
            view.buildDrawingCache(false);
            view.getDrawingCache(false);
            return bitmap;

        }catch(Exception e){
            return null;
        }
    }

    public static final Bitmap makeThumbBitmap(final Bitmap bitmapSrc, final int width, final int height){
        return ThumbnailUtils.extractThumbnail(bitmapSrc, width, height);
    }

    public static final int hash(String id, String name) {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public static final String getLogTag(){
        return "JoPhotoPicker";
    }

    public static final void Log(String msg){
        Log.d(getLogTag(), msg);
    }
}
