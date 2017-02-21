package com.floatingmuseum.androidtest.views.tags;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class TagsView extends ViewGroup {

    private float horizontalSpecing;
    private float verticalSpecing;
    private List<Line> lineList = new ArrayList<>();
    private int noPaddingWidth;

    public TagsView(Context context) {
        super(context);
    }

    public TagsView(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    /**
     * 设置单行中view的水平间距
     *
     * @param horizontalSpecing
     */
    public void setHorizontalSpecing(float horizontalSpecing) {
        this.horizontalSpecing = horizontalSpecing;
    }

    /**
     * 设置行与行的垂直间距
     *
     * @param verticalSpecing
     */
    public void setVerticalSpecing(float verticalSpecing) {
        this.verticalSpecing = verticalSpecing;
    }

    /**
     * 计算位置，分行操作
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取HotLayout宽度
        int hotLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        noPaddingWidth = hotLayoutWidth - getPaddingLeft() - getPaddingRight();
        Line line = null;
        // 遍历子view个数，
        for (int i = 0; i < getChildCount(); i++) {
            TextView childView = (TextView) getChildAt(i);
            // 通过父ViewGroup计算一下子view宽高
            childView.measure(0, 0);

            if (line == null) {
                line = new Line();
            }

            // 如果line中集合没有view，则一定可以添加view
            if (line.getViewList().size() == 0) {
                line.addView(childView);
            } else if (line.getLineWidth() + horizontalSpecing
                    + childView.getMeasuredWidth() > noPaddingWidth) {
                // 如果新添加view进去导致宽度超出，保存当前line对象，并创建新line对象，存入新line对象的集合中
                lineList.add(line);
                line = new Line();
                line.addView(childView);
            } else {
                line.addView(childView);
            }

            // 如果是最后一个view添加完毕后，需要保存line对象，不然会丢失
            if (i == (getChildCount() - 1)) {
                lineList.add(line);
            }
        }

        // 计算高度，先加上上下padding值
        int hotLayoutHeight = getPaddingBottom() + getPaddingTop();

        // 先计算所有行相加的高度
        for (int j = 0; j < lineList.size(); j++) {
            hotLayoutHeight += lineList.get(j).getLineHeight();
        }

        // 在加上所有行之间间距的高度
        hotLayoutHeight += ((lineList.size() - 1) * verticalSpecing);

        setMeasuredDimension(hotLayoutWidth, hotLayoutHeight);
    }

    /**
     * 将子view放到指定位置上
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 获取左边和高度的padding值，用来摆放view
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        for (int i = 0; i < lineList.size(); i++) {
            Line line = lineList.get(i);
            // 如果遍历到第二个行对象，开始叠加行高度
            if (i > 0) {
                // 当前行高度，等于前面的行高度与垂直间距相加
                paddingTop += (lineList.get(i - 1).getLineHeight() + verticalSpecing);
            }

            // 计算当前行的剩余行宽度，平均分配给每个view，使其充满行宽
            float lastLineWidth = getLastLineWidth(lineList.get(i)
                    .getLineWidth());

            List<View> viewList = line.getViewList();

            float perWidth = lastLineWidth / viewList.size();

            for (int j = 0; j < viewList.size(); j++) {
                View view = viewList.get(j);
                // 计算出每个view的精确宽度
                int realWidth = MeasureSpec.makeMeasureSpec(
                        (int) (view.getMeasuredWidth() + perWidth),
                        MeasureSpec.EXACTLY);
                view.measure(realWidth, 0);

                if (j == 0) {// 如果是第一个view
                    // 左为paddingLeft，上为paddingTop，右为paddingLeft值和view宽度和，下为paddingTop和控件高度和。
                    view.layout(paddingLeft, paddingTop,
                            paddingLeft + view.getMeasuredWidth(), paddingTop
                                    + view.getMeasuredHeight());
                } else {// 其他view根据前一个view的位置进行摆放
                    View preView = viewList.get(j - 1);
                    // 左等于之前view的右边+水平间距
                    int left = (int) (preView.getRight() + horizontalSpecing);
                    // 上下等于之前view的上下，右等左+自身view的宽度
                    view.layout(left, preView.getTop(),
                            left + view.getMeasuredWidth(), preView.getBottom());

                }
            }
        }
    }

    /**
     * 计算每行剩余宽度
     *
     * @param lineWidth
     * @return
     */
    private float getLastLineWidth(float lineWidth) {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
                - lineWidth;
    }

    /**
     * 行对象，封装每一行的数据
     *
     * @author Administrator
     *
     */
    class Line {
        private List<View> viewLsit = new ArrayList<View>();// 用来存放当前行内View的集合
        private float lineWidth;// 记录当前行的宽度，宽度等于 所有子view的宽度+子view之间的水平间距
        private float lineHeight;// 当前行的高度，之前行的总高度+之间的垂直间距

        /**
         * 获取当前行view集合
         *
         * @return
         */
        public List<View> getViewList() {
            return viewLsit;
        }

        /**
         * 获取当前行宽度
         *
         * @return
         */
        public float getLineWidth() {
            return lineWidth;
        }

        /**
         * 获取当前行高度
         *
         * @return
         */
        public float getLineHeight() {
            return lineHeight;
        }

        public void addView(View view) {
            // 安全判断，不添加重复view到集合中
            if (!viewLsit.contains(view)) {
                // 如果集合中只有一个view 行宽就是view的宽度
                if (viewLsit.size() == 0) {
                    lineWidth = view.getMeasuredWidth();
                } else {
                    // 已有行宽 加上新的子view和水平间距的宽
                    lineWidth += (view.getMeasuredWidth() + horizontalSpecing);
                }

                // 新view和已有view进行比较 行高以更高的view为准
                lineHeight = Math.max(lineHeight, view.getMeasuredHeight());

                viewLsit.add(view);
            }
        }
    }
}
