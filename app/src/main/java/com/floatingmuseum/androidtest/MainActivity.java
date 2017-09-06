package com.floatingmuseum.androidtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.BaseMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.StrikethroughSpan;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.functions.FunctionsActivity;
import com.floatingmuseum.androidtest.thirdpartys.ThirdPartiesActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.floatingmuseum.androidtest.views.ViewActivity;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_views)
    Button btViews;
    @BindView(R.id.bt_functions)
    Button btFunctions;
    @BindView(R.id.bt_third_parties)
    Button btThirdParties;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    @BindView(R.id.tv_test_blur_text)
    TextView tvTestBlurText;

    private String[] needPermissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        String pluginPath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Plugins/PluginDemo.apk");
//        String pluginPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().concat("/PluginDemo.apk");
        File pluginFile = new File(pluginPath);
        Logger.d("插件...apk是否存在:" + pluginFile.exists());
        btViews.setOnClickListener(this);
        btFunctions.setOnClickListener(this);
        btThirdParties.setOnClickListener(this);


        List<String> oldList = new ArrayList<>();
        oldList.add("1");
        oldList.add("2");
        oldList.add("3");
        oldList.add("4");
        oldList.add("5");
        oldList.add("6");
        oldList.add("7");
        oldList.add("8");
        oldList.add("9");
        Logger.d("差集测试...数据库数据:" + oldList);
        List<String> newList = new ArrayList<>();
        newList.add("2");
        newList.add("3");
        newList.add("4");
        newList.add("5");
        newList.add("6");
        newList.add("7");
        newList.add("8");
        newList.add("9");
        newList.add("10");
        Logger.d("差集测试...数据库数据:" + newList);
        long startTime = System.currentTimeMillis();
        List<String> tempList = new ArrayList<>();
        tempList.addAll(oldList);
        boolean isChanged1 = tempList.removeAll(newList);
        Logger.d("差集测试...需要从数据库删除:" + tempList + "...数据是否改变:" + isChanged1);
        tempList.clear();
        tempList.addAll(newList);
        boolean isChanged2 = tempList.removeAll(oldList);
        Logger.d("差集测试...需要添加到数据库:" + tempList + "...数据是否改变:" + isChanged2);
        Logger.d("差集测试...耗时:" + (System.currentTimeMillis() - startTime));

        initPermission(needPermissions);

        testBlurText();
    }

    private void testBlurText() {
        MaskFilterSpan maskFilterSpan1 = new MaskFilterSpan(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
        MaskFilterSpan maskFilterSpan2 = new MaskFilterSpan(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        final String text = "This is a fantastic movie -- as long as Batman vs. Superman or Suicide Squad are used as a reference bar.\n" +
                "I'm so disappointed in this movie it almost makes angry.\n" +
                "WW was supposed to be a \"strong independent woman\" and yet [spoiler]she falls in love with literally the first man she sees[/spoiler].\n" +
                "I just found her incredibly naive.\n" +
                "Which movie trope did the film not deliver? \n" +
                "We have the super-hot main character every man has to drool over in his first scene and the anglo-saxon [spoiler]love interest that honourably sacrifices himself for no reason (well, only so we don't have to deal with the question how growing old with a non-ageing goddess would be).[/spoiler]\n" +
                "We have the forgettable side-character #1 that is only here for comic relief, #2 that's only there to make the protagonist question her loyalty and #3 with a troubled past that the protagonist can help overcome. \n" +
                "And of course the german bad guys played by non-germans speaking in english with a ridiculous -- what they think ought to pass as a -- german accent. Here is the thing: germans don't speak with other germans in english with a distinct german fake accent. Let them speak german and add subtitles (ugh, reading...) or let them speak normal english.\n" +
                "But arriving in the war zone we can finally see her compassionately putting actions to her words:[spoiler] You've been in these trenches for a year without winning? Let me show you how an immortal demi-god handles this.[/spoiler]\n" +
                "\n" +
                "But, yes, the Captain Americaesque fight scenes against unnamed goons looked nice...";
//        String text = "abcdefg hijklmn. opq rst uvw xyz 1234567890 abcdefg hijklmn opq rst uvw xyz 1234567890";
        final SpannableString spanText = new SpannableString(text);
        List<Pair<Integer, Integer>> spoilersContainer = new ArrayList<>();
        getSpoilerIndex(text, 0, spoilersContainer);
        for (final Pair<Integer, Integer> pair : spoilersContainer) {
//            spanText.setSpan(new RemoveMaskSpan(spanText,pair.first,pair.second), pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spanText.setSpan(new BackgroundColorSpan(Color.DKGRAY), pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            Logger.d("评论内容...剧透位置:" + pair.toString());
        }

//        spanText.setSpan(maskFilterSpan1, 5, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        spanText.setSpan(maskFilterSpan2, 15, 20, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

//        spanText.setSpan(new ForegroundColorSpan(Color.RED),5,10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        spanText.setSpan(new ForegroundColorSpan(Color.RED),15,20, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

//        spanText.setSpan(new BackgroundColorSpan(Color.BLACK),5,10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        spanText.setSpan(new BackgroundColorSpan(Color.BLACK),15,20, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        spanText.setSpan(new StrikethroughSpan(),5,10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        spanText.setSpan(new StrikethroughSpan(),15,20, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        tvTestBlurText.setText(spanText);
//        movementmethod
//        tvTestBlurText.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvTestBlurText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvTestBlurText.setText(text);
            }
        });
    }

    private void getSpoilerIndex(String rawComment, int beginIndex, List<Pair<Integer, Integer>> spoilersContainer) {
        int startIndex = rawComment.indexOf("[spoiler]", beginIndex);
        int endIndex = rawComment.indexOf("[/spoiler]", beginIndex);
        if (startIndex != -1 && endIndex != -1) {
            spoilersContainer.add(new Pair<>(startIndex, endIndex + "[/spoiler]".length()));
            getSpoilerIndex(rawComment, endIndex + "[/spoiler]".length(), spoilersContainer);
        }
    }

    private void initPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions.length > 0) {
            List<String> needRequests = new ArrayList<>();
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    Logger.d("申请权限:" + permission);
                    needRequests.add(permission);
                } else {
                    Logger.d("拥有此权限:" + permission);
                }
            }
            if (needRequests.size() != 0) {
                requestPermissions(needRequests.toArray(new String[needRequests.size()]), 1024);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1024) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for (String permission : permissions) {
                    Logger.d("权限获取成功:" + permission);
                }
                ToastUtil.show("权限获取成功");
            } else {
                for (String permission : permissions) {
                    Logger.d("权限获取被拒绝:" + permission);
                }
                ToastUtil.show("权限获取被拒绝");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_views:
                startActivity(ViewActivity.class);
                break;
            case R.id.bt_functions:
                startActivity(FunctionsActivity.class);
                break;
            case R.id.bt_third_parties:
                startActivity(ThirdPartiesActivity.class);
//                int x = 1 / 0;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
