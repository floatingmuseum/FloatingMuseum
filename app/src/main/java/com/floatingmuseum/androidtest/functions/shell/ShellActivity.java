package com.floatingmuseum.androidtest.functions.shell;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.jrummyapps.android.shell.CommandResult;
import com.jrummyapps.android.shell.Shell;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/16.
 * <p>
 * 执行Shell命令
 */

public class ShellActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_su_available)
    Button btSuAvailable;
    @BindView(R.id.bt_sh_command)
    Button btShCommand;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell);
        ButterKnife.bind(this);

        btSuAvailable.setOnClickListener(this);
        btShCommand.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_su_available:
                Logger.d("Is SU available: " + isSuAvailable());
                ToastUtil.show("Is SU available: " + isSuAvailable());
                break;
            case R.id.bt_sh_command:
                test();
                break;
        }
    }

    private boolean isSuAvailable() {
        return Shell.SU.available();
    }

    private void test() {
        CommandResult result = Shell.SH.run("ps");
        showResult(result);
    }

    private void showResult(CommandResult result) {
        boolean isSuccessful = result.isSuccessful();
        Logger.d("isSuccessful:" + isSuccessful + "...exitCode:" + result.exitCode);
        if (isSuccessful) {
            Logger.d(result.getStdout());
        } else {
            Logger.d(result.getStderr());
        }
    }
}
