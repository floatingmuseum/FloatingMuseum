package com.floatingmuseum.androidtest.functions.download;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.FileUtil;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Response;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by Floatingmuseum on 2017/3/7.
 * <p>
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
    @BindView(R.id.rv_download)
    RecyclerView rvDownload;
    @BindView(R.id.tv_net_speed)
    TextView tvNetSpeed;
    @BindView(R.id.rb_link_one)
    RadioButton rbLinkOne;
    @BindView(R.id.rb_link_two)
    RadioButton rbLinkTwo;
    @BindView(R.id.rb_link_three)
    RadioButton rbLinkThree;
    @BindView(R.id.rb_link_four)
    RadioButton rbLinkFour;
    @BindView(R.id.rg_link)
    RadioGroup rgLink;

    private List<DownloadItem> downloadItems;
    private DownloadListAdapter adapter;

    private String currentUrl;
    private String url1 = "http://beijing.edu505.com:8080/regionalServer//upload/3c5d8fe0-4f1d-4f4e-b7c5-59f2c4b4e9fd.apk";
    private String url2 = "http://o95ur971i.bkt.clouddn.com/newsreader_news_wxkm.apk";
    private String url3 = "http://beijing.edu505.com:8080/regionalServer//upload/299cff7a-8b21-4943-a650-469037189e61.apk";
    private String url4 = "http://dldir1.qq.com/weixin/android/weixin6330android920.apk";
    private int fileDownloaderTaskId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlist);
        ButterKnife.bind(this);
//        initSingleDownload();
        btStart.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btRemove.setOnClickListener(this);
        currentUrl = url1;
        rgLink.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonId) {
                switch (radioButtonId){
                    case R.id.rb_link_one:
                        currentUrl = url1;
                        break;
                    case R.id.rb_link_two:
                        currentUrl = url2;
                        break;
                    case R.id.rb_link_three:
                        currentUrl = url3;
                        break;
                    case R.id.rb_link_four:
                        currentUrl = url4;
                        break;
                }
            }
        });
        rvDownload.setLayoutManager(new LinearLayoutManager(this));
        downloadItems = new ArrayList<>();
        adapter = new DownloadListAdapter(downloadItems);
        rvDownload.setAdapter(adapter);
    }

    private void initSingleDownload() {
        OkGo.get(currentUrl)
                .execute(new FileCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        Logger.d("OkGo下载信息...onBefore:" + Thread.currentThread());
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Logger.d("OkGo下载信息...onError:" + Thread.currentThread());
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        ToastUtil.show("下载成功");
                        Logger.d("OkGo下载信息...onSuccess::" + Thread.currentThread() + "...Path" + file.getAbsolutePath());
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        int intProgress = (int) (progress * 100);
                        pbDownload.setProgress(intProgress);
                        tvNetSpeed.setText("下载速度:" + FileUtil.bytesToKb(networkSpeed) + "kb");
                        Logger.d("OkGo下载信息...downloadProgress:CurrentSize:" + currentSize + "...TotalSize:" + totalSize + "...Progress:" + progress + "...NetworkSpeed:" + networkSpeed + "...Thread:" + Thread.currentThread());
                    }
                });
    }

    private Disposable disposable;

    private void initRxDownload() {
        final long startTime = System.currentTimeMillis();
        disposable = RxDownload.getInstance(this)
                .maxThread(5)//5线程
                .maxRetryCount(3)//错误重试次数
                .download(currentUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DownloadStatus>() {
                    @Override
                    public void accept(@NonNull DownloadStatus status) throws Exception {
                        Logger.d("RxDownload信息...DownloadSize:" + status.getDownloadSize() + "...TotalSize" + status.getTotalSize() + "...Percent" + status.getPercentNumber());
                        pbDownload.setProgress((int) status.getPercentNumber());
                        tvNetSpeed.setText(status.getFormatDownloadSize() + "/" + status.getFormatTotalSize());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Logger.d("RxDownload信息...onError");
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        long time = System.currentTimeMillis() - startTime;
                        long resultTime = time / 1000;
                        Logger.d("RxDownload信息...onComplete:耗时" + resultTime);
                    }
                });
    }

    private BaseDownloadTask initFileDownloader() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        Logger.d("FileDownloader信息...地址:" + path);
        return FileDownloader.getImpl()
                .create(currentUrl)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Logger.d("FileDownloader信息...pending:...soFarBytes:" + soFarBytes + "...totalBytes:" + totalBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Logger.d("FileDownloader信息...progress:...soFarBytes:" + soFarBytes + "...totalBytes:" + totalBytes + "..." + task.getCallbackProgressMinInterval() + "..." + task.getCallbackProgressTimes());
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Logger.d("FileDownloader信息...completed..." + task.getFilename() + "..." + task.getPath() + "..." + task.getUrl());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Logger.d("FileDownloader信息...paused:...soFarBytes:" + soFarBytes + "...totalBytes:" + totalBytes);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Logger.d("FileDownloader信息...error");
                        e.printStackTrace();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Logger.d("FileDownloader信息...warn");
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start:
//                initSingleDownload();
//                initRxDownload();
                ToastUtil.show("Start");
                fileDownloaderTaskId = initFileDownloader().start();
                break;
            case R.id.bt_pause:
//                if (disposable != null && !disposable.isDisposed()) {
//                    disposable.dispose();
//                }
                ToastUtil.show("Pause");
                FileDownloader.getImpl().pause(fileDownloaderTaskId);
                break;
            case R.id.bt_cancel:
                break;
            case R.id.bt_remove:
                break;
        }
    }
}
