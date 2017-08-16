package com.floatingmuseum.androidtest.views.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class CameraActivity extends BaseActivity {

    @BindView(R.id.camera_view)
    CameraView cameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
    }
}
