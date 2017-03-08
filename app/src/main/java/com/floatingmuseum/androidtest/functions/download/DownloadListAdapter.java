package com.floatingmuseum.androidtest.functions.download;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.R;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/3/8.
 */

public class DownloadListAdapter extends BaseQuickAdapter<DownloadItem,BaseViewHolder> {
    public DownloadListAdapter(List<DownloadItem> data) {
        super(R.layout.item_download, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownloadItem item) {

    }
}
