package com.superfamous.superphotopicker.photofilter.utility;

import android.content.Context;
import android.graphics.Bitmap;

import com.superfamous.superphotopicker.util.SimpleSharedPreference;
import com.superfamous.superphotopicker.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by josongmin on 2016-04-10.
 */
public class FilterImageSdcardCache {
    //그냥
    private String dirName = "jodevfiltergallery";

    //builder

    public FilterImageSdcardCache setDirName(String dirName){
        this.dirName = dirName;
        return this;
    }

    //캐시있냐 없냐
    public boolean hasCache(Context context, String id, String fileName){
        String hash = Util.hash(id, fileName) + "";
        String hashDest = SimpleSharedPreference.with(context).get(hash);
        if(hashDest == null) return false;

        File file = new File(getDirectory(), hashDest);
        return file.exists();
    }

    public String getFilterImagePath(Context context, String id, String fileName){
        String hash = Util.hash(id, fileName) + "";
        String hashDest = SimpleSharedPreference.with(context).get(hash);
        File file = new File(getDirectory(), hashDest);

        return file.getAbsolutePath();
    }


    public String saveFilterImage(Context context, Bitmap bitmap, String id, String fileName){

        String hash = Util.hash(id, fileName) + "";
        String destHash = hash + "_" + new Random().nextInt(Integer.MAX_VALUE) + "";

        //같은 파일의 캐쉬가 있으면 지운다.
        String beforeFileHash = SimpleSharedPreference.with(context).get(hash);
        if(beforeFileHash != null){
            new File(getDirectory(), beforeFileHash).delete();
        }

        SimpleSharedPreference.with(context).push(hash, destHash);


        File file = new File(getDirectory(), destHash);

        if(file.exists()){
            file.delete();
        }

        try {

            //비트맵 줄이고 시작하기
            Bitmap bitmapDest = Util.makeThumbBitmap(bitmap, 200, 200);

            OutputStream outStream = new FileOutputStream(file);
            bitmapDest.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            bitmapDest.recycle();

            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void clearCache(){
        File[] listFile = getDirectory().listFiles();
        try{
            if(listFile.length > 0){
                for(int i = 0 ; i < listFile.length ; i++){
                    if(listFile[i].isFile()){
                        listFile[i].delete();
                    }else{
//                        removeDIR(listFile[i].getPath());
                    }
                    listFile[i].delete();
                }
            }
        }catch(Exception e){
            System.err.println(System.err);
            System.exit(-1);
        }
    }

    private File getDirectory(){
        File dir = new File("/sdcard/" + dirName);
        if(!dir.exists()){
            dir.mkdirs();
        }

        return dir;
    }

}
