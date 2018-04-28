package com.superfamous.superphotopicker.views.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mrjodev.photopicker.R;
import com.mrjodev.photopicker.loader.model.PhotoModel;
import com.mrjodev.photopicker.views.activity.PhotoEditViewActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by josongmin on 2016-04-02.
 */


public class PhotoEditViewFragment extends Fragment{

    private View vLoading;
    private ImageView ivPhoto;
    private PhotoViewAttacher attacher;
    private Bitmap bitmap = null;
    private StatusListener statusListener;
    private PhotoModel photoModel;
    private Picasso picasso;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoModel = getArguments().getParcelable("photoModel");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_edit_view, container, false);
        vLoading = v.findViewById(R.id.fragPhotoEditView_vLoading);
        ivPhoto = (ImageView)v.findViewById(R.id.fragPhotoEditView_ivPhoto);
        attacher = new PhotoViewAttacher(ivPhoto);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //불러오기

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoModel.getPath(), options);

        int maxSizePixel = 1800;
        int width = options.outWidth;
        int height = options.outHeight;
//            float ratio = width / (float)height;
        boolean isChanged = false;

        if(width > maxSizePixel || height > maxSizePixel){
            width = width / 2;
            height = height / 2;
        }

        picasso = ((PhotoEditViewActivity)getActivity()).getPicasso();

        picasso.load("file://" + photoModel.getPath()).resize(width, height)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        vLoading.setVisibility(View.GONE);
                        attacher.update();
                        bitmap = ((BitmapDrawable)ivPhoto.getDrawable()).getBitmap();

                        if(statusListener != null){
                            statusListener.onBitmapLoaded(bitmap);
                        }
                    }

                    @Override
                    public void onError() {
                        vLoading.setVisibility(View.GONE);
                    }
                });
    }

    /**비트맵 갖고오기. 없으면 콜백 등록 있으면 바로 리턴*/
    public void setStatusListener(StatusListener statusListener){
        if(bitmap != null){
            statusListener.onBitmapLoaded(bitmap);
            this.statusListener = statusListener;
        }else{
            this.statusListener = statusListener;
        }
    }

    /**비트맵 새로 업데이트해주기*/
    public void updateBitmap(Bitmap bitmap){
        ivPhoto.setImageBitmap(bitmap);
        attacher.update();
        if(bitmap != null){
            this.bitmap.recycle();
        }
        this.bitmap = bitmap;

        Log.d("JO", "PhotoEditViewFragment updateBitmap statusListener " + statusListener);

        if(statusListener != null){
            statusListener.onBitmapChanged(bitmap);
            Log.d("JO", "PhotoEditViewFragment onBitmapChanged bitmap.isRecycled" + bitmap.isRecycled());

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        if(ivPhoto == null) return;
//        ivPhoto.setImageDrawable(null);
//        ivPhoto.setImageBitmap(null);
//
////        picasso.shutdown();
//        picasso.cancelRequest(ivPhoto);
//        picasso.invalidate("file://" + photoModel.getPath());
//
//
//        if(bitmap != null && !bitmap.isRecycled()){
//            bitmap.recycle();
//        }
//        bitmap = null;
    }

    public static interface StatusListener{
        public void onBitmapLoaded(Bitmap bitmap);
        public void onBitmapChanged(Bitmap bitmap);
    }

    public static final PhotoEditViewFragment getInstance(Parcelable photoModel){
        PhotoEditViewFragment frag = new PhotoEditViewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("photoModel", photoModel);
        frag.setArguments(bundle);
        return frag;
    }

}