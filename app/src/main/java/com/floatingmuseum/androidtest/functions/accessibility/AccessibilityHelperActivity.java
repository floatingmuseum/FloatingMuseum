package com.floatingmuseum.androidtest.functions.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.MaskFilterSpan;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/8/25.
 */

public class AccessibilityHelperActivity extends BaseActivity {

    @BindView(R.id.tv_accessibility_state)
    TextView tvAccessibilityState;
    @BindView(R.id.bt_help_open)
    Button btHelpOpen;

    private boolean firstOpenActivity = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);
        ButterKnife.bind(this);
        btHelpOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpOpen();
            }
        });

//        AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
//        List<AccessibilityServiceInfo> installedList = manager.getInstalledAccessibilityServiceList();
//        for (AccessibilityServiceInfo info : installedList) {
//            info.loadDescription(getPackageManager());
//            Logger.d("辅助助手...已安装的服务:" + info.toString());
//            Logger.d("辅助助手...已安装的服务:" + info.loadDescription(getPackageManager()));
//
//        }
//        Logger.d("辅助助手****************************************************");
//        List<AccessibilityServiceInfo> enabledList = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
//        for (AccessibilityServiceInfo info : enabledList) {
//            Logger.d("辅助助手...已启用的服务:" + info.toString());
//        }

        checkServiceState();
    }

    private void helpOpen() {
        startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkServiceState();
    }

    private void checkServiceState() {
        boolean enabled = AccessibilityHelper.isEnabled(this, TestService.class.getCanonicalName());
        if (firstOpenActivity) {
            firstOpenActivity = false;
            if (!enabled) {
                new AlertDialog.Builder(this)
                        .setMessage("辅助功能未打开,是否去辅助功能页打开")
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        }
        setStateText(enabled);
    }

    private void setStateText(boolean enabled) {
        SpannableString stateText = new SpannableString("服务状态:" + enabled);
        ForegroundColorSpan colorSpan;
        if (enabled) {
            colorSpan = new ForegroundColorSpan(Color.GREEN);
        } else {
            colorSpan = new ForegroundColorSpan(Color.RED);
        }
//        MaskFilterSpan maskFilterSpan = new MaskFilterSpan(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
        stateText.setSpan(colorSpan, "服务状态:".length(), stateText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvAccessibilityState.setText(stateText);
    }
}
