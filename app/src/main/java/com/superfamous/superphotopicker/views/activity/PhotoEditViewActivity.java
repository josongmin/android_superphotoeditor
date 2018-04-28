package com.superfamous.superphotopicker.views.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.edmodo.cropper.CropImageView;
import com.mrjodev.photopicker.R;
import com.mrjodev.photopicker.loader.model.PhotoModel;
import com.mrjodev.photopicker.photofilter.ThumbnailFilterUtil;
import com.mrjodev.photopicker.photofilter.renderer.Filter;
import com.mrjodev.photopicker.photofilter.renderer.FilterRenderer;
import com.mrjodev.photopicker.photofilter.utility.FilterImageSdcardCache;
import com.mrjodev.photopicker.views.fragment.PhotoEditViewFragment;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_photo_edit_view)
public class PhotoEditViewActivity extends AppCompatActivity implements View.OnClickListener{

    public final static int REQCODE = 113;

    @ViewById(R.id.actPhotoEditView_vLoading)
    View vLoading;

    @ViewById(R.id.actPhotoEditView_vp)
    ViewPager vpContents;

    @ViewById(R.id.actPhotoEditView_glsfv)
    GLSurfaceView glSurfaceView;

    @ViewById(R.id.actPhotoEditView_ivCrop)
    CropImageView cropImageView;

    @ViewById(R.id.actPhotoEditView_vCropLine)
    View vCropLine;

    @ViewById(R.id.actPhotoEditView_vgFilters)
    ViewGroup vgFilters;

    @ViewById(R.id.actPhotoEditView_rlCropButtons)
    ViewGroup vgCropButtons;

    Picasso picasso;
    FilterRenderer filterRenderer;
    PhotoPagerAdapter photoPagerAdapter;
    PhotoEditViewFragment targetFragment;
    ThumbnailFilterUtil filterGLSurfaceViewsManager;

    ArrayList<Integer> listChangedItemPos = null;
    ArrayList<PhotoModel> listPhoto = null;
    PhotoModel targetPhotoModel = null;
    int pos = 0;

    private Bitmap currerntBitmap = null;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @AfterViews
    void afterViews() {
        //받아옵니당..

        long start = System.currentTimeMillis();

        listPhoto = getIntent().getParcelableArrayListExtra("listPhoto");
        pos = getIntent().getIntExtra("pos", 0);

        Log.d("JO", "after getParcelableArrayListExtra : " + (System.currentTimeMillis() - start) );

        targetPhotoModel = listPhoto.get(pos);

        initViews();
        init();

        Log.d("JO", "after init  : " + (System.currentTimeMillis() - start));

//        ImageView iv;
//        iv.setSelected(true);
    }


    private void initViews(){
        vgCropButtons.setVisibility(View.INVISIBLE);
        vLoading.setVisibility(View.INVISIBLE);
    }

    //초기화
    private void init(){
        listChangedItemPos = new ArrayList<Integer>();
        picasso =  new Picasso.Builder(this).defaultBitmapConfig(Bitmap.Config.RGB_565).build();

        //큰 이미지 필터 처리용
        filterRenderer = new FilterRenderer(glSurfaceView);

        //작은 이미지 필터 처리용 매니저
        filterGLSurfaceViewsManager = new ThumbnailFilterUtil(this, vgFilters, 46, 46); //dp
        filterGLSurfaceViewsManager.init((ViewGroup)glSurfaceView.getParent());
        filterGLSurfaceViewsManager.setFilterPhotoClickListener(onFilterPhotoClickListener);

        //포토 어댑터
        photoPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(), listPhoto);
        vpContents.setAdapter(photoPagerAdapter);
        vpContents.setOffscreenPageLimit(3);
        vpContents.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                onPageChoosed();
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        vpContents.setCurrentItem(pos);

        Handler handler = new Handler(){
            @Override
            public void dispatchMessage(Message msg) {
                onPageChoosed();
            }
        };
        handler.sendEmptyMessageDelayed(0, 100);

    }

    //화면선택되면 프래그먼트 대상 변경함.  이 프래그먼트에 필터 맥임
    private void onPageChoosed(){
        PhotoEditViewFragment photoEditViewFragment = photoPagerAdapter.mapFragments.get(vpContents.getCurrentItem());
        targetFragment = photoEditViewFragment;
        targetPhotoModel = listPhoto.get(vpContents.getCurrentItem());
        if(targetFragment == null) {
            return;
        }

        targetFragment.setStatusListener(targetBitmapStatusListener);
    }

    //외부에서 이미지 변경된경우
    private void onNewBitmapComeFromGLCanvas(Bitmap bitmap){

        //크롭중일때

        if(cropImageView.getVisibility() == View.VISIBLE){
            cropImageView.setVisibility(View.GONE);
            vgCropButtons.setVisibility(View.GONE);
        }

        if(vpContents.getVisibility() == View.INVISIBLE){
            vpContents.setVisibility(View.VISIBLE);
        }


        targetFragment.updateBitmap(bitmap); //타겟 프래그먼트에 업데이트
        vLoading.setVisibility(View.GONE);
    }

    private void onNewBitmapComeAfterCrop(Bitmap bitmap){
        filterRenderer.setImageBitmap(bitmap);
        filterRenderer.getDrawingBitmapAfterDrawFrame(onSnapShotListener);
        requestRendering();
    }

    //프래그먼트 이미지 로드됫을경우
    private void onFragmentBitmapLoad(final Bitmap bitmap){
        currerntBitmap = bitmap;
        new Thread(){
            @Override
            public void run() {
                filterRenderer.setImageBitmap(bitmap);
            }
        }.start();

        //썸네일 불러오기
        filterGLSurfaceViewsManager.updateItems(bitmap);
        vLoading.setVisibility(View.GONE);
    }

    //필터 클릭되쓸때 콜백
    private ThumbnailFilterUtil.FilterPhotoClickListener onFilterPhotoClickListener = new ThumbnailFilterUtil.FilterPhotoClickListener() {
        @Override
        public void onFilterPhotoClick(Filter filter, ImageView ivPhoto, int pos) {

            filterRenderer.setEffectFilter(filter);
            requestRendering();
        }
    };

    //그려지는 이미지 가져온 후 콜백
    private FilterRenderer.OnSnapShotListener onSnapShotListener = new FilterRenderer.OnSnapShotListener() {
        @Override
        public void onSnapShotCreated(final Bitmap bitmap) {
            Log.d("JO", "onSnapShotCreated bitmap.isRecycled" + bitmap.isRecycled());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onNewBitmapComeFromGLCanvas(bitmap);
                }
            });

            //파일이 변한경우(렌더링 원본 말고 나머지 경우)
            listChangedItemPos.add(vpContents.getCurrentItem());
            PhotoModel photoModel = targetPhotoModel;
            new FilterImageSdcardCache().saveFilterImage(PhotoEditViewActivity.this, bitmap, photoModel.getId() +"", photoModel.getPath()); //즈어장
            //sdcard파일 저장
        }
    };

    //프레그먼트 비트맵 상태 변경되면 넘어옴
    private PhotoEditViewFragment.StatusListener targetBitmapStatusListener = new PhotoEditViewFragment.StatusListener() {

        @Override
        public void onBitmapLoaded(final Bitmap bitmap) {
            onFragmentBitmapLoad(bitmap);
        }

        @Override
        public void onBitmapChanged(Bitmap bitmap) {
            currerntBitmap = bitmap;
        }
    };

    //필터 변경 후 새로 그리기
    private void requestRendering(){
        Log.d("JO", "requestRendering");

        if(onClickCrop){
            cancelCrop();
            onClickCrop = false;
        }

        vLoading.setVisibility(View.VISIBLE);
        filterRenderer.getDrawingBitmapAfterDrawFrame(onSnapShotListener);
        filterRenderer.requestRender();
    }

    private Bitmap getFullScreenFixedRatioBitmap(Bitmap source){

        float frameWidth = (float)glSurfaceView.getWidth();
        float frameHeight = (float)glSurfaceView.getHeight();
        float bitmapWidth = (float)source.getWidth();
        float bitmapHeight = (float)source.getHeight();
        float newWidth, newHeight;
        Bitmap bitmapFullScreen = null;

        if(frameHeight == bitmapHeight || frameWidth == bitmapWidth){
            bitmapFullScreen = source;
            Log.d("JO", "onCropImage 같아서 크기 안건드림");
        }else{
            float widthRatio = frameWidth / bitmapWidth ;
            float heightRatio = frameHeight / bitmapHeight;

            //가로가 더 길경우 가로를 풀로 채운다
            if(widthRatio < heightRatio){
                newWidth = frameWidth;
                newHeight = (int)(bitmapHeight * widthRatio);
            }
            //세로가 더 길경우 세로를 풀로 채운다
            else{
                newWidth = (int)(bitmapWidth * heightRatio);
                newHeight = frameHeight;
            }
            Log.d("JO", "onCropImage 크기변경됨");
            bitmapFullScreen = Bitmap.createScaledBitmap(source, (int)newWidth, (int)newHeight, true);
        }

        return bitmapFullScreen;
    }

    private void cancelCrop(){
        //크롭취소
        PropertyValuesHolder a1 = PropertyValuesHolder.ofFloat("translationY", -62, 0);
        PropertyValuesHolder a2 = PropertyValuesHolder.ofFloat("scaleX", 1f);
        PropertyValuesHolder a3 = PropertyValuesHolder.ofFloat("scaleY", 1f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(cropImageView, a1, a2, a3);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                vpContents.setVisibility(View.VISIBLE);

                cropImageView.setVisibility(View.GONE);
                vCropLine.setVisibility(View.GONE);
                vgCropButtons.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        anim.setDuration(300);
        anim.start();


        cropImageView.getChildAt(1).setVisibility(View.INVISIBLE);
        onClickCrop = false;
    }


    boolean onClickCrop = false;
    // 크롭 버튼 클릭
    @Click({R.id.actPhotoEditView_vbtnCut, R.id.actPhotoEditView_tvbtnCropCancel})
    public void onCropBtnClick(View v){
        if(!onClickCrop){
            //크롭화면 고고
            cropImageView.getChildAt(1).setVisibility(View.VISIBLE);

            Bitmap bitmapFullScreen = getFullScreenFixedRatioBitmap(currerntBitmap);
            if(bitmapFullScreen == currerntBitmap){
                //변화없음
            }

            cropImageView.setGuidelines(2);
            cropImageView.setImageBitmap(bitmapFullScreen);
            cropImageView.setVisibility(View.VISIBLE);
            vCropLine.setVisibility(View.VISIBLE);
            vpContents.setVisibility(View.INVISIBLE);
            vgCropButtons.setVisibility(View.VISIBLE);
//            cropImageView.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
            PropertyValuesHolder a1 = PropertyValuesHolder.ofFloat("translationY", 0, -62);
            PropertyValuesHolder a2 = PropertyValuesHolder.ofFloat("scaleX", 0.90f);
            PropertyValuesHolder a3 = PropertyValuesHolder.ofFloat("scaleY", 0.90f);
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(cropImageView, a1, a2, a3);
            anim.setDuration(300);
            anim.start();

            anim.start();
            onClickCrop = true;
        }
        else {
            cancelCrop();
        }
    }

    @Click(R.id.actPhotoEditView_tvbtnCropOk) //자르기 완료
    public void onBtnCropOk(View v){
        Bitmap bitmapCropped = cropImageView.getCroppedImage();
        Bitmap bitmapCroppedFullScreen = getFullScreenFixedRatioBitmap(bitmapCropped);
        bitmapCropped.recycle();

        PropertyValuesHolder a1 = PropertyValuesHolder.ofFloat("translationY", -62, 0);
        PropertyValuesHolder a2 = PropertyValuesHolder.ofFloat("scaleX", 1f);
        PropertyValuesHolder a3 = PropertyValuesHolder.ofFloat("scaleY", 1f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(cropImageView, a1, a2, a3);
        anim.setDuration(300);
        anim.start();

        cropImageView.setImageBitmap(bitmapCroppedFullScreen);
        cropImageView.getChildAt(1).setVisibility(View.INVISIBLE);

        vCropLine.setVisibility(View.GONE);
        onClickCrop = false;
//            cropImageView.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
        onNewBitmapComeAfterCrop(bitmapCroppedFullScreen);


    }

    // 회전 버튼 클릭
    @Click(R.id.actPhotoEditView_vbtnRotate)
    public void onRotateBtnClick(View v){
        filterRenderer.rotateBy(90);
        requestRendering();
    }


    @Override
    public void onClick(View v) {

    }


    public Picasso getPicasso() {
        return picasso;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        picasso.shutdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent().putExtra("listChangedItemPos", listChangedItemPos));
        finish();
    }

    //포토페이저 어댑터
    class PhotoPagerAdapter extends FragmentStatePagerAdapter {

        public Map<Integer, PhotoEditViewFragment> mapFragments = new HashMap<Integer, PhotoEditViewFragment>();
        ArrayList<PhotoModel> listPhotoModel;
        int size = 0;

        public PhotoPagerAdapter(FragmentManager fm, ArrayList<PhotoModel> listPhotoModel) {
            super(fm);
            this.listPhotoModel = listPhotoModel;
            this.size = listPhotoModel.size();
        }

        @Override
        public Fragment getItem(int position) {
            PhotoModel photoModel = listPhotoModel.get(position);
            PhotoEditViewFragment f = PhotoEditViewFragment.getInstance(photoModel);
            mapFragments.put(position, f);
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mapFragments.remove(position);
        }

        @Override
        public int getCount() {
            return size;
        }
    }

    //static
    public static ArrayList<Integer> getChangedItemPos(int requestCode, int resultCode, Intent data){
        if(requestCode == REQCODE && resultCode == RESULT_OK){
            return data.getIntegerArrayListExtra("listChangedItemPos");
        }else{
            return null;
        }
    }
}

