package com.gioppl.swipedeleteview;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by GIOPPL on 2017/8/4.
 */

public class SwipeDeleteLayout extends LinearLayout {
    private ViewDragHelper viewDragHelper;
    private View tv_swipe;
    private View tv_delete;
    private Point tv_swipePoint = new Point();
    private int sizeHeight;//控件的宽度
    private int sizeWidth;//控件的高度
    private int freeWidth;//空闲的区域
    private int MXA_OPEN_VELOCITY = 400;
    private boolean left_swipe = true;//判断左右滑动的

    public SwipeDeleteLayout(Context context) {
        super(context);
        init();
    }


    public SwipeDeleteLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeDeleteLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwipeDeleteLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override//返回true则表示可以捕获该view
            public boolean tryCaptureView(View child, int pointerId) {
                return child == tv_swipe;
            }

            @Override//水平的移动
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                log(left + "," + dx);
                if (child == tv_swipe) {
                    if (-left >= tv_delete.getMeasuredWidth()) {
                        return tv_delete.getMeasuredWidth() * -1;
                    } else if (left > 0)
                        return 0;
                }
                left_swipe = dx <= 0;
                return left;
            }


            @Override//竖直的移动
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (child == tv_swipe) top = tv_swipePoint.y;
                return top;
            }

            @Override//手指释放的时候回调
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
//                log(tv_swipe.getTop() + "," + tv_swipe.getLeft());
//                log(xvel+"#"+yvel);
                if (xvel < 0) {//左边
                    viewDragHelper.smoothSlideViewTo(tv_swipe, -240, 0);
                    ViewCompat.postInvalidateOnAnimation(SwipeDeleteLayout.this);
                } else if (xvel > 0) {//右边
                    viewDragHelper.smoothSlideViewTo(tv_swipe, 0, 0);
                    ViewCompat.postInvalidateOnAnimation(SwipeDeleteLayout.this);
                } else {//xvel==0的时候触发
                    if (left_swipe) {
                        viewDragHelper.smoothSlideViewTo(tv_swipe, -240, 0);
                        ViewCompat.postInvalidateOnAnimation(SwipeDeleteLayout.this);
                    } else {
                        viewDragHelper.smoothSlideViewTo(tv_swipe, 0, 0);
                        ViewCompat.postInvalidateOnAnimation(SwipeDeleteLayout.this);
                    }
                }
            }

            @Override//获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面; 最好不要返回0
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }


            @Override//获取view垂直方向的拖拽范围，最好不要返回0
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

            //当child的位置改变的时候执行,一般用来做其他子View的伴随移动 changedView：位置改变的child
            //left：child当前最新的left top: child当前最新的top dx: 本次水平移动的距离 dy: 本次垂直移动的距离
            @Override
            public void onViewPositionChanged(View changedView, int left, int top,
                                              int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                tv_delete.layout(tv_swipe.getMeasuredWidth() + freeWidth, 0, tv_swipe.getMeasuredWidth() + freeWidth + tv_delete.getMeasuredWidth(), tv_delete.getMeasuredHeight());//(int l, int t,int r,int b)


            }

            @Override//在边界拖动时回调
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {

            }

            @Override//当ViewDragHelper状态发生变化时回调（IDLE,DRAGGING,SETTING[自动滚动时]）
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            @Override//当view被捕获时回调
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
            }

            @Override//当触摸到边界时回调。
            public void onEdgeTouched(int edgeFlags, int pointerId) {
                super.onEdgeTouched(edgeFlags, pointerId);
            }

            @Override//true的时候会锁住当前的边界，false则unLock。
            public boolean onEdgeLock(int edgeFlags) {
                return super.onEdgeLock(edgeFlags);
            }

            @Override//改变同一个坐标（x,y）去寻找captureView位置的方法。（具体在：findTopChildUnder方法中）
            public int getOrderedChildIndex(int index) {
                return super.getOrderedChildIndex(index);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (viewDragHelper.shouldInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_swipe = getChildAt(0);//滑动的那个view
        tv_delete = getChildAt(1);//点击删除的那个view
        tv_delete.setTranslationZ(1);
        tv_swipe.setTranslationZ(2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        freeWidth = sizeWidth - tv_delete.getMeasuredWidth() - tv_swipe.getMeasuredWidth();
//        tv_delete.setVisibility(View.GONE);
//        log(freeWidth);
//        log(tv_delete.getMeasuredWidth() + "," + tv_swipe.getMeasuredWidth());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private void log(String text) {
        Log.i("###", text);
    }

    private void log(int text) {
        Log.i("###", "" + text);
    }
}
