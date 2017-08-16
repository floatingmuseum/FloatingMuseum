package com.floatingmuseum.androidtest.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.views.camera.CameraActivity;
import com.floatingmuseum.androidtest.views.simple.SimpleViewActivity;
import com.floatingmuseum.androidtest.views.tags.TagsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class ViewActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_tags)
    Button btTags;
    @BindView(R.id.bt_simple_view)
    Button btSimpleView;
    @BindView(R.id.bt_photo_video)
    Button btPhotoVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views);
        ButterKnife.bind(this);

        btSimpleView.setOnClickListener(this);
        btTags.setOnClickListener(this);
        btPhotoVideo.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_simple_view:
                startActivity(SimpleViewActivity.class);
                break;
            case R.id.bt_tags:
                startActivity(TagsActivity.class);
                break;
            case R.id.bt_photo_video:
                startActivity(CameraActivity.class);
                break;
        }
    }
}
