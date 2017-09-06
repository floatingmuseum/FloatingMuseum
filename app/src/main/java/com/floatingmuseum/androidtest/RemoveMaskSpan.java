package com.floatingmuseum.androidtest;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.View;

import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/9/6.
 */

public class RemoveMaskSpan extends ClickableSpan {
    private SpannableString spanText;
    private int startIndex;
    private int endIndex;

    public RemoveMaskSpan(SpannableString spanText, int startIndex, int endIndex) {
        this.spanText = spanText;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.BLUE);
    }

    @Override
    public void onClick(View view) {
        Logger.d("评论内容...显示剧透:" + startIndex + "..." + endIndex);
        spanText.setSpan(new BackgroundColorSpan(Color.RED), startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }
}
