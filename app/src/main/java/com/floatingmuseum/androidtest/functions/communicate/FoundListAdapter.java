package com.floatingmuseum.androidtest.functions.communicate;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.R;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/6/19.
 */

public class FoundListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public FoundListAdapter(@Nullable List<String> data) {
        super(R.layout.item_communicate, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_endpointID, item);
    }
}
