package com.floatingmuseum.androidtest.functions.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;

import java.util.concurrent.Semaphore;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/7/26.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera1Activity extends BaseActivity {

    @BindView(R.id.camera_view)
    TextureView cameraView;
    @BindView(R.id.bt_take_picture)
    Button btTakePicture;

    private String tag = Camera1Activity.class.getSimpleName();
    private CameraManager cameraMagager;
    private String cameraID;
    private CameraCaptureSession captureSession;
    private CameraDevice device;
    private Size previewSize;
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);
        ButterKnife.bind(this);

        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera(width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                configureTransform(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

//    private void initPermission() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermission(Manifest.permission.CAMERA);
//        } else {
//        }
//    }

    private void requestPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 1024);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1024) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ToastUtil.show("权限获取成功");
            } else {
                ToastUtil.show("权限获取被拒绝");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openCamera(int width, int height) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA);
            return;
        }
        cameraMagager = (CameraManager) getSystemService(CAMERA_SERVICE);

    }

    private void configureTransform(int width, int height) {
    }

    private void takePicture() {
    }

    private void createCameraPreviewSession() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Logger.d(tag + "...StateCallback...onOpened:" + camera.getId());
            device = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Logger.d(tag + "...StateCallback...onDisconnected:" + camera.getId());
            camera.close();
            device = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Logger.d(tag + "...StateCallback...onError:" + camera.getId() + "...error:" + error);
            camera.close();
            device = null;
        }
    };
}
