package com.kuanquan.qudao.ui.fragment;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.fly.baselibrary.utils.base.ToastUtils;
import com.example.fly.baselibrary.utils.useful.LogUtil;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.widget.MsgView;
import com.kuanquan.qudao.R;
import com.kuanquan.qudao.bean.HomeBeanChild;
import com.kuanquan.qudao.statusbar.StatuBarActivity;
import com.kuanquan.qudao.ui.activity.HideStatuBarActivity;
import com.kuanquan.qudao.ui.activity.MainActivity;
import com.kuanquan.qudao.ui.activity.NotifyActivity;
import com.kuanquan.qudao.ui.webview.ProgressWebViewActivity;
import com.kuanquan.qudao.utils.DataUtils;
import com.kuanquan.qudao.widget.HomeBanner;
import com.kuanquan.qudao.widget.HomeTitleView;
import com.kuanquan.qudao.widget.ProjectViewpager;
import java.util.ArrayList;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 主页的消息页面
 */
public class NotiFragment extends CommonFragment implements HomeBanner.OnPageClickListener,ProjectViewpager.OnPageItemClickListener,View.OnClickListener {
    //AppBarLayout
    private AppBarLayout mAppBarLayout;
    //顶部HeaderLayout
    private LinearLayout headerLayout;
    //是否隐藏了头部
    private boolean isHideHeaderLayout = false;
    private HomeTitleView mHomeTitleView;
    private HomeBanner mHomeBanner;

    private ProjectViewpager project_view_tab;

    private MyPagerAdapter mAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private AFragment mAFragment; // 课程
    private BFragment mBFragment; // 文章
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {"课程\n大众化", "文章\n 大众化","直播\n 大众化","咨询\n 大众化","谷歌\n 大众化"};
    private LinearLayoutManager manager;
    private LinearLayout mLinearLayout; // 小圆点
    private ArrayList<ImageView> dotsList = new ArrayList<ImageView>();

    private LinearLayout ll_view_pager; // 小圆点
    private ArrayList<ImageView> dotsListF = new ArrayList<ImageView>();
    private View project_view_show;
    private ImageButton image_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_noti, container, false);
    }

    @Override
    protected void initView() {
        image_button = view.findViewById(R.id.activity_collection_tab_layout_image_button);
        image_button.setOnClickListener(this);
        project_view_show = view.findViewById(R.id.project_view_show);
        headerLayout = (LinearLayout) view.findViewById(R.id.home_header_layout);  // 隐藏的头部布局
        mViewPager = (ViewPager) view.findViewById(R.id.activity_collection_vp);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.activity_collection_tab_layout);

        project_view_tab = (ProjectViewpager) view.findViewById(R.id.project_view_tab);
        mHomeTitleView = view.findViewById(R.id.home_title_view);
        mHomeTitleView.setOnClick(this);


        project_view_tab.setData(DataUtils.getTabData(DataUtils.getTabItemData()),this);
        ll_view_pager = (LinearLayout) view.findViewById(R.id.ll_view_pager);
        if (DataUtils.getTabData(DataUtils.getTabItemData()) != null && DataUtils.getTabData(DataUtils.getTabItemData()).size() > 0) {
            try {
                dotsListF.clear();
                ll_view_pager.removeAllViews();
                for (int i = 0; i < DataUtils.getTabData(DataUtils.getTabItemData()).size(); i++) {
                    ImageView view = new ImageView(getContext());
                    if (i == 0) {
                        view.setImageResource(R.drawable.dots_focus_tab);
                    } else {
                        view.setImageResource(R.drawable.dots_normal_tab);
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(26, 18);
                    params.setMargins(5, 0, 5, 0);
                    ll_view_pager.addView(view, params);
                    dotsListF.add(view);
                }
            }catch (Exception e){
                LogUtil.e("小圆点异常 = " + e);
            }
        }

        onTabPageSelected(0);

        mHomeBanner = view.findViewById(R.id.home_banner_layout);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.ll_view);
        if (DataUtils.getBannerData() != null && DataUtils.getBannerData().size() > 0) {
            try {
                dotsList.clear();
                mLinearLayout.removeAllViews();
                for (int i = 0; i < DataUtils.getBannerData().size(); i++) {
                    ImageView view = new ImageView(getContext());
                    if (i == 0) {
                        view.setImageResource(R.drawable.dots_focus);
                    } else {
                        view.setImageResource(R.drawable.dots_normal);
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(18, 18);
                    params.setMargins(5, 0, 5, 0);
                    mLinearLayout.addView(view, params);
                    dotsList.add(view);
                }
            }catch (Exception e){
                LogUtil.e("小圆点异常 = " + e);
            }
        }

        mHomeBanner.setData(DataUtils.getBannerData(),this);
        mHomeBanner.setScrollSpeed(mHomeBanner);

        initAppBarLayout();
    }

    // 初始化AppBarLayout
    private void initAppBarLayout(){
        LayoutTransition mTransition = new LayoutTransition();
        ObjectAnimator addAnimator = ObjectAnimator.ofFloat(null, "translationY", 0, 1.f).
                setDuration(mTransition.getDuration(LayoutTransition.APPEARING));
        mTransition.setAnimator(LayoutTransition.APPEARING, addAnimator);

        //header view_tsnackbar_layout height
        final int headerHeight = getResources().getDimensionPixelOffset(R.dimen.header_home_height_root);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.home_appbar);
        mAppBarLayout.setLayoutTransition(mTransition);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                verticalOffset = Math.abs(verticalOffset);
                if ( verticalOffset >= headerHeight ){
                    isHideHeaderLayout = true;
                    LogUtil.e("移出屏幕了");
                    //当偏移量超过顶部layout的高度时，我们认为他已经完全移动出屏幕了
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                            mParams.setScrollFlags(0);
                            headerLayout.setLayoutParams(mParams);
                            headerLayout.setVisibility(View.GONE);
                            mHomeTitleView.changeState(true);
                            mHomeBanner.pauseBanner();
                        }
                    },100);
                }
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mAFragment = new AFragment();
        mFragments.clear();
        mFragments.add(mAFragment);
        for (int i = 0; i < 4; i++) {
            mBFragment = new BFragment();
            mFragments.add(mBFragment);
        }

        mAdapter = new MyPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
        setSelected(0);
        // tab点击事件
        mSlidingTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                setSelected(position);
            }

            @Override
            public void onTabReselect(int position) {
//                LogUtil.e("NotiFragment","未选中");
            }
        });

        // viewpager滑动事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TextView titleViewF = mSlidingTabLayout.getTitleView(0);
        titleViewF.setSingleLine(false);
        titleViewF.setText("10\n中国");
//        RelativeLayout.LayoutParams paramsf = (RelativeLayout.LayoutParams) titleViewF.getLayoutParams();
//        paramsf.height = 100;
//        paramsf.width = 100;
//        titleViewF.setLayoutParams(paramsf);
//        Badge badge = new QBadgeView(getActivity());
//        badge.bindTarget(titleViewF);
//        badge.setGravityOffset(15,0,true);  // 设置外边距

        mSlidingTabLayout.showMsg(0,0);
        mSlidingTabLayout.setMsgMargin(0,20,5);
        MsgView msgView = mSlidingTabLayout.getMsgView(0);
//        msgView.setIsRadiusHalfHeight(false);
//        msgView.setIsWidthHeightEqual(false);
//        msgView.setCornerRadius(600);
//        msgView.setStrokeWidth(10);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) msgView.getLayoutParams();
        params.height = 30;
        params.width = 30;
        msgView.setLayoutParams(params);
//        mSlidingTabLayout.showMsg(0,0);
//        mSlidingTabLayout.setMsgMargin(0,20,0);
//        mSlidingTabLayout.showDot(0);
    }

    private void setSelected(int position){
        if (mTitles != null) {
            for (int i = 0; i < mTitles.length; i++) {
                if (i == position) {
                    TextView titleView = mSlidingTabLayout.getTitleView(i);
                    titleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    LogUtil.e("NotiFragment","选中");
                }else{
                    TextView titleView = mSlidingTabLayout.getTitleView(i);
                    titleView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    LogUtil.e("NotiFragment","未选中");
                }
            }
        }
    }

    @Override
    protected boolean isBindEventBusHere() {
        return false;
    }

    @Override
    public void onPageClick(HomeBeanChild info) {
//        startActivity(new Intent(getContext(), StatuBarActivity.class));
        startActivity(new Intent(getContext(), ProgressWebViewActivity.class));
    }

    @Override
    public void onPageItemClick(HomeBeanChild info) {
//        ToastUtils.showShort(info.id);
        startActivity(new Intent(getContext(), HideStatuBarActivity.class));
    }

    @Override
    public void onTabPageSelected(int position) {
        for(int i=0;i<dotsListF.size();i++){
            if(position%dotsListF.size() == i){
                ViewGroup.LayoutParams layoutParams = dotsListF.get(i).getLayoutParams();
                layoutParams.height = 15;
                layoutParams.width = 25;
                dotsListF.get(i).setLayoutParams(layoutParams);
                dotsListF.get(i).setImageResource(R.drawable.dots_focus_tab);
            }else{
                ViewGroup.LayoutParams layoutParams = dotsListF.get(i).getLayoutParams();
                layoutParams.height = 15;
                layoutParams.width = 15;
                dotsListF.get(i).setLayoutParams(layoutParams);
                dotsListF.get(i).setImageResource(R.drawable.dots_normal_tab);
            }
        }
    }

    @Override
    public void onTabOther(int type) {
        if (1 == type) {
            project_view_show.setVisibility(View.VISIBLE);
            ll_view_pager.setVisibility(View.GONE);
        }else{
            ll_view_pager.setVisibility(View.VISIBLE);
            project_view_show.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {
        for(int i=0;i<dotsList.size();i++){
            if(position%dotsList.size() == i){
                dotsList.get(i).setImageResource(R.drawable.dots_focus);
            }else{
                dotsList.get(i).setImageResource(R.drawable.dots_normal);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_title_rl_left_image:
                mHomeTitleView.changeState(false);
                if (isHideHeaderLayout){
                    isHideHeaderLayout = false;
                    headerLayout.setVisibility(View.VISIBLE);
                    mHomeBanner.resumeBanner();
                    postEventBus("recycler_unFocus");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                            mParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|
                                    AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                            headerLayout.setLayoutParams(mParams);
                        }
                    },300);
                }
                break;
            case R.id.home_title_rl_right_image: // 通知
                startActivity(new Intent(context, NotifyActivity.class));
                break;
            case R.id.activity_collection_tab_layout_image_button:
                isHideHeaderLayout = true;
                LogUtil.e("移出屏幕了");
                //当偏移量超过顶部layout的高度时，我们认为他已经完全移动出屏幕了
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                        mParams.setScrollFlags(0);
                        headerLayout.setLayoutParams(mParams);
                        headerLayout.setVisibility(View.GONE);
                        mHomeTitleView.changeState(true);
                        mHomeBanner.pauseBanner();
                    }
                },100);
                break;
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
