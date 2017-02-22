package com.floatingmuseum.androidtest.functions.exception;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/22.
 */

public class ExceptionActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.null_pointer_exception)
    Button nullPointerException;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception);
        ButterKnife.bind(this);
        nullPointerException.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.null_pointer_exception:
                executeNullPointerException();
                break;
        }
    }

    private void executeNullPointerException() {
        String name = null;
        int length = name.length();
    }
}
