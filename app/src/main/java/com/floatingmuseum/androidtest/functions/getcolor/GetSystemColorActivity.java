package com.floatingmuseum.androidtest.functions.getcolor;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/6/8.
 */

public class GetSystemColorActivity extends Activity {

    @BindView(R.id.switch_model)
    Switch switchModel;
    @BindView(R.id.et_model)
    EditText etModel;
    @BindView(R.id.ll_color_container)
    LinearLayout llColorContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_color);
        ButterKnife.bind(this);

        getColor();
    }

    private void getColor() {
        int[] obtainArray = new int[]{android.R.attr.color, android.R.attr.colorAccent, android.R.attr.colorControlNormal,
                android.R.attr.colorControlHighlight, android.R.attr.colorControlActivated, android.R.attr.colorActivatedHighlight,
                android.R.attr.colorBackground, android.R.attr.colorBackgroundCacheHint, android.R.attr.colorBackgroundFloating,
                android.R.attr.colorButtonNormal, android.R.attr.colorEdgeEffect, android.R.attr.colorFocusedHighlight,
                android.R.attr.colorForeground, android.R.attr.colorForegroundInverse, android.R.attr.colorLongPressedHighlight,
                android.R.attr.colorMultiSelectHighlight, android.R.attr.colorPressedHighlight, android.R.attr.colorPrimary,
                android.R.attr.colorPrimaryDark, android.R.attr.colorSecondary, android.R.attr.actionMenuTextColor,
                android.R.attr.calendarTextColor, android.R.attr.centerColor, android.R.attr.editTextColor,
                android.R.attr.endColor, android.R.attr.fastScrollTextColor, android.R.attr.fillColor,
                android.R.attr.gestureColor,android.R.attr.keyTextColor,android.R.attr.navigationBarColor,
                android.R.attr.numbersBackgroundColor,android.R.attr.numbersInnerTextColor,android.R.attr.numbersSelectorColor,
                android.R.attr.shadowColor,android.R.attr.numbersTextColor,android.R.attr.startColor,
                android.R.attr.statusBarColor,android.R.attr.strokeColor,android.R.attr.subtitleTextColor,
                android.R.attr.textColor,android.R.attr.titleTextColor,android.R.attr.uncertainGestureColor,
                android.R.attr.cacheColorHint,android.R.attr.textColorAlertDialogListItem,android.R.attr.textColorHighlight};
        TypedArray array = getTheme().obtainStyledAttributes(obtainArray);

        for (int i = 0; i < obtainArray.length; i++) {
            int color = array.getColor(i, 0);
            TextView colorView = new TextView(this);
            if (color == 0) {
                colorView.setText("Index:" + i + "...没有颜色");
            } else {
                colorView.setText("Index:" + i+"...值:"+color);
            }
            colorView.setTextSize(20);
            colorView.setBackgroundColor(color);
            llColorContainer.addView(colorView);
        }
        array.recycle();
    }
}
