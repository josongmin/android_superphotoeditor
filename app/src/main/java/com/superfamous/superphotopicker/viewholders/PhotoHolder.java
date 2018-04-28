package com.superfamous.superphotopicker.viewholders;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.Target;
import com.superfamous.superphotopicker.R;
import com.superfamous.superphotopicker.loader.model.PhotoModel;
import com.superfamous.superphotopicker.photofilter.utility.FilterImageSdcardCache;
import com.superfamous.superphotopicker.views.activity.PhotoEditViewActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by josongmin on 2016-03-24.
 */
public class PhotoHolder extends JoViewHolder<PhotoModel>{

    @Bind(R.id.itemPhoto_vTopLine)
    View vTopLine;

    @Bind(R.id.itemPhoto_vgParent)
    ViewGroup vgParent;

    @Bind(R.id.itemPhoto_vPhotoFilter)
    View vPhotoFilter;

    @Bind(R.id.itemPhoto_ivPhoto)
    ImageView ivPhoto;

    @Bind(R.id.itemPhoto_ivPhotoRepl)
    ImageView ivPhotoRepl;

    @Bind(R.id.itemPhoto_rlbtnChk)
    ViewGroup vgBtnChk;

    @Bind(R.id.itemPhoto_ivBgCheck)
    View vBgChk;

    @Bind(R.id.itemPhoto_ivBgUncheck)
    View vBgUnChk;

    GlideDrawable resourceBefore = null;

    public PhotoHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onViewCreated() {
        ButterKnife.bind(this, itemView);

        int itemWidth = getRcvParams().getParam("itemWidth");
        int itemHeight = getRcvParams().getParam("itemHeight");
        itemView.setLayoutParams(new RelativeLayout.LayoutParams(itemWidth, itemHeight));
    }

    @Override
    public void onBind(final PhotoModel m, int position, int absPosition) {
        super.onBind(m, position, absPosition);
        vPhotoFilter.setVisibility(View.INVISIBLE);

        //클릭리스너
        vgBtnChk.setOnClickListener(onChkClick);
        ivPhoto.setOnClickListener(onItemClick);


        if(position < 4){
            vTopLine.setVisibility(View.GONE);
        }else{
            vTopLine.setVisibility(View.VISIBLE);
        }

        //체크상태
        if(m.isChecked){
            vBgChk.setVisibility(View.VISIBLE);
            vBgUnChk.setVisibility(View.GONE);
        }else{
            vBgChk.setVisibility(View.GONE);
            vBgUnChk.setVisibility(View.VISIBLE);
        }

        //캐시처리
        final FilterImageSdcardCache filterImageSdcardCache = (FilterImageSdcardCache)getAdapter().getParams().getParam("filterImageSdcardCache");
        //필터된 캐시있는지 확인
        if(filterImageSdcardCache.hasCache(getContext(), m.getId()+"", m.getPath())){
            //있으면 마법봉 표시.
            ivPhotoRepl.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load("file://" + m.getPath())
                    .placeholder(R.color.colorPrimary)
//                    .placeholder(R.drawable.placeholder_photo_gray)
                    .thumbnail(0.3f)
                    .error(R.drawable.placeholder_photo_gray)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Glide.with(getContext())
                                    .load("file://" + filterImageSdcardCache.getFilterImagePath(getContext(), m.getId()+"", m.getPath()))
                                    .thumbnail(0.3f)
                                    .dontAnimate()
                                    .animate(new ViewPropertyAnimation.Animator() {
                                        @Override
                                        public void animate(View view) {
                                            vPhotoFilter.setVisibility(View.VISIBLE);
                                            vPhotoFilter.setAlpha(1f);
                                            vPhotoFilter.animate().alpha(0f).setDuration(1600).start();
                                        }
                                    })
                                    .into(ivPhotoRepl);
                            return false;
                        }
                    })
                    .into(ivPhoto);

        }else{
            ivPhotoRepl.setVisibility(View.GONE);
            Glide.with(getContext())
                    .load("file://" + m.getPath())
                    .placeholder(R.color.colorPrimary)
//                    .placeholder(R.drawable.placeholder_photo_gray)
                    .thumbnail(0.3f)
                    .error(R.drawable.placeholder_photo_gray)
                    .dontAnimate()
                    .into(ivPhoto);

        }
    }

    final View.OnClickListener onChkClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<PhotoModel> list = (ArrayList<PhotoModel>)getAdapter().getListItems();
            int pos = getPos();

            PhotoModel m = getModel();
            m.isChecked = !m.isChecked;

            getAdapter().notifyItemChanged(getAbsPos());
        }
    };

    final View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<PhotoModel> list = (ArrayList<PhotoModel>)getAdapter().getListItems();
            int pos = getPos();

            //액티비티를 갖고와야한답..
            ((Activity)getContext()).startActivityForResult(new Intent(getContext(), PhotoEditViewActivity_.class)
                    .putParcelableArrayListExtra("listPhoto", list)
                    .putExtra("pos", pos), PhotoEditViewActivity.REQCODE);
        }
    };
}
