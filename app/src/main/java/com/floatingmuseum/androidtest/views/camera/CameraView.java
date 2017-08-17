package com.floatingmuseum.androidtest.views.camera;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.io.File;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class CameraView extends FrameLayout implements CameraStateCallback {

    public static final int CAMERA_FACING_FRONT = 0;
    public static final int CAMERA_FACING_BACK = 1;
    public static final int CAMERA_FACING_OTHER = 2;

    private Context context;
    private CameraPreview cameraPreview;
    private CameraImpl camera;
    private CameraCallback cameraCallback;
    private int facing = CAMERA_FACING_BACK;

    public CameraView(@NonNull Context context) {
        this(context, null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initPreview(context);
    }

    private void initPreview(Context context) {
        if (Build.VERSION.SDK_INT < 14) {
            cameraPreview = new SurfacePreview(context, this, previewCallback);
        } else {
            cameraPreview = new TexturePreview(context, this, previewCallback);
        }
    }

    private void openCamera(Context context, int width, int height) {
        if (Build.VERSION.SDK_INT < 21) {
            camera = new Camera1(context, cameraPreview, this);
        } else {
            camera = new Camera2(context, cameraPreview, this);
        }
        camera.setOutputs(facing, width, height);
        camera.configureTransform(width, height);
        camera.openCamera();
    }

    public void setCameraCallback(CameraCallback cameraCallback) {
        this.cameraCallback = cameraCallback;
    }

    @Override
    public void onPhotoTaken(File photoFile) {
        if (cameraCallback != null) {
            cameraCallback.onPhotoTaken(photoFile);
        }
    }

    public CameraImpl getCamera() {
        // TODO: 2017/8/17 返回CameraImpl不太好 
        return camera;
    }

    private PreviewCallback previewCallback = new PreviewCallback() {
        @Override
        void onPreviewAvailable(int width, int height) {
            openCamera(context, width, height);
        }

        @Override
        void onPreviewSizeChanged(int width, int height) {
            camera.configureTransform(width, height);
        }
    };
}
