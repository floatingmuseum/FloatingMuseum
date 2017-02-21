package com.floatingmuseum.androidtest.base;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected void startActivity(Class targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }
}
