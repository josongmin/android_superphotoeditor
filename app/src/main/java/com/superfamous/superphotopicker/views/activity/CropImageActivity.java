package com.superfamous.superphotopicker.views.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.edmodo.cropper.CropImageView;
import com.mrjodev.photopicker.R;

public class CropImageActivity extends AppCompatActivity implements View.OnClickListener{


    CropImageView cropImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        this.cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setAspectRatio(5, 5);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setGuidelines(1);
//        cropImageView.setBackgroundColor(getResources().getColor(android.R.color.black));


        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn1:
                cropImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sample_image1));
                break;

            case R.id.btn2:
                cropImageView.setAspectRatio(5, 5);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setGuidelines(1);
                break;

            case R.id.btn3:
                cropImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sample_image3));
                cropImageView.setAspectRatio(5, 5);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setGuidelines(2);
                cropImageView.animate().scaleX(1f).scaleY(1f).translationYBy(1f).setDuration(400).start();
                break;

            case R.id.btn4:

                cropImageView.setAspectRatio(5, 5);
                cropImageView.setFixedAspectRatio(false);
                cropImageView.setGuidelines(0);
                cropImageView.animate().scaleX(0.9f).scaleY(0.9f).translationYBy(0.9f).setDuration(400).start();
                break;

        }
    }
}
