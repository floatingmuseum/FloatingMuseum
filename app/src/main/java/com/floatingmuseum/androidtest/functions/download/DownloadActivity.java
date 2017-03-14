package com.floatingmuseum.androidtest.functions.download;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.FileUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DownloadActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.pb_single_task)
    ProgressBar pbSingleTask;
    @BindView(R.id.rv_multitask)
    RecyclerView rvMultitask;
    @BindView(R.id.bt_single_task_start)
    Button btSingleTaskStart;
    @BindView(R.id.bt_single_task_stop)
    Button btSingleTaskStop;

    private String url1 = "http://beijing.edu505.com:8080/regionalServer//upload/3c5d8fe0-4f1d-4f4e-b7c5-59f2c4b4e9fd.apk";
    private String url2 = "http://o95ur971i.bkt.clouddn.com/newsreader_news_wxkm.apk";
    private String url3 = "http://beijing.edu505.com:8080/regionalServer//upload/299cff7a-8b21-4943-a650-469037189e61.apk";
    private String url4 = "http://dldir1.qq.com/weixin/android/weixin6330android920.apk";
    private FileInfo singleTaskFileInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
        btSingleTaskStart.setOnClickListener(this);
        btSingleTaskStop.setOnClickListener(this);
    }

    private void initData() {
        singleTaskFileInfo = new FileInfo(0, url1, FileUtil.getUrlFileName(url1), 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_single_task_start:
                sendCommandToService(new Intent(this, DownloadService.class).setAction(DownloadService.ACTION_START).putExtra(DownloadService.EXTRA_FILE_INFO, singleTaskFileInfo));
                break;
            case R.id.bt_single_task_stop:
                sendCommandToService(new Intent(this, DownloadService.class).setAction(DownloadService.ACTION_STOP).putExtra(DownloadService.EXTRA_FILE_INFO, singleTaskFileInfo));
                break;
        }
    }

    private void sendCommandToService(Intent intent) {
        startService(intent);
    }
}
