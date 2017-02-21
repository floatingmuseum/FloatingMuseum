package com.floatingmuseum.androidtest.views.tags;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.mockdata.Cheeses;
import com.floatingmuseum.androidtest.utils.ToastUtil;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/15.
 * 标签View
 */

public class TagsActivity extends AppCompatActivity {

//    @BindView(R.id.tags)
    TagsView tags;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    private int tags_text_radius;
    private int textPaddingTopAndBottom;
    private int textPaddingLeftAndRight;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        ButterKnife.bind(this);
        tags = new TagsView(this);
        // 设置tags內边距 从res文件中获取，可以自适应手机，不需写死padding值
        int padding = (int) getResources().getDimension(R.dimen.tags_inner_padding);
        tags.setPadding(padding, padding, padding, padding);
        // 行内view的水平间距
        int tags_lineh_inner_padding = (int) getResources().getDimension(R.dimen.tags_lineh_inner_padding);
        tags.setHorizontalSpecing(tags_lineh_inner_padding);
        // 行与行的垂直间距
        int tags_linev_inner_padding = (int) getResources().getDimension(R.dimen.tags_linev_inner_padding);
        tags.setVerticalSpecing(tags_linev_inner_padding);
        // 包裹tags，宽高充满父布局
        scrollView.addView(tags, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initViewShapeValues();
        initTags();
    }
    private void initViewShapeValues() {
        tags_text_radius = (int) getResources().getDimension(R.dimen.tags_text_radius);
        textPaddingTopAndBottom = (int) getResources().getDimension(R.dimen.tags_textv_inner_padding);
        textPaddingLeftAndRight = (int) getResources().getDimension(R.dimen.tags_texth_inner_padding);
    }
    private void initTags() {
        for (final String name : Cheeses.NAMES) {
            final TextView textView = new TextView(this);
            textView.setText(name);
            textView.setTextColor(Color.WHITE);// 字体颜色
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);// 字体大小
            textView.setGravity(Gravity.CENTER);// 居中
            textView.setPadding(textPaddingLeftAndRight,
                    textPaddingTopAndBottom,
                    textPaddingLeftAndRight,
                    textPaddingTopAndBottom);
            GradientDrawable normalState = generateDrawable(getColor(),
                    tags_text_radius);
            GradientDrawable pressState = generateDrawable(Color.GRAY,
                    tags_text_radius);
            StateListDrawable selector = generateSelector(normalState, pressState);
            textView.setBackgroundDrawable(selector);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.show(name);
                }
            });
            tags.addView(textView);
        }
    }
    private GradientDrawable generateDrawable(int color, int radius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        // 图片类型
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        // 颜色
        gradientDrawable.setColor(color);
        // 边角弧度
        // gradientDrawable.setCornerRadii(radii)//也是设置弧度，可以传一个数组进去，自定义四个角不同弧度
        gradientDrawable.setCornerRadius(radius);
        return gradientDrawable;
    }
    public StateListDrawable generateSelector(Drawable normal, Drawable pressed) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
                pressed);
        // 数组中不写值，即为默认
        stateListDrawable.addState(new int[]{}, normal);
        return stateListDrawable;
    }
    /**
     * // 随机一个颜色值  50到199之间，颜色值范围为0到255 太低偏黑， 太高偏白， 限定一个中间范围
     *
     * @return
     */
    public int getColor() {
        Random random = new Random();
        // 随机一个颜色值
        // 50到199之间， 字体颜色值范围为0到255 太低字体偏黑， 太高字体偏白， 限定一个中间范围
        int red = random.nextInt(150) + 50;
        int green = random.nextInt(150) + 50;
        int blue = random.nextInt(150) + 50;
        // 通过Color.rgb混合红绿蓝的值
        return Color.rgb(red, green, blue);
    }
}
