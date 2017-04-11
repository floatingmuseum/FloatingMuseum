package com.floatingmuseum.androidtest.functions.catchtime;


import android.text.format.DateUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.App;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.utils.TimeUtil;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/4/10.
 */
public class CatchTimeAdapter extends BaseQuickAdapter<AppTimeInfo, BaseViewHolder> {

    private List<AppTimeInfo> showingList;

    public CatchTimeAdapter(List<AppTimeInfo> showingList) {
        super(R.layout.item_catch_time, showingList);
        this.showingList = showingList;
    }

    @Override
    protected void convert(BaseViewHolder helper, AppTimeInfo item) {
        if (helper.getLayoutPosition() == 0 || item.getDayStartTime() != showingList.get(helper.getLayoutPosition() - 1).getDayStartTime()) {//可见
            helper.setText(R.id.tv_day_time, "日期:" + TimeUtil.getTime(item.getDayStartTime()))
                    .setVisible(R.id.tv_day_time, true);
//            DateUtils.formatDateTime(App.context, item.getDayStartTime(), DateUtils.FORMAT_SHOW_DATE)
        } else {
            helper.setVisible(R.id.tv_day_time, false);
        }

        helper.setText(R.id.tv_app_name, "应用名称:" + item.getName())
                .setText(R.id.tv_package_name, "应用包名:" + item.getPackageName())
                .setText(R.id.tv_app_using_time, "应用当日使用时间:" + TimeUtil.getUsingTime(item.getUsingTime()));
    }
}
