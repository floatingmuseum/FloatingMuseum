package com.floatingmuseum.androidtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Parcel;
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
        String text = "Alec Baldwin nailed his job; \n" +
                "The animators nailed their job (seriously, the art style and changes were amazing);\n" +
                "The ideas guy nailed his job ([spoiler]It's all the imagination of the kid[/spoiler]);\n" +
                "The writers just let the story down :( ([spoiler]Although at the beginning it's clear that the whole shebang is the older kid's imagination, the writers probably realised that the script was too short and so the end got changed to something that makes no sense. Why would Tim receive the kid for a second time? - unless it was all a dream...[/spoiler] ).\n" +
                "\n" +
                "All in all, a good movie, amazing acting and animating, partly let down by a badly written ending to the story. 8/10.";
        SpannableString spanText = new SpannableString(text);
        List<Pair<Integer, Integer>> spoilersContainer = new ArrayList<>();
        getSpoilerIndex(text, 0, spoilersContainer);
        for (Pair<Integer, Integer> pair : spoilersContainer) {
            MaskFilterSpan blurSpan = new MaskFilterSpan(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.DKGRAY);
            spanText.setSpan(backgroundColorSpan, pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            spanText.setSpan(blurSpan, pair.first, pair.second, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spanText.setSpan(new RemoveMaskSpan(spanText, backgroundColorSpan, pair.first, pair.second), pair.first, pair.second, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//            spanText.setSpan(new StrikethroughSpan(), pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            spanText.setSpan(new BackgroundColorSpan(Color.DKGRAY), pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            Logger.d("评论内容...剧透位置:" + pair.toString());
        }
        tvTestBlurText.setMovementMethod(LinkMovementMethod.getInstance());
        tvTestBlurText.setText(spanText);
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
