package com.superfamous.superphotopicker.photofilter.renderer;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.superfamous.superphotopicker.photofilter.utility.GLTextureHelper;
import com.superfamous.superphotopicker.photofilter.utility.GLToolbox;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by josongmin on 2016-04-02.
 */
public class FilterRenderer implements GLSurfaceView.Renderer{


    public final static int EFFECT_NO       = 0;
    public final static int EFFECT_BRIGHT   = 1;
    public final static int EFFECT_SEPIA    = 2;
    private static final Object syncObj = "SYNC";

    private Bitmap bitmapTarget, bitmapOriginal;
    private int viewWidth, viewHeight;
    private int imageWidth, imageHeight;
    private int rotation;
    private boolean isFirstSetup, bitmapChanged, adjustMode;
    //
    private GLTextureHelper glTextureHelper;
    private GLSurfaceView glSurfaceView;
    private GL10 gl;
    //

    private Filter filter;
    private EffectContext effectContext;
    private Effect effect;

    //
    private OnSnapShotListener onSnapShotListener;
    private MultiImageFilterListener multiImageFilterListener;

    private int[] arrTextureHandle = new int[2]; //텍스쳐 들어갈거

    public FilterRenderer(GLSurfaceView glSurfaceView) {
        this.glTextureHelper = new GLTextureHelper();
        this.glSurfaceView = glSurfaceView;

        glSurfaceView.setEGLContextClientVersion(2); //OepnGL ES 버전 2쓴다는 얘기
        glSurfaceView.setRenderer(this); //렌더러 설정. 여기에 implements했음.
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); //requestRender()할떄랑 surface최초 그려질때만 그려짐. 기본값은 그냥 기려지는거

        isFirstSetup = true;
        initParam();
    }

    private void initParam(){
        filter = null;
        rotation = 0;
    }


    public void makeMuiltDrawingBitmapAfterDrawFrame(MultiImageFilterListener multiImageFilterListener){
        this.multiImageFilterListener = multiImageFilterListener;
    }

    public void getDrawingBitmapAfterDrawFrame(OnSnapShotListener onSnapShotListener){
        this.onSnapShotListener = onSnapShotListener;
    }

    public boolean isReady(){
        if(bitmapOriginal == null){
            return false;
        }else{
            return true;
        }
    }


    public synchronized void setImageBitmap(Bitmap bitmap, boolean adjustMode){
        this.adjustMode = adjustMode;
        if(bitmapOriginal != null) bitmapOriginal.recycle();

        if(bitmap == null) return;

        //편집모드일경우 원본 살려놓음
        if(adjustMode){
            this.bitmapOriginal = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            setTargetBitmap(bitmap.copy(Bitmap.Config.ARGB_8888,true));
        }
        //걍 한번 부르는 경우
        else{
            this.bitmapOriginal = bitmap;
            setTargetBitmap(bitmapOriginal);
        }

        initParam();

    }
    /**비트맵 소스 설정. 항상 최초로 설정되어야함*/
    public synchronized void setImageBitmap(Bitmap bitmap){
        setImageBitmap(bitmap, true);
    }

    /**그려질 비트맵 설정*/
    public void setTargetBitmap(Bitmap bitmapTarget){
        this.bitmapTarget = bitmapTarget;
        this.imageWidth = bitmapTarget.getWidth();
        this.imageHeight = bitmapTarget.getHeight();

        bitmapChanged = true;
    }

    /**회전*/
    public void rotateBy(int rotation){
        this.rotation += rotation;
        initTargetImageAfterChangeRotation();
    }

    /**회전*/
    public void rotate(int rotation){
        this.rotation = rotation;
        initTargetImageAfterChangeRotation();
    }

    /**필터적용. 이후에는 draw해줘야함*/
    public void setEffectFilter(Filter filter){
        this.filter = filter;
    }

    /**그리는거 요청*/
    public void requestRender(){
        glSurfaceView.requestRender();
    }


    /**끝*/
    public void destroy(){
        if(bitmapOriginal != null && !bitmapOriginal.isRecycled()) bitmapOriginal.recycle();
        if(bitmapTarget != null && !bitmapTarget.isRecycled()) bitmapTarget.recycle();

    }


    /**회전 변경 후 그려지는 비트맵 설정*/
    private void initTargetImageAfterChangeRotation(){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        Bitmap bitmapDest = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, true);

        setTargetBitmap(bitmapDest);
    }

    //그려지는곳. 백그라운드로 작업됨
    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (FilterRenderer.class){
            if(isFirstSetup){ //최초 세팅. 여기서 설정하는 것들은 onDrawFrame에서만 가능
                effectContext = EffectContext.createWithCurrentGlContext(); //이펙트관리자 설정
                glTextureHelper.init(); //텍스쳐 설정
                isFirstSetup = false;
            }

            if(bitmapChanged){
                freeAllTexture();
                initTexture(); // 변경된 이미지로 텍스쳐 불러옴
                bitmapChanged = false;
            }


            //멀티 샷. 한번 그리고 끝
            if(multiImageFilterListener != null){
                Bitmap[] arrBitmap = new Bitmap[multiImageFilterListener.getListFilter().size()];
                int i = 0;
                for(Filter filter : multiImageFilterListener.getListFilter()){

                    FilterRenderer.this.filter =  filter;
                    applyEffect();

                    renderResult(gl);
                    arrBitmap[i++] = GLToolbox.createBoundedBitmapFromGLSurface(gl, viewWidth, viewHeight, imageWidth, imageHeight);

                }

                multiImageFilterListener.onMultiSnapShotCreated((arrBitmap));
                multiImageFilterListener = null;


            }
            else{
                // 일반 그리기
                applyEffect();
                //그리기
                renderResult(gl);

                //리스너 있으면 실행.
                if(onSnapShotListener != null){
                    Bitmap bitmap = GLToolbox.createBoundedBitmapFromGLSurface(gl, viewWidth, viewHeight, imageWidth, imageHeight);
                    onSnapShotListener.onSnapShotCreated(bitmap); onSnapShotListener = null;
                }
            }
        }
    }



    //변경된 이미지로 텍스쳐 불러오기
    private void initTexture(){
        glTextureHelper.updateTextureSize(imageWidth, imageHeight);

        // Generate textures
        GLES20.glGenTextures(arrTextureHandle.length, arrTextureHandle, 0); //생성할 텍스쳐 수와 배열넘김. 텍스쳐의 번호 반환
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, arrTextureHandle[0]); //arrTextureHandle[0]을 2D로 쓰겠다는 얘기다.

        // glTexImage2D
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTarget, 0);
        GLToolbox.initTexParams();

    }

    //이펙트설정. onDrawFrame에서 실해되어야함
    private void applyEffect(){

        if(filter == null || filter.isOriginal()){
            filter = null;
            return;
        }

        boolean fitstFilter = true;
        EffectFactory effectFactory = effectContext.getFactory();

        for(Filter.FilterModel filterModel : filter.getListFilters()){

            effect = effectFactory.createEffect(filterModel.effectType);
            List<Object> params = filterModel.listParmas;
            for(int i = 0; i < params.size(); i+=2){
                effect.setParameter((String)params.get(i), params.get(i+1));
            }

            if(fitstFilter){
                //첨에는 원본에서 대상으로
                effect.apply(arrTextureHandle[0], imageWidth, imageHeight, arrTextureHandle[1]);
            }else{
                //그담부턴 필터 덧붙이기
                effect.apply(arrTextureHandle[1], imageWidth, imageHeight, arrTextureHandle[1]);
                fitstFilter = false;
            }
        }
    }

    /**그리기*/
    private void renderResult(GL10 gl) {
        if(filter != null){
            glTextureHelper.renderTexture(arrTextureHandle[1]);
        }else{
            glTextureHelper.renderTexture(arrTextureHandle[0]);
        }
    }

    private void freeAllTexture(){
        if(arrTextureHandle != null){
            gl.glDeleteTextures(arrTextureHandle.length, arrTextureHandle, 0);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.gl = gl;
        //do nothing
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glTextureHelper.updateViewSize(width, height);
        this.viewHeight = height;
        this.viewWidth = width;

    }

    public interface OnSnapShotListener{
        public void onSnapShotCreated(Bitmap bitmap);
    }

}
