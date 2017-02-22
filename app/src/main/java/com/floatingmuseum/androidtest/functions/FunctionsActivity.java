package com.floatingmuseum.androidtest.functions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.functions.autoinstall.AutoInstallActivity;
import com.floatingmuseum.androidtest.functions.camera.CameraActivity;
import com.floatingmuseum.androidtest.functions.exception.ExceptionActivity;
import com.floatingmuseum.androidtest.functions.shell.ShellActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class FunctionsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_auto_install)
    Button btAutoInstall;
    @BindView(R.id.bt_shell)
    Button btShell;
    @BindView(R.id.bt_camera)
    Button btCamera;
    @BindView(R.id.bt_exception)
    Button btException;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);
        ButterKnife.bind(this);

        btAutoInstall.setOnClickListener(this);
        btShell.setOnClickListener(this);
        btCamera.setOnClickListener(this);
        btException.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_auto_install:
                startActivity(AutoInstallActivity.class);
                break;

            case R.id.bt_shell:
                startActivity(ShellActivity.class);
                break;
            case R.id.bt_camera:
                startActivity(CameraActivity.class);
                break;
            case R.id.bt_exception:
                startActivity(ExceptionActivity.class);
                break;
        }
    }
}
