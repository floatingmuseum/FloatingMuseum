package com.floatingmuseum.androidtest.views.simple;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;

/**
 * Created by Floatingmuseum on 2017/4/24.
 */

public class MessageProgressDialog extends AlertDialog {

    protected MessageProgressDialog(@NonNull Context context) {
        super(context);
    }

    protected MessageProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected MessageProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
