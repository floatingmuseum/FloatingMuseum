package com.floatingmuseum.androidtest.views.simple;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;

/**
 * Created by Floatingmuseum on 2017/4/24.
 */

public class MessageProgressDialog extends Dialog {

    private Context context;

    protected MessageProgressDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    protected MessageProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected MessageProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout container = (LinearLayout) View.inflate(context,R.layout.dialog_message_progress,null);
        setContentView(container);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.CENTER;
//        lp.height = DensityUtil.dip2px(context,250);
        lp.height = 600;
//        lp.width = DensityUtil.dip2px(context,200);
        lp.width = 900;
        win.setAttributes(lp);

        TextView tvDialogMessage = (TextView) container.findViewById(R.id.tv_dialog_message);
        tvDialogMessage.setText("Look me.");
//        tvDialogMessage.setVisibility(View.VISIBLE);
    }
}
