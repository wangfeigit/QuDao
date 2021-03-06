package com.example.fly.baselibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.example.fly.baselibrary.R;

import java.util.ArrayList;

/**
 * Item侧滑布局
 */
public class EasySwipeMenuLayout extends ViewGroup {


    private static final String TAG = "EasySwipeMenuLayout";
    private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);
    //布局资源
    private int mLeftViewResID;
    private int mRightViewResID;
    private int mContentViewResID;

    //布局对象
    private View mLeftView;
    private View mRightView;
    private View mContentView;

    //内容布局属性参数
    private MarginLayoutParams mContentViewLp;
    //是否滑动
    private boolean isSwipeing;
    //是否点击
    private boolean isTouched;
    //最新的坐标
    private PointF mLastP;
    //起始坐标
    private PointF mFirstP;
    //View显示百分比多少，才会显示全部
    private float mFraction = 0.5f;
    private boolean mCanLeftSwipe = true;
    private boolean mCanRightSwipe = true;
    private int mScaledTouchSlop;
    private Scroller mScroller;
    private static EasySwipeMenuLayout mViewCache;
    private static State mStateCache;
    private int mMaxHeight;

    public EasySwipeMenuLayout(Context context) {
        this(context, null);
    }

    public EasySwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasySwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);

    }

    /**
     * 初始化方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        //创建辅助对象
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mScaledTouchSlop = viewConfiguration.getScaledTouchSlop();
        mScroller = new Scroller(context);
        //1、获取配置的属性值
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasySwipeMenuLayout, defStyleAttr, 0);

        try {
            int indexCount = typedArray.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.EasySwipeMenuLayout_leftMenuView) {
                    mLeftViewResID = typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_leftMenuView, -1);
                } else if (attr == R.styleable.EasySwipeMenuLayout_rightMenuView) {
                    mRightViewResID = typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_rightMenuView, -1);
                } else if (attr == R.styleable.EasySwipeMenuLayout_contentView) {
                    mContentViewResID = typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_contentView, -1);
                } else if (attr == R.styleable.EasySwipeMenuLayout_canLeftSwipe) {
                    mCanLeftSwipe = typedArray.getBoolean(R.styleable.EasySwipeMenuLayout_canLeftSwipe, true);
                } else if (attr == R.styleable.EasySwipeMenuLayout_canRightSwipe) {
                    mCanRightSwipe = typedArray.getBoolean(R.styleable.EasySwipeMenuLayout_canRightSwipe, true);
                } else if (attr == R.styleable.EasySwipeMenuLayout_fraction) {
                    mFraction = typedArray.getFloat(R.styleable.EasySwipeMenuLayout_fraction, 0.5f);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取childView的个数

        int count = getChildCount();
        //参考frameLayout测量代码
        final boolean measureMatchParentChildren =
                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                        MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        mMatchParentChildren.clear();
        mMaxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        //遍历childViews
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                mMaxHeight = Math.max(mMaxHeight,
                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT ||
                            lp.height == LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child);
                    }
                }
            }
        }
        // Check against our minimum height and width
        mMaxHeight = Math.max(mMaxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(mMaxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

        count = mMatchParentChildren.size();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                final View child = mMatchParentChildren.get(i);
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                final int childWidthMeasureSpec;
                if (lp.width == LayoutParams.MATCH_PARENT) {
                    final int width = Math.max(0, getMeasuredWidth()
                            - lp.leftMargin - lp.rightMargin);
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            width, MeasureSpec.EXACTLY);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            lp.leftMargin + lp.rightMargin,
                            lp.width);
                }

                final int childHeightMeasureSpec;
                if (lp.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                    final int height = Math.max(0, getMeasuredHeight()
                            - lp.topMargin - lp.bottomMargin);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            height, MeasureSpec.EXACTLY);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            lp.topMargin + lp.bottomMargin,
                            lp.height);
                }

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int left = 0 + getPaddingLeft();
        int right = 0 + getPaddingLeft();
        int top = 0 + getPaddingTop();
        int bottom = 0 + getPaddingTop();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (mLeftView == null && child.getId() == mLeftViewResID) {
                // Log.i(TAG, "找到左边按钮view");
                mLeftView = child;
                mLeftView.setClickable(true);
            } else if (mRightView == null && child.getId() == mRightViewResID) {
                // Log.i(TAG, "找到右边按钮view");
                mRightView = child;
                mRightView.setClickable(true);
            } else if (mContentView == null && child.getId() == mContentViewResID) {
                // Log.i(TAG, "找到内容View");
                mContentView = child;
                mContentView.setClickable(true);
            }

        }
        //布局contentView
        int cRight = 0;
        if (mContentView != null) {
            mContentViewLp = (MarginLayoutParams) mContentView.getLayoutParams();
            int cTop = top + mContentViewLp.topMargin;
            int cLeft = left + mContentViewLp.leftMargin;
            cRight = left + mContentViewLp.leftMargin + mContentView.getMeasuredWidth();
            int cBottom = cTop + mContentView.getMeasuredHeight();
            mContentView.layout(cLeft, cTop, cRight, cBottom);
        }
        if (mLeftView != null) {
            MarginLayoutParams leftViewLp = (MarginLayoutParams) mLeftView.getLayoutParams();
            int lTop = top + mContentViewLp.topMargin;
            int lLeft = 0 - mLeftView.getMeasuredWidth() + leftViewLp.leftMargin + leftViewLp.rightMargin;
            int lRight = 0 - leftViewLp.rightMargin;
            int lBottom = lTop + mContentView.getMeasuredHeight();
            mLeftView.layout(lLeft, lTop, lRight, lBottom);
        }
        if (mRightView != null) {
            MarginLayoutParams rightViewLp = (MarginLayoutParams) mRightView.getLayoutParams();
            int lTop = top + mContentViewLp.topMargin;
            int lLeft = mContentView.getRight() + mContentViewLp.rightMargin + rightViewLp.leftMargin;
            int lRight = lLeft + mRightView.getMeasuredWidth();
            int lBottom = lTop + mContentView.getMeasuredHeight();
            mRightView.layout(lLeft, lTop, lRight, lBottom);
        }


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //   System.out.println(">>>>dispatchTouchEvent() ACTION_DOWN");

                isSwipeing = false;
                if (mLastP == null) {
                    mLastP = new PointF();
                }
                mLastP.set(ev.getRawX(), ev.getRawY());
                if (mFirstP == null) {
                    mFirstP = new PointF();
                }
                mFirstP.set(ev.getRawX(), ev.getRawY());
                if (mViewCache != null) {
                    if (mViewCache != this) {
                        mViewCache.handlerSwipeMenu(State.CLOSE);

                    }
//                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // System.out.println(">>>>dispatchTouchEvent() ACTION_MOVE getScrollX:" + getScrollX());
                isSwipeing = true;
                float distanceX = mLastP.x - ev.getRawX();
                float distanceY = mLastP.y - ev.getRawY();
                if (Math.abs(distanceY) > mScaledTouchSlop * 2) {
                    break;
                }
                //当处于水平滑动时，禁止父类拦截
                if (Math.abs(distanceX) > mScaledTouchSlop * 2 || Math.abs(getScrollX()) > mScaledTouchSlop * 2) {
                    requestDisallowInterceptTouchEvent(true);
                }
                scrollBy((int) (distanceX), 0);//滑动使用scrollBy

                //越界修正
                if (getScrollX() < 0) {
                    if (!mCanRightSwipe || mLeftView == null) {
                        scrollTo(0, 0);
                    } else {//左滑
                        if (getScrollX() < mLeftView.getLeft()) {
                            scrollTo(mLeftView.getLeft(), 0);
                        }

                    }
                } else if (getScrollX() > 0) {
                    if (!mCanLeftSwipe || mRightView == null) {
                        scrollTo(0, 0);
                    } else {
                        if (getScrollX() > mRightView.getRight() - mContentView.getRight() - mContentViewLp.rightMargin) {
                            scrollTo(mRightView.getRight() - mContentView.getRight() - mContentViewLp.rightMargin, 0);
                        }
                    }
                }

                mLastP.set(ev.getRawX(), ev.getRawY());

                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                //    System.out.println(">>>>dispatchTouchEvent() ACTION_CANCEL OR ACTION_UP");
                State result = isShouldOpen(getScrollX());
                handlerSwipeMenu(result);
                break;
            }
            default: {
                break;
            }
        }

        return super.dispatchTouchEvent(ev);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Log.d(TAG, "dispatchTouchEvent() called with: " + "ev = [" + event + "]");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //对左边界进行处理
                float distance = mLastP.x - event.getRawX();
                if (Math.abs(distance) > mScaledTouchSlop) {
                    // 当手指拖动值大于mScaledTouchSlop值时，认为应该进行滚动，拦截子控件的事件
                    return true;
                }
                break;

            }
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    /**
     * 自动设置状态
     *
     * @param result
     */
    private void handlerSwipeMenu(State result) {
        if (result == State.LEFTOPEN) {
            mScroller.startScroll(getScrollX(), 0, mLeftView.getLeft() - getScrollX(), 0);
            mViewCache = this;
            mStateCache = result;
        } else if (result == State.RIGHTOPEN) {
            mViewCache = this;
            mScroller.startScroll(getScrollX(), 0, mRightView.getRight() - mContentView.getRight() - mContentViewLp.rightMargin - getScrollX(), 0);
            mStateCache = result;
        } else {
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
            mViewCache = null;
            mStateCache = null;

        }
        invalidate();
    }


    @Override
    public void computeScroll() {
        //判断Scroller是否执行完毕：
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //通知View重绘-invalidate()->onDraw()->computeScroll()
            invalidate();
        }
    }


    /**
     * 根据当前的scrollX的值判断松开手后应处于何种状态
     *
     * @param scrollX
     * @return
     */
    private State isShouldOpen(int scrollX) {
        if (getScrollX() < 0 && mLeftView != null) {
            //➡滑动
            //获得leftView的测量长度
            //滑动超过View的一半就自动显示全部
            if (Math.abs(mLeftView.getWidth() * mFraction) < Math.abs(getScrollX())) {
                return State.LEFTOPEN;
            }

        } else if (getScrollX() > 0 && mRightView != null) {
            //⬅️滑动
            //滑动超过View的一半就自动显示全部
            if (Math.abs(mRightView.getWidth() * mFraction) < Math.abs(getScrollX())) {
                return State.RIGHTOPEN;
            }

        }
        return State.CLOSE;
    }


    @Override
    protected void onDetachedFromWindow() {
        if (this == mViewCache) {
            mViewCache.handlerSwipeMenu(State.CLOSE);
        }
        super.onDetachedFromWindow();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this == mViewCache) {
            mViewCache.handlerSwipeMenu(mStateCache);
        }
    }

    public void resetStatus() {
        if (mViewCache != null) {
            if (mStateCache != null && mStateCache != State.CLOSE && mScroller != null) {
                mScroller.startScroll(mViewCache.getScrollX(), 0, -mViewCache.getScrollX(), 0);
                mViewCache.invalidate();
                mViewCache = null;
                mStateCache = null;
            }
        }
    }


    public float getFraction() {
        return mFraction;
    }

    public void setFraction(float mFraction) {
        this.mFraction = mFraction;
    }

    public boolean isCanLeftSwipe() {
        return mCanLeftSwipe;
    }

    public void setCanLeftSwipe(boolean mCanLeftSwipe) {
        this.mCanLeftSwipe = mCanLeftSwipe;
    }

    public boolean isCanRightSwipe() {
        return mCanRightSwipe;
    }

    public void setCanRightSwipe(boolean mCanRightSwipe) {
        this.mCanRightSwipe = mCanRightSwipe;
    }

    public static EasySwipeMenuLayout getViewCache() {
        return mViewCache;
    }


    public static State getStateCache() {
        return mStateCache;
    }

    public enum State {
        LEFTOPEN,
        RIGHTOPEN,
        CLOSE,
    }
}
