package com.superfamous.superphotopicker.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.superfamous.superphotopicker.R;
import com.superfamous.superphotopicker.loader.model.PhotoDirectoryModel;

import java.util.List;

/**
 * Created by josongmin on 2016-04-22.
 */
public class AlbumViewManager implements View.OnClickListener{

    private final static int MAX_VISIBLE_COLUMN = 6;
    private final static int PADDING_BOTTOM = Util.dpToPx(15);
    private final static int ITEM_RES_ID = R.layout.item_photo_album_active;

    private Activity activity;
    ViewGroup vgAlbumGroup;
    ScrollView sv;
    OnAlbumChooseListener onAlbumChooseListener;
    View vBlack;
    boolean isOpen = false;

    int itemHeight = 0;
    public AlbumViewManager(Activity activity, ViewGroup vgAlbumGroup, ScrollView sv, View vBlack) {
        this.vgAlbumGroup = vgAlbumGroup;
        this.activity = activity;
        this.sv = sv;
        this.vBlack = vBlack;
    }


    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOnAlbumChooseListener(OnAlbumChooseListener onAlbumChooseListener) {
        this.onAlbumChooseListener = onAlbumChooseListener;
    }

    public void load(List<PhotoDirectoryModel> listPhotoDirecModels) {

        vgAlbumGroup.removeAllViews();

        //인플레이팅해서 넣기.
        for(PhotoDirectoryModel m : listPhotoDirecModels){
            ViewGroup vgItem = (ViewGroup)activity.getLayoutInflater().inflate(ITEM_RES_ID, null);

            final ImageView ivPhoto = (ImageView)vgItem.findViewById(R.id.itemPhotoAlbum_ivPhoto);
            final TextView tvName = (TextView)vgItem.findViewById(R.id.itemPhotoAlbum_tvName);
            final TextView tvCnt = (TextView)vgItem.findViewById(R.id.itemPhotoAlbum_tvCnt);

            Glide.with(activity).load("file://" + m.getCoverPath()).into(ivPhoto);
            tvCnt.setText(m.getPhotos().size() + "개의 사진이 있습니다.");
            tvName.setText(m.getName());

            vgItem.setTag(R.id.itemPhotoAlbum_ivPhoto, m);
            vgItem.setTag(R.id.itemPhotoAlbum_tvName, listPhotoDirecModels);
            vgItem.setOnClickListener(this);

            if(m.getId().equals("ALL")){
                ImageView ivChk = (ImageView)vgItem.findViewById(R.id.itemPhotoAlbum_ivChk);
                ivChk.setImageResource(R.drawable.ic_check_on);

            }

            vgAlbumGroup.addView(vgItem);
            vgItem = null;
        }

        sv.post(new Runnable() {
            @Override
            public void run() {
                itemHeight = vgAlbumGroup.getChildAt(0).getHeight();
                ViewGroup.LayoutParams lp = sv.getLayoutParams();
                lp.height = itemHeight * MAX_VISIBLE_COLUMN + PADDING_BOTTOM;
                sv.setLayoutParams(lp);
            }
        });
    }

    public void show(){

        sv.setVisibility(View.VISIBLE);
        doRevealAnimation(true);

        vBlack.setVisibility(View.VISIBLE);
        vBlack.animate().alpha(1f).setDuration(300).start();

        isOpen = true;
    }

    public void dismiss(){
        doRevealAnimation(false);
        vBlack.animate().alpha(0f).setDuration(300).start();

        isOpen = false;
    }

    //애니메이션
    private void doRevealAnimation(boolean reveal){

        int cx = vgAlbumGroup.getLeft() + (vgAlbumGroup.getWidth() / 3);
        int cy = vgAlbumGroup.getTop() + vgAlbumGroup.getBottom();

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(vgAlbumGroup,
                cx, cy,
                0,
                hypo(vgAlbumGroup.getHeight(), vgAlbumGroup.getWidth()),
                View.LAYER_TYPE_SOFTWARE);
        animator.setDuration(320);
        animator.setInterpolator(new LinearInterpolator());

        if(reveal){
            vgAlbumGroup.setVisibility(View.VISIBLE);
            animator.start();
        }else{
            SupportAnimator reverseAnimator = animator.reverse();
            reverseAnimator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() { }

                @Override
                public void onAnimationEnd() {
                    sv.setVisibility(View.INVISIBLE);
                    vgAlbumGroup.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel() {}

                @Override
                public void onAnimationRepeat() {}
            });
            reverseAnimator.start();
        }

    }

    public float hypo(float a, float b){
        return (float) Math.sqrt( Math.pow(a, 2) + Math.pow(b, 2) );
    }

    @Override
    public void onClick(View v) {

        PhotoDirectoryModel mDirec = (PhotoDirectoryModel)v.getTag(R.id.itemPhotoAlbum_ivPhoto);
        List<PhotoDirectoryModel> list = (List<PhotoDirectoryModel>)v.getTag(R.id.itemPhotoAlbum_tvName);


        for(int i = 0; i < list.size(); i++){
            ImageView ivChk = (ImageView)vgAlbumGroup.getChildAt(i).findViewById(R.id.itemPhotoAlbum_ivChk);
            ivChk.setImageResource(R.drawable.ic_check_off_gray);

            PhotoDirectoryModel m = list.get(i);

            if(m == mDirec){
                ivChk.setImageResource(R.drawable.ic_check_on);
                if(onAlbumChooseListener != null){
                    onAlbumChooseListener.onAlbumChoose(m);
                }

                dismiss();
            }


        }
    }

    static public interface OnAlbumChooseListener{
        public void onAlbumChoose(PhotoDirectoryModel m);
    }
}
