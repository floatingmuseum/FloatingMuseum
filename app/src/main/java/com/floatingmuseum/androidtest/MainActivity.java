package com.floatingmuseum.androidtest;

import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.functions.FunctionsActivity;
import com.floatingmuseum.androidtest.views.ViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_views)
    TextView tvViews;
    @BindView(R.id.tv_functions)
    TextView tvFunctions;
    @BindView(R.id.tv_third_parties)
    TextView tvThirdParties;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        tvViews.setOnClickListener(this);
        tvFunctions.setOnClickListener(this);
        tvThirdParties.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.tv_views:
                startActivity(ViewActivity.class);
                break;
            case R.id.tv_functions:
                startActivity(FunctionsActivity.class);
                break;
            case R.id.tv_third_parties:
                break;
        }
    }
}
