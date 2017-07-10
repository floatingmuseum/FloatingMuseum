package floatingmuseum.replugindemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class PluginActivity extends AppCompatActivity implements View.OnClickListener {

    private String tag = PluginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        Log.d(tag, "onCreate");

        initView();
    }

    private void initView() {
        Button btStartOtherActivity = (Button) findViewById(R.id.bt_start_other_activity);
        Button btStartService = (Button) findViewById(R.id.bt_start_service);
        Button btStopService = (Button) findViewById(R.id.bt_stop_service);
        Button btRegisterBroadcast = (Button) findViewById(R.id.bt_register_broadcast);
        Button btUnregisterBroadcast = (Button) findViewById(R.id.bt_unregister_broadcast);
        Button btSendBroadcast = (Button) findViewById(R.id.bt_send_broadcast);


        btStartOtherActivity.setOnClickListener(this);
        btStartService.setOnClickListener(this);
        btStopService.setOnClickListener(this);
        btRegisterBroadcast.setOnClickListener(this);
        btUnregisterBroadcast.setOnClickListener(this);
        btSendBroadcast.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(tag, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag, "onResume");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start_other_activity:
                startActivity(new Intent(this, OtherActivity.class));
                break;
            case R.id.bt_start_service:
                startService(new Intent(this, PluginService.class));
                break;
            case R.id.bt_stop_service:
                stopService(new Intent(this, PluginService.class));
                break;
            case R.id.bt_register_broadcast:
                break;
            case R.id.bt_unregister_broadcast:
                break;
            case R.id.bt_send_broadcast:
                Intent intent = new Intent();
                intent.setAction("floatingmuserm.plugin.receiver.action");
                intent.putExtra("extra_name", "floating");
                sendBroadcast(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(tag, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(tag, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(tag, "onDestroy");
    }
}
