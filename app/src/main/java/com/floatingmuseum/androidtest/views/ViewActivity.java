package com.floatingmuseum.androidtest.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.views.tags.TagsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class ViewActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_tags)
    TextView tvTags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views);
        ButterKnife.bind(this);

        tvTags.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.tv_tags:
                startActivity(TagsActivity.class);
                break;
        }
    }
}
