package com.floatingmuseum.androidtest.functions.hotspot;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

/**
 * Created by Floatingmuseum on 2017/3/20.
 * <p>
 * 创建热点，进行点对点传输
 */

public class HotSpotActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);
    }
}
