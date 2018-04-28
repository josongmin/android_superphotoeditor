/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.superfamous.superphotopicker.photofilter.utility;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLException;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class GLToolbox {

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                String info = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);
                throw new RuntimeException("Could not compile shader " + shaderType + ":" + info);
            }
        }
        return shader;
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus,
                    0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                String info = GLES20.glGetProgramInfoLog(program);
                GLES20.glDeleteProgram(program);
                throw new RuntimeException("Could not link program: " + info);
            }
        }
        return program;
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public static void initTexParams() {

        //이미지 품질 알고리즘 설정
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    /**그려지고 있는 그림만 비트맵으로 저장*/
    public static final Bitmap createBoundedBitmapFromGLSurface(GL10 gl, final int viewWidth, final int viewHeight, final int imgWidth, final int imgHeight){
        Bitmap bitmap = null;

        float imgAspectRatio = imgWidth / (float)imgHeight;
        float viewAspectRatio = viewWidth / (float)viewHeight;
        float relativeAspectRatio = viewAspectRatio / imgAspectRatio;

        int tobeHeight, tobeWidth, tobeX, tobeY;

        if (relativeAspectRatio < 1.0f) { //width에 맞춤
            System.out.println("widtht에 마춤");
            float widthRatio = viewWidth / (float)imgWidth;
            tobeHeight = (int)(imgHeight * widthRatio);
            tobeWidth = viewWidth;
            tobeX = 0;
            tobeY = (viewHeight - tobeHeight) / 2;

            System.out.println("tobeWidth x tobeHeight " + tobeWidth + " x " +  tobeHeight);
            System.out.println("tobeX x tobeY " + tobeX + " x " +  tobeY);

        }else{
            System.out.println("height에 마춤");

            float heightRatio = viewHeight / (float)imgHeight;
            tobeWidth = (int)(imgWidth * heightRatio);
            tobeHeight = viewHeight;
            tobeX = (viewWidth - tobeWidth) / 2;
            tobeY = 0;

            System.out.println("tobeWidth x tobeHeight " + tobeWidth + " x " +  tobeHeight);
            System.out.println("tobeX x tobeY " + tobeX + " x " +  tobeY);

        }

        bitmap = createBitmapFromGLSurface(tobeX, tobeY, tobeWidth, tobeHeight, gl);
        return bitmap;
    }


    /**배경 포함해서 비트맵 저장*/
    public static final Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl) throws OutOfMemoryError {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.RGB_565);
    }
}
