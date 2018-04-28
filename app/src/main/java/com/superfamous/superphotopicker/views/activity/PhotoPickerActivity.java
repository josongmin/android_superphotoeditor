package com.superfamous.superphotopicker.views.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mrjodev.jorecyclermanager.JoRecyclerAdapter;
import com.mrjodev.photopicker.R;
import com.mrjodev.photopicker.loader.callbacks.PhotosResultCallback;
import com.mrjodev.photopicker.loader.manager.PhotoManager;
import com.mrjodev.photopicker.loader.model.PhotoDirectoryModel;
import com.mrjodev.photopicker.photofilter.utility.FilterImageSdcardCache;
import com.mrjodev.photopicker.util.AlbumViewManager;
import com.mrjodev.photopicker.viewholders.PhotoHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_photo_picker_whitedown)
public class PhotoPickerActivity extends AppCompatActivity implements ViewTreeObserver.OnPreDrawListener {


    @ViewById(R.id.actJoPickPhoto_rcv)
    RecyclerView rcv;

    @ViewById(R.id.actJoPickPhoto_pbLoading)
    View vLoading;

    @ViewById(R.id.actJoPickPhoto_rlContent)
    ViewGroup vgContent;

    @ViewById(R.id.actJoPickPhoto_rlBottom)
    ViewGroup vgBottom;

    @ViewById(R.id.actPhotoPicker_vgAlbum)
    ViewGroup vgAlbum;

    @ViewById(R.id.actPhotoPicker_ivNameArrow)
    ImageView ivNameArrow;

    @ViewById(R.id.actPhotoPicker_tvAlbumName)
    TextView tvAlbumName;

    JoRecyclerAdapter photoAdapter;
    AlbumViewManager albumViewManager;

    @AfterViews
    void afterViews() {
        initAdapter();

        albumViewManager = new AlbumViewManager(this, vgAlbum, (ScrollView)findViewById(R.id.actPhotoPicker_svAlbum), findViewById(R.id.actPhotoPicker_vAlbumBlack));
        albumViewManager.setOnAlbumChooseListener(new AlbumViewManager.OnAlbumChooseListener() {
            @Override
            public void onAlbumChoose(PhotoDirectoryModel m) {
                photoAdapter.setListItems(m.getPhotos());
                photoAdapter.notifyDataSetChanged();

                tvAlbumName.setText(m.getName());
                ivNameArrow.animate().rotation(0);
            }
        });
        Log.d("JO", "afterViews");

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
//        }
        loadList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("JO", "onResume");

//        getViewTreeObserver().addOnPreDrawListener(this);
    }

    private void onPhotoLoaded(List<PhotoDirectoryModel> listAlbum){
        albumViewManager.load(listAlbum);

        photoAdapter.setListItems(listAlbum.get(0).getPhotos());
        photoAdapter.notifyDataSetChanged();
    }

    private void initAdapter(){

        photoAdapter = new JoRecyclerAdapter(
                new JoRecyclerAdapter.Params()
                        .setRecyclerView(rcv)
                        .setGridLayoutManager(4)
                        .setItemViewId(R.layout.item_photo)
                        .setItemViewHolderCls(PhotoHolder.class)
                        .addParam("filterImageSdcardCache", new FilterImageSdcardCache())
                        .addParam("itemWidth", getResources().getDisplayMetrics().widthPixels / 4)
                        .addParam("itemHeight", getResources().getDisplayMetrics().widthPixels / 3)
        );
        rcv.getItemAnimator().setChangeDuration(0);

    }

    private void loadList(){
        vLoading.setVisibility(View.VISIBLE);

        PhotoManager.loadAlbumsWithImages(this, new PhotosResultCallback() {
            @Override
            public void onResultCallback(List<PhotoDirectoryModel> listAlbum) {
                vLoading.setVisibility(View.GONE);
                onPhotoLoaded(listAlbum);
            }
        });
    }

    @Click(R.id.actPhotoPicker_llbtnOk)
    public void onOkClick(View v){
        Toast.makeText(this, "클릭", Toast.LENGTH_LONG).show();
    }


    @Click(R.id.actPhotoPicker_vbtnShowAlbum)
    public void onShowAlbumClick(final View v){

        if(!albumViewManager.isOpen()){
            albumViewManager.show();
            ivNameArrow.animate().rotation(180);
//            actPhotoPicker_ivNameArrow
        }else{
            albumViewManager.dismiss();
            ivNameArrow.animate().rotation(0);
        }

    }



    //리빌용

    protected ViewTreeObserver getViewTreeObserver(){
        return vgContent.getViewTreeObserver();
    }


    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        new FilterImageSdcardCache().clearCache();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Integer> listChangedItemPos = PhotoEditViewActivity.getChangedItemPos(requestCode, resultCode, data);

        if(listChangedItemPos != null){
            for(Integer i : listChangedItemPos){
                photoAdapter.notifyItemChanged(i);
                Log.d("JO", "listChangedItemPos " + i );
            }
        }
    }
}
