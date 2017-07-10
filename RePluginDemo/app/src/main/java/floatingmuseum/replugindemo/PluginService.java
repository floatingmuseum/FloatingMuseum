package floatingmuseum.replugindemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Floatingmuseum on 2017/7/10.
 */

public class PluginService extends Service {
    private String tag = PluginService.class.getSimpleName();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(tag,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.i(tag,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag,"onDestroy");
    }
}
