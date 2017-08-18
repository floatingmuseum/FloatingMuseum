package com.floatingmuseum.androidtest.views.camera;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.google.android.exoplayer2.util.FlacStreamInfo;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class CameraActivity extends BaseActivity implements CameraCallback, View.OnClickListener {

    @BindView(R.id.camera_view)
    CameraView cameraView;
    @BindView(R.id.bt_take_photo)
    Button btTakePhoto;
    @BindView(R.id.iv_flash_mode)
    ImageView ivFlashMode;
    @BindView(R.id.iv_camera_facing)
    ImageView ivCameraFacing;
    @BindView(R.id.bottom_settings)
    RelativeLayout bottomSettings;
    @BindView(R.id.iv_settings)
    ImageView ivSettings;
    @BindView(R.id.sb_zoom)
    SeekBar sbZoom;

    private static String TAG = CameraActivity.class.getSimpleName();
    private CameraView.Photographer photographer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        cameraView.setCameraCallback(this);
        photographer = cameraView.getPhotographer();
        btTakePhoto.setOnClickListener(this);
        ivFlashMode.setOnClickListener(this);
        ivCameraFacing.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 2017/8/17 open camera
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: 2017/8/17 close camera
    }

    @Override
    public void onCameraOpened() {

    }

    @Override
    public void onPhotoTaken(File file) {
        ToastUtil.show("Photo has been taken." + file.getAbsolutePath());
        Log.d(TAG, "Photo has been taken." + file.getAbsolutePath());
    }

    @Override
    public void onCameraClosed() {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_take_photo:
                photographer.takePhoto();
                break;
            case R.id.iv_flash_mode:
                switchFlashMode();
                break;
            case R.id.iv_camera_facing:
                switchCameraFacing();
                break;
        }
    }

    private void switchCameraFacing() {
        int facing = photographer.getCameraFacing();
        if (CameraParam.CAMERA_FACING_BACK == facing) {
            photographer.switchCamera(CameraParam.CAMERA_FACING_FRONT);
            setImage(ivCameraFacing, R.drawable.ic_camera_front_white_36dp);
        } else if (CameraParam.CAMERA_FACING_FRONT == facing) {
            photographer.switchCamera(CameraParam.CAMERA_FACING_BACK);
            setImage(ivCameraFacing, R.drawable.ic_camera_back_white_36dp);
        }
    }

    private void switchFlashMode() {
        int mode = photographer.getFlashMode();
        Log.d(TAG, "Flash mode:" + ivFlashMode);
        if (CameraParam.FLASH_MODE_AUTO == mode) {
            photographer.setFlashMode(CameraParam.FLASH_MODE_CLOSE);
            setImage(ivFlashMode, R.drawable.ic_flash_off_white_36dp);
        } else if (CameraParam.FLASH_MODE_CLOSE == mode) {
            photographer.setFlashMode(CameraParam.FLASH_MODE_OPEN);
            setImage(ivFlashMode, R.drawable.ic_flash_on_white_36dp);
        } else if (CameraParam.FLASH_MODE_OPEN == mode) {
            photographer.setFlashMode(CameraParam.FLASH_MODE_AUTO);
            setImage(ivFlashMode, R.drawable.ic_flash_auto_white_36dp);
        }
    }

    private void setImage(ImageView view, int resID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(getResources().getDrawable(resID, null));
        } else {
            view.setImageDrawable(getResources().getDrawable(resID));
        }
    }
}
