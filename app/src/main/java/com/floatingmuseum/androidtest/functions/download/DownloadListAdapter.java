package com.floatingmuseum.androidtest.functions.download;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.utils.FileUtil;
import com.lzy.okserver.download.DownloadManager;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/3/8.
 */

public class DownloadListAdapter extends BaseQuickAdapter<DownloadItem, DownloadListAdapter.DownloadListViewHolder> {
    public DownloadListAdapter(List<DownloadItem> data) {
        super(R.layout.item_download, data);
    }

    @Override
    protected void convert(DownloadListViewHolder helper, DownloadItem item) {
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
        helper.setText(R.id.tv_net_speed, "下载速度:" + FileUtil.bytesToKb(item.getNetSpeed()) + "kb")
                .setText(R.id.tv_filename, item.getFileName())
                .setProgress(R.id.pb_progress, item.getPercent())
                .setText(R.id.bt_download_state, buttonText)
                .addOnClickListener(R.id.bt_download_state);
    }

    public class DownloadListViewHolder extends BaseViewHolder {

        public final TextView tvNetSpeed;
        public final ProgressBar pbProgress;
        public final Button btDownloadState;

        public DownloadListViewHolder(View view) {
            super(view);
            tvNetSpeed = (TextView) view.findViewById(R.id.tv_net_speed);
            pbProgress = (ProgressBar) view.findViewById(R.id.pb_progress);
            btDownloadState = (Button) view.findViewById(R.id.bt_download_state);
        }
    }
}
