package com.superfamous.superphotopicker.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.superfamous.superphotopicker.R;


/**
 * Created by josongmin on 2016-06-07.
 */

//뷰홀더
public class WorkerViewHolder extends RecyclerView.ViewHolder{
    public TextView tvName, tvAddress;
    public ImageView ivPhoto;
    public WorkerViewHolder(View itemView) {
        super(itemView);
        ivPhoto = itemView.findViewById(R.id.itemWorker_ivPhoto);
        tvAddress = itemView.findViewById(R.id.itemWorker_tvAddress);
        tvName =  itemView.findViewById(R.id.itemWorker_tvName);
    }
}