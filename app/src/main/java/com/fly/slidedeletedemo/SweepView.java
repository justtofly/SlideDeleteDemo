package com.fly.slidedeletedemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by justtofly on 2019/4/14 10:25.
 * 作用：自定义view实现滑动删除的效果
 */
public class SweepView extends ViewGroup {
    private View mContentView;
    private View mDeleteView;
    private ViewDragHelper mViewDragHelper;

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

        //ViewDragHelper，用来处理滑动和拖拽的
        //创建实例
        mViewDragHelper = ViewDragHelper.create(this, new MyDragCallback());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量孩子，contentView,使用它父亲的宽度和高度
        mContentView.measure(widthMeasureSpec, heightMeasureSpec);

        //deleteView
        LayoutParams layoutParams = mDeleteView.getLayoutParams();
        int deleteWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
        int deleteHeightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
        mDeleteView.measure(deleteWidthMeasureSpec, deleteHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //布局
        int contentWidth = mContentView.getMeasuredWidth();
        int deleteWidth = mDeleteView.getMeasuredWidth();

        //contentView的布局
        mContentView.layout(0, 0, contentWidth, mContentView.getMeasuredHeight());

        //deleteView的布局
        mDeleteView.layout(contentWidth, 0, contentWidth + deleteWidth, mDeleteView.getMeasuredHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //核心逻辑方法
        mViewDragHelper.processTouchEvent(event);
        //消费touch
        return true;
    }

    class MyDragCallback extends ViewDragHelper.Callback {

        /**
         * @param view 是的是touch的view，不是自己，是自己的孩子
         * @param i    点的标记
         * @return viewdraghelper是否继续分析处理child的相关touch事件
         */
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            System.out.println("调用tryCaptureView");
            System.out.println("触摸的是否是内容控件，contentview:" + (mContentView == view));
            System.out.println("触摸的是否是删除控件，deleteview:" + (mDeleteView == view));
            return mContentView == view || mDeleteView == view;
        }

        /**
         * 捕获水平方向移动的位移数据，此方向调用后，view就会动了
         *
         * @param child 是哪个孩子移动了
         * @param left  父容器的左侧
         * @param dx    增量值
         * @return 你想我怎么动
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //处理拖动contentview和deleteview越界的问题
            if (child == mContentView) {
                //假如拖动的是contentview
                if (left > 0) {
                    //如果向右拖动话，就不让它拖动
                    return 0;
                } else {
                    if (-left > mDeleteView.getWidth()) {
                        return -mDeleteView.getWidth();
                    }
                }
            } else if (child == mDeleteView) {
                //假如拖动的是deleteview的话
                if (left < mContentView.getWidth() - mDeleteView.getWidth()) {
                    return mContentView.getWidth() - mDeleteView.getWidth();
                } else if (left > mContentView.getWidth()) {
                    return mContentView.getWidth();
                }
            }
            return left;
        }

        /**
         * 当view的位置改变的时候回调
         *
         * @param changedView 哪个view的位置改变了
         * @param left        changedview的left
         * @param top
         * @param dx          增量值
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //ui刷新，兼容2.3的系统和4.0的系统
            invalidate();

            //contentview的宽度
            int contentviewWidth = mContentView.getWidth();
            int deleteviewWidth = mDeleteView.getWidth();
            int deleteviewHeight = mDeleteView.getHeight();

            if (changedView == mContentView) {
                //如果contentview的位置改变了，相应地就需要改变deleteview的位置
                int devLeft = contentviewWidth + left;
                int devRight = contentviewWidth + left + deleteviewWidth;
                mDeleteView.layout(devLeft, 0, devRight, deleteviewHeight);
            } else {
                //如果deleteview位置改变了，就需要改变contentview的位置
                int conLeft = left - contentviewWidth;
                int conRight = left;
                mContentView.layout(conLeft, 0, left, deleteviewHeight);
            }
        }

        /**
         * 当触发touchup的时候回调
         * @param releasedChild 是哪个view的touchup触发了
         * @param xvel x方向的速率
         * @param yvel y方向的速率
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int deleteWidth = mDeleteView.getMeasuredWidth();
            int contentWidth = mContentView.getMeasuredWidth();
            int deleteHeight = mDeleteView.getMeasuredHeight();
            /*//如果contentview的touchup触发了
            if (releasedChild==mContentView){
                //向左滑动，滑动的距离大于deleteview的宽度一半
                if (-mContentView.getLeft()>deleteWidth/2){
                    //显示deleteview
                    mDeleteView.layout(contentWidth-deleteWidth,0,contentWidth,deleteHeight);
                    //显示contentview
                    mContentView.layout(-deleteWidth,0,contentWidth-deleteWidth,deleteHeight);
                }else{
                    //向左滑动，滑动的距离小于或者等于deleteview宽度的一半
                    //显示deleteview
                    mDeleteView.layout(contentWidth,0,contentWidth+deleteWidth,deleteHeight);
                    //显示contentview
                    mContentView.layout(0,0,contentWidth,deleteHeight);
                }
            }*/

            //不管是滑动contentview，还是滑动deleteview都是一样的
            //向左滑动，滑动的距离大于deleteview的宽度一半
            if (-mContentView.getLeft()>deleteWidth/2){
                //显示deleteview
                mDeleteView.layout(contentWidth-deleteWidth,0,contentWidth,deleteHeight);
                //显示contentview
                mContentView.layout(-deleteWidth,0,contentWidth-deleteWidth,deleteHeight);
            }else{
                //向左滑动，滑动的距离小于或者等于deleteview宽度的一半
                //显示deleteview
                mDeleteView.layout(contentWidth,0,contentWidth+deleteWidth,deleteHeight);
                //显示contentview
                mContentView.layout(0,0,contentWidth,deleteHeight);
            }
        }
    }
}
