package floatingmuseum.replugindemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Floatingmuseum on 2017/7/10.
 */

public class PluginReceiver extends BroadcastReceiver {
    private String tag = PluginReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag,"Intent:"+intent.toString());
    }
}
