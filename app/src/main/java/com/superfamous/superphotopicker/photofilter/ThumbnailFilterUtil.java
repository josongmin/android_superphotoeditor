package com.superfamous.superphotopicker.photofilter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superfamous.superphotopicker.R;
import com.superfamous.superphotopicker.photofilter.renderer.Filter;
import com.superfamous.superphotopicker.photofilter.renderer.FilterRenderer;
import com.superfamous.superphotopicker.photofilter.renderer.MultiImageFilterListener;
import com.superfamous.superphotopicker.util.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by josongmin on 2016-04-04.
 */
public class ThumbnailFilterUtil implements View.OnClickListener{

    final static int filterItemPadding = 4;

    private final static String TAG = "ThumbnailFilterUtil";
    private Context context;
    private ViewGroup vgFilterImage;
    private int thumbWidth, thumbHeight;
    private List<Filter> listFilterInfo;
    private FilterRenderer filterRendererThumb;
    private Queue<Bitmap> queueBitmapForRecycle;
    private Activity activity;
    private FilterPhotoClickListener filterPhotoClickListener;
    private Filter checkedFilter = null;

    public ThumbnailFilterUtil(Activity activity, ViewGroup vgFilterImage, int thumbWidth, int thumbHeight){
        this.context = vgFilterImage.getContext();
        this.vgFilterImage = vgFilterImage;
        this.thumbWidth = Util.dpToPx(thumbWidth);
        this.thumbHeight = Util.dpToPx(thumbHeight);
        this.activity = activity;

        vgFilterImage.removeAllViews();

        this.queueBitmapForRecycle = new LinkedList<Bitmap>();
        this.listFilterInfo = new ArrayList<Filter>();

        putFilters(Filter.Builder().setFilterName("원본").setOriginal(true).build());
        putFilters(Filter.Builder().setFilterName("예쁘게").addEffectAutoFix(0.5f).build());
        putFilters(Filter.Builder().setFilterName("화사한").addEffectGrain(1.0f).build());
        putFilters(Filter.Builder().setFilterName("맛있는").addEffectDocumentary().build());
        putFilters(Filter.Builder().setFilterName("여행지").addEffectCrossProcess().build());
        putFilters(Filter.Builder().setFilterName("감자국").addEffectPosterize().build());
        putFilters(Filter.Builder().setFilterName("방사능").addEffectSepia().build());
        putFilters(Filter.Builder().setFilterName("뽀샤시").addEffectGrayScale().build());
        putFilters(Filter.Builder().setFilterName("화사한").addEffectGrain(1.0f).build());
        putFilters(Filter.Builder().setFilterName("맛있는").addEffectDocumentary().build());
        putFilters(Filter.Builder().setFilterName("여행지").addEffectCrossProcess().build());
        putFilters(Filter.Builder().setFilterName("감자국").addEffectPosterize().build());
        putFilters(Filter.Builder().setFilterName("방사능").addEffectSepia().build());
        putFilters(Filter.Builder().setFilterName("뽀샤시").addEffectGrayScale().build());

    }

    public void setFilterPhotoClickListener(FilterPhotoClickListener filterPhotoClickListener) {
        this.filterPhotoClickListener = filterPhotoClickListener;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    /**필터 추가*/
    public void putFilters(Filter filter){
        listFilterInfo.add(filter);
    }

    /**뷰 생성 초기화. 그려질 SurfaceView가 필요함. 근데 안보여야되니 겹치는 레이아웃뒤로*/
    public void init(ViewGroup vgStackable){
        final GLSurfaceView glSurfaceViewThumb = new GLSurfaceView(context);
        glSurfaceViewThumb.setLayoutParams(new LinearLayout.LayoutParams(thumbWidth, thumbHeight));
        ((ViewGroup)(vgStackable)).addView(glSurfaceViewThumb, 0);

        //필터 렌더러 및 glView넣기
        this.filterRendererThumb = new FilterRenderer(glSurfaceViewThumb);
        Bitmap placeHolder = BitmapFactory.decodeResource(activity.getResources(), R.drawable.placeholder_photo_gray);

        vgFilterImage.setPadding(Util.dpToPx(filterItemPadding), 0, Util.dpToPx(filterItemPadding), 0);

        boolean first = false;
        //여기서 썸네일뷰 생성하고 넣기
        for(Filter filterInfo : listFilterInfo){
            ViewGroup vgPhoto = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.item_filterphoto, null);
            final CircleImageView ivPhoto = (CircleImageView)vgPhoto.findViewById(R.id.itemFilterPhoto_ivPhoto);
            final TextView tvFilterName = (TextView)vgPhoto.findViewById(R.id.itemFilterPhoto_tvText);


            ivPhoto.setBorderColor(Color.parseColor("#33000000"));
            ivPhoto.setBorderWidth(1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(thumbWidth, thumbHeight);
            layoutParams.setMargins(Util.dpToPx(filterItemPadding), 0, Util.dpToPx(filterItemPadding), 0);
            ivPhoto.setLayoutParams(layoutParams);
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            ivPhoto.setImageResource(R.drawable.placeholder_photo_gray);
            ivPhoto.setImageBitmap(placeHolder);

            tvFilterName.setText(filterInfo.getFilterName());

            vgFilterImage.addView(vgPhoto);
            filterInfo.setTag(vgPhoto);
//            filterInfo.ivPhoto = ivPhoto;
        }

        //filter가 적용되어있으면 해당 필터에 체크, 없으면 첫번째꺼 체크

        ((TextView)((ViewGroup)listFilterInfo.get(0).getTag()).findViewById(R.id.itemFilterPhoto_tvText))
                .setTextColor(activity.getResources().getColor(R.color.colorAccent));


    }

    public void cancelOtherRequest(Bitmap bitmap){

    }

    /**모델<-> 뷰 업데이트*/
    public synchronized void updateItems(Bitmap bitmap){


        Util.Log("updateItems thumbWidth " + thumbWidth + " thumbHeight " + thumbHeight);
        filterRendererThumb.setImageBitmap(Util.makeThumbBitmap(bitmap, thumbWidth, thumbHeight), false);
        filterRendererThumb.makeMuiltDrawingBitmapAfterDrawFrame(new MultiImageFilterListener(listFilterInfo) {
            @Override
            public void onMultiSnapShotCreated(final Bitmap... arrBitmaps) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < arrBitmaps.length; i++) {

                            ImageView ivThumb = (ImageView)((ViewGroup)listFilterInfo.get(i).getTag()).findViewById(R.id.itemFilterPhoto_ivPhoto);
//                            ImageView ivThumb = (ImageView)vgFilterImage.getChildAt(i);
                            if(ivThumb.getDrawable() != null && ivThumb.getDrawable() instanceof BitmapDrawable){
                                ((BitmapDrawable)ivThumb.getDrawable()).getBitmap().recycle();
                            }

                            ivThumb.setImageDrawable(null);
                            ivThumb.setImageBitmap(arrBitmaps[i]);

                            ivThumb.animate().scaleY(1f).rotationBy(30).scaleX(1f).setDuration(0).start();
                            ivThumb.animate().scaleY(1f).rotation(0).scaleX(1f).setDuration(150).start();


                            if(!ivThumb.hasOnClickListeners()){
                                ivThumb.setTag(i);
                                ivThumb.setOnClickListener(ThumbnailFilterUtil.this);
                            }
                        }
                        //백그라운드로 지우기
//                        removeUnusedBitmap();
//                        queueUnusedBitmap(arrBitmaps);
                    }
                });
            }
        });
        filterRendererThumb.requestRender();
    }

    private void queueUnusedBitmap(Bitmap[] arrBitmap){
        synchronized (queueBitmapForRecycle){
            for(Bitmap bitmap : arrBitmap){
                queueBitmapForRecycle.add(bitmap);
            }
        }
    }

    private void removeUnusedBitmap(){
        synchronized (queueBitmapForRecycle){

            while(queueBitmapForRecycle.peek() != null){
                Bitmap bitmap = queueBitmapForRecycle.poll();
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    /**해제*/
    public void destroyItems(){

    }

    @Override
    public void onClick(View v) {
        int pos = (int)v.getTag();
        Filter filter = listFilterInfo.get(pos);
        checkedFilter = filter;

        CircleImageView ivReturn = null;

        for(Filter f : listFilterInfo){
            ViewGroup vg = (ViewGroup)f.getTag();
            CircleImageView ivPhoto = (CircleImageView)vg.findViewById(R.id.itemFilterPhoto_ivPhoto);
            TextView tv = (TextView)vg.findViewById(R.id.itemFilterPhoto_tvText);

            if(f == filter){
                ivReturn = ivPhoto;
                tv.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            }else{
                tv.setTextColor(Color.parseColor("#dd000000"));
            }
        }

        if(filterPhotoClickListener != null){
            filterPhotoClickListener.onFilterPhotoClick(filter, ivReturn, pos);
        }
    }

    public interface FilterPhotoClickListener{
        public void onFilterPhotoClick(Filter filter, ImageView ivPhoto, int pos);
    }
}