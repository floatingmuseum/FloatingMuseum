package com.floatingmuseum.androidtest.views.camera;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class CameraView extends FrameLayout {

    public static final int CAMERA_FACING_FRONT = 0;
    public static final int CAMERA_FACING_BACK = 1;
    public static final int CAMERA_FACING_OTHER = 2;

    private Context context;
    private CameraPreview cameraPreview;
    private CameraImpl camera;
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

    private void openCamera(Context context, int width, int height) {
        CameraStateCallback stateCallback = new CameraStateCallback();
        if (Build.VERSION.SDK_INT < 21) {
            camera = new Camera1(context, cameraPreview,stateCallback);
        } else {
            camera = new Camera2(context, cameraPreview,stateCallback);
        }
    }

    private void initPreview(Context context) {
        if (Build.VERSION.SDK_INT < 14) {
            cameraPreview = new SurfacePreview(context, this);
        } else {
            cameraPreview = new TexturePreview(context, this);
        }
    }

    public class CameraStateCallback {
    }

    private PreviewCallback previewCallback = new PreviewCallback() {
        @Override
        void onPreviewAvailable(int width, int height) {
            openCamera(context, width, height);
        }

        @Override
        void onPreviewSizeChanged(int width, int height) {

        }
    };
}
