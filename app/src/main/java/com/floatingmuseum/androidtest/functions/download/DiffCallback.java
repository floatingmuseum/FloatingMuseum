package com.floatingmuseum.androidtest.functions.download;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.floatingmuseum.androidtest.functions.catchtime.AppTimeUsingInfo;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/5/21.
 */

public class DiffCallback extends DiffUtil.Callback {
    private List<AppInfo> oldList;
    private List<AppInfo> newList;

    public DiffCallback(List<AppInfo> oldList, List<AppInfo> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getUrl().equals(newList.get(newItemPosition).getUrl());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getUrl().equals(newList.get(newItemPosition).getUrl());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
