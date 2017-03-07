package com.floatingmuseum.androidtest.functions.download;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/3/7.
 *
 * 应用下载列表，多线程，后台，状态更新，断点。
 */

public class DownloadListActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.pb_download)
    ProgressBar pbDownload;
    @BindView(R.id.bt_start)
    Button btStart;
    @BindView(R.id.bt_pause)
    Button btPause;
    @BindView(R.id.bt_cancel)
    Button btCancel;
    @BindView(R.id.bt_remove)
    Button btRemove;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlist);
        ButterKnife.bind(this);
        btStart.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btRemove.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_start:
                break;
            case R.id.bt_pause:
                break;
            case R.id.bt_cancel:
                break;
            case R.id.bt_remove:
                break;
        }
    }
}
