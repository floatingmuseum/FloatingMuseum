package floatingmuseum.userhunter;

import android.app.Application;
import android.content.Context;

/**
 * Created by Floatingmuseum on 2017/6/12.
 */

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
