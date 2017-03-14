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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.FileUtil;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;
import com.lzy.okserver.listener.DownloadListener;
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
    private DownloadManager okGoManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlist);
        ButterKnife.bind(this);
//        initSingleDownload();
        okGoManager = DownloadService.getDownloadManager();
        btStart.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btRemove.setOnClickListener(this);
        currentUrl = url1;
        rgLink.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonId) {
                switch (radioButtonId) {
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
        initDownloadItems();
        adapter = new DownloadListAdapter(downloadItems);
        rvDownload.setAdapter(adapter);
        rvDownload.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.bt_download_state) {
                    DownloadItem item = downloadItems.get(position);
                    int state = item.getDownloadState();
                    Logger.d("OkGo信息...Button点击:FileName:" + item.getFileName() + "...State:" + item.getDownloadState() + "...Percent:" + item.getPercent() + "...Url:" + item.getUrl());
                    if (state == DownloadManager.NONE || state == DownloadManager.ERROR || state == DownloadManager.PAUSE || state == DownloadManager.FINISH) {
                        startDownload(item.getUrl());
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startDownload(String url) {
        DownloadInfo info = okGoManager.getDownloadInfo(url);
        if (info != null) {
            File file = new File(info.getTargetFolder(), info.getFileName());
            Logger.d("OkGo信息...DownloadInfo:...FileName:" + info.getFileName() + "...Folder:" + info.getTargetFolder() + "...Path:" + info.getTargetPath() + "..." + file.exists());
            if (!file.exists()) {//如果数据库存在，文件却不存在，删除数据库信息，才可以重新下载
                DownloadManager.getInstance().removeTask(url, true);
            }
        }
        GetRequest request = OkGo.get(url);
        okGoManager.addTask(url, request, new DownloadListener() {
            @Override
            public void onProgress(DownloadInfo info) {
                Logger.d("OkGo信息:...onProgress:FileName:" + info.getFileName() + "...TotalLength:" + info.getTotalLength() + "...DownloadLength:" + info.getDownloadLength() + "...NetworkSpeed:" + info.getNetworkSpeed() + "...Progress:" + info.getProgress());
//                updateDownloadItem(info);
                updateItem(info);
            }

            @Override
            public void onFinish(DownloadInfo info) {
                Logger.d("OkGo信息:...onFinish:FileName:" + info.getFileName());
//                updateDownloadItem(info);
                updateItem(info);
            }

            @Override
            public void onError(DownloadInfo info, String errorMsg, Exception e) {
                Logger.d("OkGo信息:...onError:FileName:" + info.getFileName());
//                updateDownloadItem(info);
                updateItem(info);
                e.printStackTrace();
            }
        });
    }

    private void updateDownloadItem(DownloadInfo info) {
        for (DownloadItem downloadItem : downloadItems) {
            if (downloadItem.getUrl().equals(info.getUrl())) {
                downloadItem.setDownloadState(info.getState());
                downloadItem.setPercent((int) (info.getProgress() * 100));
                downloadItem.setNetSpeed(info.getNetworkSpeed());
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }


    private void initDownloadItems() {
        List<String> urlList = new ArrayList<>();
        urlList.add(url1);
        urlList.add(url2);
        urlList.add(url3);
        urlList.add(url4);
        for (int x = 0; x < urlList.size(); x++) {
            String url = urlList.get(x);
            DownloadItem item = new DownloadItem();
            item.setFileName(FileUtil.getUrlFileName(url));
            item.setUrl(url);
            DownloadInfo info1 = okGoManager.getDownloadInfo(url);
            if (info1 != null) {
                item.setPercent((int) (info1.getProgress() * 100));
                item.setDownloadState(info1.getState());
                File file = new File(info1.getTargetFolder(), info1.getFileName());
                if (!file.exists()) {
                    item.setPercent(0);
                    item.setDownloadState(DownloadManager.NONE);
                }
            } else {
                item.setPercent(0);
                item.setDownloadState(DownloadManager.NONE);
            }
            downloadItems.add(item);
        }
    }

    private void initOkGoDownload() {
        GetRequest request = OkGo.get(currentUrl);
        //检查数据库中是否有此tag的任务
        DownloadInfo info = okGoManager.getDownloadInfo(currentUrl);
        if (info != null) {
            File file = new File(info.getTargetFolder(), info.getFileName());
            Logger.d("OkGo信息...DownloadInfo:...FileName:" + info.getFileName() + "...Folder:" + info.getTargetFolder() + "...Path:" + info.getTargetPath() + "..." + file.exists());

            if (!file.exists()) {//如果数据库存在，文件却不存在，删除数据库信息，才可以重新下载
                DownloadManager.getInstance().removeTask(currentUrl, true);
            }
        }
        okGoManager.addTask(currentUrl, request, new DownloadListener() {
            @Override
            public void onProgress(DownloadInfo info) {
                Logger.d("OkGo信息:...onProgress:" + "...TotalLength:" + info.getTotalLength() + "...DownloadLength:" + info.getDownloadLength() + "...NetworkSpeed:" + info.getNetworkSpeed() + "...Progress:" + info.getProgress());
                int intProgress = (int) (info.getProgress() * 100);
                pbDownload.setProgress(intProgress);
                tvNetSpeed.setText("下载速度:" + FileUtil.bytesToKb(info.getNetworkSpeed()) + "kb");
            }

            @Override
            public void onFinish(DownloadInfo downloadInfo) {
                Logger.d("OkGo信息:...onFinish");
            }

            @Override
            public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
                Logger.d("OkGo信息:...onError");
                e.printStackTrace();
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
//                initRxDownload();
                ToastUtil.show("Start");
//                fileDownloaderTaskId = initFileDownloader().start();
                initOkGoDownload();
                break;
            case R.id.bt_pause:
//                if (disposable != null && !disposable.isDisposed()) {
//                    disposable.dispose();
//                }
                ToastUtil.show("Pause");
//                FileDownloader.getImpl().pause(fileDownloaderTaskId);
                DownloadManager.getInstance().pauseTask(currentUrl);
                break;
            case R.id.bt_cancel:
                break;
            case R.id.bt_remove:
                break;
        }
    }

    private void updateItem(DownloadInfo info) {
        for (int i = 0; i < downloadItems.size(); i++) {
            DownloadItem item = downloadItems.get(i);
            if (item.getUrl().equals(info.getUrl())) {
                item.setDownloadState(info.getState());
                item.setPercent((int) (info.getProgress() * 100));
                item.setNetSpeed(info.getNetworkSpeed());
                DownloadListAdapter.DownloadListViewHolder holder = (DownloadListAdapter.DownloadListViewHolder) rvDownload.findViewHolderForAdapterPosition(i);
                holder.tvNetSpeed.setText("下载速度:" + FileUtil.bytesToKb(item.getNetSpeed()) + "kb");
                holder.pbProgress.setProgress(item.getPercent());
                int state = item.getDownloadState();
                String buttonText;
                switch (state) {
                    case DownloadManager.NONE:
                        buttonText = "下载";
                        break;
                    case DownloadManager.WAITING:
                        buttonText = "等待";
                        break;
                    case DownloadManager.DOWNLOADING:
                        buttonText = "下载中";
                        break;
                    case DownloadManager.PAUSE:
                        buttonText = "暂停";
                        break;
                    case DownloadManager.FINISH:
                        buttonText = "完成";
                        break;
                    case DownloadManager.ERROR:
                        buttonText = "错误";
                        break;
                    default:
                        buttonText = "下载";
                        break;
                }
                holder.btDownloadState.setText(buttonText);
            }
        }
    }
}
