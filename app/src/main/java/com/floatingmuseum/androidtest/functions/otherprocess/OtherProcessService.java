package com.floatingmuseum.androidtest.functions.otherprocess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Floatingmuseum on 2017/6/13.
 */

public class OtherProcessService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
