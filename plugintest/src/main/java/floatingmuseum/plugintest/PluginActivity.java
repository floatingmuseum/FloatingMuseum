package floatingmuseum.plugintest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PluginActivity extends AppCompatActivity {

    private String tag = PluginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        Log.w(tag, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(tag, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(tag, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(tag, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(tag, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(tag, "onDestroy");
    }
}
