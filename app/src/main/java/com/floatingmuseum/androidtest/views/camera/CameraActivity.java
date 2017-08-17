package com.floatingmuseum.androidtest.views.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class CameraActivity extends BaseActivity implements CameraCallback {

    @BindView(R.id.camera_view)
    CameraView cameraView;
    @BindView(R.id.bt_take_photo)
    Button btTakePhoto;

    private static String TAG = CameraActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        cameraView.setCameraCallback(this);
        btTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        Log.d(TAG, "Photo has been taken." + file.getAbsolutePath());
    }

    @Override
    public void onCameraClosed() {

    }

    @Override
    public void onError(Exception e) {

    }
}
