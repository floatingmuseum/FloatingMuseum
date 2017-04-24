package com.floatingmuseum.androidtest.views.simple;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Floatingmuseum on 2017/4/14.
 */

public class SimpleViewActivity extends BaseActivity {

    @BindView(R.id.tv_switch_state)
    TextView tvSwitchState;
    @BindView(R.id.switch_view)
    Switch switchView;
    @BindView(R.id.bt_open_dialog)
    Button btOpenDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_view);
        ButterKnife.bind(this);

        initSwitchView();
        initMessageProgressDialog();
    }

    private void initSwitchView() {
//        int colorOn = 0xFF323E46;
//        int colorOff = 0xFF666666;
//        int colorDisabled = 0xFF333333;
//        StateListDrawable thumbStates = new StateListDrawable();
//        thumbStates.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(colorOn));
//        thumbStates.addState(new int[]{-android.R.attr.state_enabled}, new ColorDrawable(colorDisabled));
//        thumbStates.addState(new int[]{}, new ColorDrawable(colorOff)); // this one has to come last
//        switchView.setThumbDrawable(thumbStates);

        tvSwitchState.setText(switchView.isChecked() ? "已开启" : "已关闭");
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.d("SimpleView...Switch:" + buttonView.toString() + "..." + isChecked);
                changeSwitchStateText(isChecked);
            }
        });
    }

    private void changeSwitchStateText(final boolean isChecked) {
        switchView.setEnabled(false);
        String stateText = isChecked ? "开启中" : "关闭中";
        tvSwitchState.setText("Switch:" + stateText);
        //延时3秒,模拟WiFi或者蓝牙打开时的状态,避免频繁点击
        Flowable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        String stateText = isChecked ? "已开启" : "已关闭";
                        tvSwitchState.setText("Switch:" + stateText);
                        switchView.setEnabled(true);
                    }
                });
    }

    private void initMessageProgressDialog() {
        btOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MessageProgressDialog dialog = (MessageProgressDialog) new MessageProgressDialog.Builder(SimpleViewActivity.this)
//                        .setMessage("数据加载中...")
//                        .setCancelable(true)
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).create();
//                dialog.show();
                AlertDialog dialog = new AlertDialog.Builder(SimpleViewActivity.this)
                        .setMessage("数据加载中")
                        .setView(R.layout.dialog_message_progress)
                        .setCancelable(true)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }
}
