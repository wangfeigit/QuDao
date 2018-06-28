package com.kuanquan.qudao.widget.live.behavior;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.BuildConfig;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kuanquan.qudao.R;
import com.kuanquan.qudao.app.QuApplication;

import java.util.List;

/**
 * 可滚动的 Content Behavior
 * <p/>
 */
public class LiveContentBehavior extends HeaderScrollingViewBehavior {
    private static final String TAG = "LiveContentBehavior";

    public LiveContentBehavior() {
    }

    public LiveContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return isDependOn(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDependentViewChanged");
        }
        offsetChildAsNeeded(parent, child, dependency);
        return false;
    }

    private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        float dependencyTranslationY = dependency.getTranslationY();
        int translationY = (int) (-dependencyTranslationY / (getHeaderOffsetRange() * 1.0f) *
                getScrollRange(dependency));
        Log.i(TAG, "offsetChildAsNeeded: translationY=" + translationY);
        child.setTranslationY(translationY);

    }

    @Override
    protected View findFirstDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (isDependOn(view)) return view;
        }
        return null;
    }

    @Override
    protected int getScrollRange(View v) {
        if (isDependOn(v)) {
            return Math.max(0, v.getMeasuredHeight() - getFinalHeight());
        } else {
            return super.getScrollRange(v);
        }
    }

    private int getHeaderOffsetRange() {
        return QuApplication.getAppContext().getResources().getDimensionPixelOffset(R.dimen
                .weibo_header_offset);
    }

    private int getFinalHeight() {
        Resources resources = QuApplication.getAppContext().getResources();

        return 0;
    }

    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.id_hide_header;
    }
}