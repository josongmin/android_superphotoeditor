package com.superfamous.superphotopicker.viewholders;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.superfamous.superphotopicker.R;
import com.superfamous.superphotopicker.loader.model.PhotoDirectoryModel;
import com.superfamous.superphotopicker.util.Util;

/**
 * Created by josongmin on 2016-03-24.
 */
public class PhotoDirectoryHolder extends JoViewHolder<PhotoDirectoryModel>{

    @Bind(R.id.itemPhotoAlbum_ivPhoto)
    ImageView ivPhoto;


    public PhotoDirectoryHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onViewCreated() {

    }

    @Override
    public void onBind(PhotoDirectoryModel m, int position, int absPosition) {
        super.onBind(m, position, absPosition);

        Uri uri = Util.generatorUri(m.getCoverPath(), "file");
        Glide.with(getContext()).load(uri).placeholder(R.drawable.icon_downarrow_black).thumbnail(0.3f).error(R.drawable.icon_downarrow_black)
                .dontAnimate()
                .into(ivPhoto);
    }


}
