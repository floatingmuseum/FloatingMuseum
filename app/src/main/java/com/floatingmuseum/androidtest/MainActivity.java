package com.floatingmuseum.androidtest;

import android.app.ActivityManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.functions.FunctionsActivity;
import com.floatingmuseum.androidtest.thirdpartys.ThirdPartiesActivity;
import com.floatingmuseum.androidtest.utils.SystemUtil;
import com.floatingmuseum.androidtest.views.ViewActivity;
import com.orhanobut.logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_views)
    Button btViews;
    @BindView(R.id.bt_functions)
    Button btFunctions;
    @BindView(R.id.bt_third_parties)
    Button btThirdParties;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btViews.setOnClickListener(this);
        btFunctions.setOnClickListener(this);
        btThirdParties.setOnClickListener(this);


        List<String> oldList = new ArrayList<>();
        oldList.add("1");
        oldList.add("2");
        oldList.add("3");
        oldList.add("4");
        oldList.add("5");
        oldList.add("6");
        oldList.add("7");
        oldList.add("8");
        oldList.add("9");
        Logger.d("差集测试...数据库数据:" + oldList);
        List<String> newList = new ArrayList<>();
        newList.add("2");
        newList.add("3");
        newList.add("4");
        newList.add("5");
        newList.add("6");
        newList.add("7");
        newList.add("8");
        newList.add("9");
        newList.add("10");
        Logger.d("差集测试...数据库数据:" + newList);
        long startTime = System.currentTimeMillis();
        List<String> tempList = new ArrayList<>();
        tempList.addAll(oldList);
        boolean isChanged1 = tempList.removeAll(newList);
        Logger.d("差集测试...需要从数据库删除:" + tempList + "...数据是否改变:" + isChanged1);
        tempList.clear();
        tempList.addAll(newList);
        boolean isChanged2 = tempList.removeAll(oldList);
        Logger.d("差集测试...需要添加到数据库:" + tempList + "...数据是否改变:" + isChanged2);
        Logger.d("差集测试...耗时:" + (System.currentTimeMillis() - startTime));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_views:
                startActivity(ViewActivity.class);
                break;
            case R.id.bt_functions:
                startActivity(FunctionsActivity.class);
                break;
            case R.id.bt_third_parties:
                startActivity(ThirdPartiesActivity.class);
//                int x = 1 / 0;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
