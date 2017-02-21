package com.floatingmuseum.androidtest.functions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.functions.autoinstall.AutoInstallActivity;
import com.floatingmuseum.androidtest.functions.camera.CameraActivity;
import com.floatingmuseum.androidtest.functions.shell.ShellActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class FunctionsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_auto_install)
    TextView tvAutoInstall;
    @BindView(R.id.tv_shell)
    TextView tvShell;
    @BindView(R.id.tv_camera)
    TextView tvCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);
        ButterKnife.bind(this);

        tvAutoInstall.setOnClickListener(this);
        tvShell.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_auto_install:
                startActivity(AutoInstallActivity.class);
                break;

            case R.id.tv_shell:
                startActivity(ShellActivity.class);
                break;
            case R.id.tv_camera:
                startActivity(CameraActivity.class);
                break;
        }
    }
}
