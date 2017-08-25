package com.floatingmuseum.androidtest.views.camera;

import android.content.Context;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.widget.FrameLayout;

import java.io.File;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class CameraView extends FrameLayout implements CameraStateCallback {

    private static String TAG = CameraView.class.getSimpleName();
    private Context context;
    private CameraPreview cameraPreview;
    private CameraImpl camera;
    private CameraCallback cameraCallback;
    private Photographer photographer;

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
        photographer = new Photographer();
        Log.d(TAG, "Photographer new:" + photographer);
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
        camera.setOutputs(CameraParam.CAMERA_FACING_BACK, width, height);
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

    public Photographer getPhotographer() {
        return photographer;
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

    public class Photographer {

        private Photographer() {
        }

        public void openCamera() {
            if (cameraPreview.isAvailable()){
                CameraView.this.openCamera(context, cameraPreview.previewWidth, cameraPreview.previewHeight);
            }else{
                initPreview(context);
            }
        }

        public void closeCamera() {
            camera.closeCamera();
        }

        public void takePhoto() {
            camera.takePhoto();
        }

        public void setFlashMode(int flashMode) {
            camera.switchFlashMode(flashMode);
        }

        public int getFlashMode() {
            return camera.getFlashMode();
        }

        public void switchCamera(int facing) {
            camera.switchCameraFacing(facing);
        }

        public int getCameraFacing() {
            return camera.getCameraFacing();
        }

        public void getResolutions(String cameraID) {
        }

        public void setResolution(Size size) {
        }

        /**
         * @param value 0 to 100
         */
        public void zoomTo(int value) {
        }
    }
}
