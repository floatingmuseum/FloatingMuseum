package com.floatingmuseum.androidtest.functions.download;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.utils.FileUtil;

import java.util.List;

import floatingmuseum.sonic.Sonic;

/**
 * Created by Floatingmuseum on 2017/3/8.
 */

public class TasksAdapter extends BaseQuickAdapter<AppInfo, TasksAdapter.TaskViewHolder> {
    public TasksAdapter(List<AppInfo> data) {
        super(R.layout.item_download, data);
    }

    @Override
    protected void convert(TaskViewHolder helper, AppInfo appInfo) {
        String fileName = FileUtil.getFileName(appInfo.getName());
        helper.setText(R.id.tv_name, fileName)
                .addOnClickListener(R.id.bt_task_state)
                .addOnClickListener(R.id.bt_task_cancel);
//        Log.i(TAG, "onBindViewHolder()...AppInfo:" + appInfo.toString());
        if (appInfo.getTotalSize() != 0) {
            helper.setText(R.id.tv_size, "Size:" + appInfo.getCurrentSize() + "/" + appInfo.getTotalSize())
                    .setProgress(R.id.pb_task, appInfo.getProgress())
                    .setText(R.id.tv_progress, "Progress:" + appInfo.getProgress() + "%");
        } else {
            helper.setText(R.id.tv_size, "Size:0/unknown")
                    .setProgress(R.id.pb_task, 0)
                    .setText(R.id.tv_progress, "Progress:0%");
        }
        String taskState = null;
        switch (appInfo.getState()) {
            case Sonic.STATE_NONE:
                taskState = "下载";
                break;
            case Sonic.STATE_START:
                taskState = "下载";
                break;
            case Sonic.STATE_WAITING:
                taskState = "等待";
                break;
            case Sonic.STATE_PAUSE:
                taskState = "继续";
                break;
            case Sonic.STATE_DOWNLOADING:
                taskState = "暂停";
                break;
            case Sonic.STATE_FINISH:
                taskState = "完成";
                break;
            case Sonic.STATE_ERROR:
                taskState = "错误";
                break;
        }

        helper.setText(R.id.bt_task_state, taskState);
    }

    public class TaskViewHolder extends BaseViewHolder {

        TextView tvName;
        TextView tvSize;
        TextView tvProgress;
        ProgressBar pbTask;
        Button btTaskState;
        Button btTaskCancel;

        public TaskViewHolder(View view) {
            super(view);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvSize = (TextView) itemView.findViewById(R.id.tv_size);
            tvProgress = (TextView) itemView.findViewById(R.id.tv_progress);
            pbTask = (ProgressBar) itemView.findViewById(R.id.pb_task);
            btTaskState = (Button) itemView.findViewById(R.id.bt_task_state);
            btTaskCancel = (Button) itemView.findViewById(R.id.bt_task_cancel);
        }
    }
}
