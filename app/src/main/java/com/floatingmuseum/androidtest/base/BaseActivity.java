package com.floatingmuseum.androidtest.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.floatingmuseum.androidtest.functions.phoenixservice.PhoenixService;
import com.orhanobut.logger.Logger;


/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public static final String ACTION_SHUT_APP = "actionShutApp";
    private DestroyAllActivitiesReceiver destroyAllActivitiesReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        destroyAllActivitiesReceiver = new DestroyAllActivitiesReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHUT_APP);
        LocalBroadcastManager.getInstance(this).registerReceiver(destroyAllActivitiesReceiver,filter);
    }

    protected void startActivity(Class targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    protected void stopApp(){
        Intent intent = new Intent(this,DestroyAllActivitiesReceiver.class);
        intent.setAction(ACTION_SHUT_APP);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (destroyAllActivitiesReceiver!=null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(destroyAllActivitiesReceiver);
        }
    }

    private class DestroyAllActivitiesReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action==null || !ACTION_SHUT_APP.equals(action)) {
                return;
            }
            Logger.d("Finish all activities");
            stopService(new Intent(BaseActivity.this,PhoenixService.class));
            BaseActivity.this.finish();
        }
    }
}
