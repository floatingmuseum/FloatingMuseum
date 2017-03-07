package com.floatingmuseum.androidtest.functions.catchtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Floatingmuseum on 2017/3/7.
 */

public class SystemShutAndBootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SHUTDOWN:
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                break;
        }
    }
}
