package floatingmuseum.userhunter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Floatingmuseum on 2017/6/12.
 */

public class RemoteHunterService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return remoteHunter;
    }

    private RemoteHunter.Stub remoteHunter = new RemoteHunter.Stub() {

        @Override
        public int add(int x, int y) throws RemoteException {
            Log.i("RemoteHunter", "RemoteHunter服务端:加法计算:" + x + "+" + y + "...Pid:" + Process.myPid());
            return x + y;
        }

        @Override
        public int sub(int x, int y) throws RemoteException {
            Log.i("RemoteHunter", "RemoteHunter服务端:减法计算:" + x + "-" + y);
            return x - y;
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("RemoteHunter", "RemoteHunter服务端:onUnbind:" + intent.toString());
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("RemoteHunter", "RemoteHunter服务端:onDestroy:");
    }
}
