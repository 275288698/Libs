package silicar.brady.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 垂直翻页
 * Created by Work on 2015/4/27.
 */
public class VerticalPager extends ViewGroup {

    /**
     * 控件高度
     */
    private int mHeight;

    /**
     * 布局总高度
     */
    private int mLayoutHeight;

    /**
     * 触摸按下的getScrollY
     */
    private int mScrollStartY;

    /**
     * 触摸松开的getScrollY
     */
    private int mScrollEndY;

    /**
     * 移动过程的Y
     */
    private int mLastY;

    /**
     * 滚动事件
     */
    private Scroller mScroller;

    /**
     * 滚动状态
     */
    private boolean isScrolling = false;

    /**
     * 加速度检测
     */
    private VelocityTracker mVelocityTracker;

    /**
     * 当前页
     */
    private int currentPage = 0;

    private OnVerticalPageChangeListener mOnVerticalPageChangeListener;

    public VerticalPager(Context context) {
        this(context, null);
    }

    public VerticalPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取屏幕高度
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(outMetrics);
//        mHeight = outMetrics.heightPixels;
        //初始化
        mScroller = new Scroller(context);
    }



    /**
     * 计算控件
     * 未计算的控件不能显示
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for(int i = 0 ;i < count ;i++)
        {
            View childView = getChildAt(i);
            //在父控件中的位置
            measureChild(childView,widthMeasureSpec,heightMeasureSpec);
        }
    }

    /**
     * 设置控件位置
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if(changed)
        {
            int count = getChildCount();
//            Log.e("l", "" + l);
//            Log.e("t",""+t);
//            Log.e("r",""+r);
//            Log.e("b",""+b);
//            Log.e("height",getHeight()+"");
            //mHeight = b - t;
            mHeight = getHeight();
            //MarginLayoutParams mLP = (MarginLayoutParams) getLayoutParams();
            //mLP.height = mHeight * count;
            mLayoutHeight = mHeight * count;
            for(int i = 0 ; i < count ;i++)
            {
                View childView = getChildAt(i);
                if(childView.getVisibility() != View.GONE)
                {
                    childView.layout(l,i*mHeight,r,(i+1)*mHeight);
                }
            }
        }
    }

    /**
     * 滚动计算
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        try {
            if(mScroller.computeScrollOffset())
            {
                //跳转到指定滚动位置
                scrollTo(0, mScroller.getCurrY());
                //刷新UI
                postInvalidate();
            }
            else
            {
                //设置当前选择页面
                int position = getScrollY() / mHeight;
                if(currentPage != position)
                {
                    if (mOnVerticalPageChangeListener != null)
                    {
                        currentPage = position;
                        //设置页面监听值
                        mOnVerticalPageChangeListener.onVerticalPageChange(currentPage);
                    }
                }
                isScrolling = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isScrolling)
            return super.onTouchEvent(event);
        int action = event.getAction();
        int motionY = (int) event.getY();
        obtainVelocity(event);
        if(action == MotionEvent.ACTION_DOWN)
        {
            mScrollStartY = getScrollY();
            mLastY = motionY;
        }
        int dy,scrollY;
        if(action == MotionEvent.ACTION_MOVE)
        {
            //如果动画未完成,
            if(!mScroller.isFinished())
                //完成后停止滚动
                mScroller.abortAnimation();
            dy = mLastY - motionY;
            scrollY = getScrollY();
            //达边界顶端,下拉无效
            if (dy < 0 && scrollY + dy < 0)
                dy = 0;
            //达边界底端,上拉无效
            if (dy > 0 && scrollY + dy > mLayoutHeight - mHeight)
                dy = 0;
            //调整当前滚动位置
            scrollBy(0, dy);
            mLastY = motionY;
        }
        if(action == MotionEvent.ACTION_UP)
        {
            mScrollEndY = getScrollY();
            dy = mScrollEndY - mScrollStartY;
            //向上滑,下一页
            if(wantScrollNext())
            {
                if(shouldScrollNext())
                    mScroller.startScroll(0, getScrollY(), 0, mHeight - dy);
                else
                    mScroller.startScroll(0, getScrollY(), 0, -dy);
            }
            //向下滑,上一页
            if(wanScrollPrevious())
            {
                if(shouldScrollPrevious())
                    mScroller.startScroll(0, getScrollY(), 0, -mHeight - dy);
                else
                    mScroller.startScroll(0, getScrollY(), 0, -dy);
            }
            isScrolling = true;
            postInvalidate();
            clearVelocity();
        }
        return true;
    }

    //滚动到下一页
    public void scrollNext()
    {
        //Log.e("scrollY",getScrollY()+"");
        //Log.e("mLayoutHeight",(mLayoutHeight-mHeight)+"");
        if (getScrollY() < mLayoutHeight-mHeight && !isScrolling) {
            mScroller.startScroll(0, getScrollY(), 0, mHeight);
            isScrolling = true;
            postInvalidate();
        }
    }

    //滚动到上一页
    public void scrollPrevious()
    {
        if (getScrollY() > 0 && !isScrolling) {
            mScroller.startScroll(0, getScrollY(), 0, -mHeight);
            isScrolling = true;
            postInvalidate();
        }
    }

    /**
     * 初始化加速度监听
     * @param event
     */
    private void obtainVelocity(MotionEvent event)
    {
        if (mVelocityTracker == null)
            //获得加速度监听
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
    }

    //重置加速度监听
    private void clearVelocity()
    {
        if(mVelocityTracker != null)
            mVelocityTracker.clear();
    }

    /**
     * 获取Y方向加速度
     * @return
     */
    private float getVelocityY()
    {
        mVelocityTracker.computeCurrentVelocity(1000);
        return mVelocityTracker.getYVelocity();
    }

    //判断滚动方向,下一页
    private boolean wantScrollNext()
    {
        return mScrollEndY > mScrollStartY;
    }
    //判断是否滚动到下一页
    private boolean shouldScrollNext()
    {
        return mScrollEndY - mScrollStartY > mHeight / 3 || getVelocityY() < -600;
    }

    //判断滚动方向,上一页
    private boolean wanScrollPrevious()
    {
        return mScrollEndY < mScrollStartY;
    }
    //判断是否滚动到上一页
    private boolean shouldScrollPrevious()
    {
        return mScrollStartY - mScrollEndY > mHeight / 3 || getVelocityY() > 600;
    }

    /**
     * 设置回调接口
     * @param onVerticalPageChangeListener
     */
    public void setOnVerticalPageChangeListener(OnVerticalPageChangeListener onVerticalPageChangeListener)
    {
        mOnVerticalPageChangeListener = onVerticalPageChangeListener;
    }

    /**
     * 页面改变监听回调接口
     */
    public interface OnVerticalPageChangeListener
    {
        void onVerticalPageChange(int currentPage);
    }
}
