package com.fly.slidedeletedemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by justtofly on 2019/4/14 10:25.
 * 作用：自定义view实现滑动删除的效果
 */
public class SweepView extends ViewGroup {
    private View mContentView;
    private View mDeleteView;

    public SweepView(Context context) {
        super(context);
    }

    public SweepView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //初始化View,找到孩子之后，两个方法都要实现，一个测量的方法，一个布局的方法
        mContentView = getChildAt(0);
        mDeleteView = getChildAt(1);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量孩子，contentView,使用它父亲的宽度和高度
        mContentView.measure(widthMeasureSpec,heightMeasureSpec);

        //deleteView
        LayoutParams layoutParams = mDeleteView.getLayoutParams();
        int deleteWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
        int deleteHeightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
        mDeleteView.measure(deleteWidthMeasureSpec,deleteHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //布局
        int contentWidth=mContentView.getMeasuredWidth();
        int deleteWidth=mDeleteView.getMeasuredWidth();

        //contentView的布局
        mContentView.layout(0,0,contentWidth,mContentView.getMeasuredHeight());

        //deleteView的布局
        mDeleteView.layout(contentWidth,0,contentWidth+deleteWidth,mDeleteView.getMeasuredHeight());
    }
}
