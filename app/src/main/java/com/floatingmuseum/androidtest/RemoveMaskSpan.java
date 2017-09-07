package com.floatingmuseum.androidtest;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.MaskFilterSpan;
import android.view.View;

import com.floatingmuseum.androidtest.views.camera.TexturePreview;
import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/9/6.
 */

public class RemoveMaskSpan extends ClickableSpan {
    private SpannableString spanText;
    private MaskFilterSpan blurSpan;
    private BackgroundColorSpan backgroundColorSpan;
    private int startIndex;
    private int endIndex;

    public RemoveMaskSpan(SpannableString spanText, MaskFilterSpan blurSpan, int startIndex, int endIndex) {
        this.spanText = spanText;
        this.blurSpan = blurSpan;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public RemoveMaskSpan(SpannableString spanText, BackgroundColorSpan backgroundColorSpan, Integer startIndex, Integer endIndex) {
        this.spanText = spanText;
        this.backgroundColorSpan = backgroundColorSpan;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View view) {
        Logger.d("评论内容...显示剧透:" + startIndex + "..." + endIndex);
//        MaskFilterSpan[] findBlurSpan = spanText.getSpans(startIndex, endIndex, MaskFilterSpan.class);
//        for (MaskFilterSpan maskFilterSpan : findBlurSpan) {
//            Logger.d("评论内容...显示剧透:findSpan:" + maskFilterSpan.toString() + "...span:" + blurSpan);
//        }
//        TextPaint tp = new TextPaint();
//        tp.setMaskFilter(new BlurMaskFilter(0, BlurMaskFilter.Blur.NORMAL));
//        blurSpan.updateDrawState(tp);
        spanText.removeSpan(backgroundColorSpan);
//        spanText.setSpan(new MaskFilterSpan(new BlurMaskFilter(0, BlurMaskFilter.Blur.NORMAL)), startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }
}
