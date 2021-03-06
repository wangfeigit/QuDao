package com.example.fly.baselibrary.mvpEeventBus;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fly.baselibrary.BaseApplication;
import com.example.fly.baselibrary.utils.base.ACache;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author: fei.wang
 * @date: 2017.4.23
 * @des:
 */
public abstract  class CommonActivity<T extends BasePresenter> extends AppCompatActivity {

    protected  T mPresent;
    protected ACache mACache;
//    protected MyProgressDialog mDialog;
    protected Activity mContext;
//    protected CompositeDisposable compositeDisposable; // 管理事件订阅
//    protected ArrayMap<String, Disposable> disposableMap;
    protected static ConcurrentHashMap<String, String> paramMap = new ConcurrentHashMap<String, String>();
    protected HashMap<String, String> valueMap = new HashMap<String, String>();

    protected FragmentManager fm;
//    protected BaseFragment currentFragment;
    protected ArrayMap<String, Integer> mThemeColorMap;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        ActivityCollector.addActivity(this);
        setRequestedOrientation(getOrientation());
        super.onCreate(savedInstanceState);
        mContext = this;
        mPresent = createPresent();
        if (mPresent != null) {
            mPresent.initModel();
            mPresent.initialize();
        }

        mACache = BaseApplication.getAppContext().mACache;
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }

//        if (mDialog == null) {
//            mDialog = new MyProgressDialog(this, R.style.MyProgressDialog);
//        }
//        mDialog.setMessage("正在加载！");

        initTheme();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initWindowTransition(getWindowTransition());
        }
//        int layoutID = getLayoutID();
//        if (layoutID != 0) {
//            setContentView(layoutID);
//        }
        initThemeAttrs();
        setStatusColor();
//        mPresenter = getPresenter();
        initValueFromPrePage();
//        initViewAndEvent(savedInstanceState);
//        initData(savedInstanceState);
    }

    protected abstract boolean isBindEventBusHere();

    /**
     * 子类实现具体的构建过程
     * @return
     */
    protected abstract T createPresent() ;

//    @Subscribe
    protected void onEventMainThread(EventCenter eventCenter) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
//        if (mPresenter != null) {
//            mPresenter.onDestory();
//        }
//            onUnsubscribe();
//        onDestroyChild();

    }

//    protected abstract void onDestroyChild();

    /**网络异常的显示和隐藏 true 显示  false 隐藏*/
    protected void showHide(boolean b,TextView noData_one,TextView noData){
        if (b) {
            RelativeLayout.LayoutParams subtitleParams = (RelativeLayout.LayoutParams) noData_one.getLayoutParams();
//            subtitleParams.topMargin = MyApp.height / 3;
            noData_one.setLayoutParams(subtitleParams);
            noData.setVisibility(View.VISIBLE);
            noData_one.setVisibility(View.VISIBLE);
        }else{
            noData.setVisibility(View.GONE);
            noData_one.setVisibility(View.GONE);
        }
    }

    public void postEventBus(String code){
        EventBus.getDefault().post(new EventCenter<Object>(code));
    }

    public void postEventBusSticky(String code){
        EventBus.getDefault().postSticky(new EventCenter<Object>(code));
    }
    public void postEventBusSticky(String code,Object obj){
        EventBus.getDefault().postSticky(new EventCenter<Object>(code,obj));
    }

    public void postEventBus(String code,Object obj){
        EventBus.getDefault().post(new EventCenter<Object>(code,obj));
    }

    @SuppressWarnings("unchecked")
    protected  <T extends View> T genericFindViewById(int id) {
        return (T) findViewById(id);
    }

    /**
     * 横竖屏，默认是竖屏
     * 1.landscape：横屏(风景照) ，显示时宽度大于高度；
     * 2.portrait：竖屏 (肖像照) ， 显示时 高 度大于 宽 度 ；
     * 3.user：用户当前的首选方向；
     * 4.behind：继承Activity堆栈中当前Activity下面的那个Activity的方向；
     * 5.sensor：由物理感应器决定显示方向，它取决于用户如何持有设备，当 设备 被旋转时方向会随之变化——在横屏与竖屏之间；
     * 6.nosensor：忽略物理感应器——即显示方向与物理感应器无关，不管用户如何旋转设备显示方向都不会随着改变("unspecified"设置除外)；
     * 7.unspecified ：未指定，此为默认值，由Android系统自己选择适当的方向，选择策略视具体设备的配置情况而定，因此不同的设备会有不同的方向选择；
     *
     * @return
     */
    protected int getOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    /**
     * 初始化视图
     *
     * @param savedInstanceState
     */
//    protected abstract void initViewAndEvent(Bundle savedInstanceState);

    /**
     * 初始化数据操作
     *
     * @param savedInstanceState
     */
//    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 设置 Presenter
     *
     * @return
     */
//    protected abstract T getPresenter();

    /**
     * 设置布局界面id
     *
     * @return
     */
//    protected abstract int getLayoutID();

    /**
     * 设置Fragment容器id
     *
     * @return
     */
//    protected abstract int setFragmentContainerResId();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mPresenter != null) {
//            mPresenter.onStop();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        PermissionManager.onRequestResult(requestCode, permissions, grantResults);
    }

    /**
     * 初始化主题颜色
     */
    protected void initTheme() {
//
    }

    /**
     * 初始化获取当前主题中的颜色
     *
     * @return
     */
    protected void initThemeAttrs() {
    }


    /**
     * 设置过渡动画
     * 默认是淡入淡出，可重写
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition getWindowTransition() {
        return new Fade();
    }

    /**
     * 初始化过渡动画
     *
     * @param transition
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initWindowTransition(Transition transition) {
        getWindow().setReturnTransition(transition);
        getWindow().setExitTransition(transition);
        getWindow().setEnterTransition(transition);
        getWindow().setReenterTransition(transition);
    }

    /**
     * 初始化沉浸式状态栏
     */
    protected void setStatusColor() {
        //获取主题中的颜色
//        TypedValue typedValue = new TypedValue();
//        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//        int color = typedValue.data;
//        StatusBarUtils.setColor(mContext, mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY), 0);
    }

//    /**
//     * 显示Fragment
//     *
//     * @param fragment
//     */
//    protected void showFragment(BaseFragment fragment, int position) {
//        if (fm == null) {
//            fm = getSupportFragmentManager();
//        }
//        FragmentTransaction transaction = fm.beginTransaction();
//        //Fragment添加
//        if (!fragment.isAdded()) {
////            fragment.setArguments(bundle);
//            transaction.add(setFragmentContainerResId(), fragment, position + "");
//        }
//        if (currentFragment == null) {
//            currentFragment = fragment;
//        }
//        //通过tag进行过渡动画滑动判断
//        if (Integer.parseInt(currentFragment.getTag()) >= Integer.parseInt(fragment.getTag())) {
//            transaction.setCustomAnimations(com.rayhahah.rbase.R.anim.fragment_push_left_in, com.rayhahah.rbase.R.anim.fragment_push_right_out);
//        } else {
//            transaction.setCustomAnimations(com.rayhahah.rbase.R.anim.fragment_push_right_in, com.rayhahah.rbase.R.anim.fragment_push_left_out);
//        }
//
//        transaction.hide(currentFragment).show(fragment);
//        transaction.commit();
//        currentFragment = fragment;
//    }

//    /**
//     * 添加事件监听处理到 事件管理类
//     *
//     * @param disposable 上流事件
//     */
//    protected void addSubscription(Disposable disposable) {
//        if (compositeDisposable == null) {
//            compositeDisposable = new CompositeDisposable();
//        }
//        compositeDisposable.add(disposable);
//    }

    /**
     * 添加事件监听处理到 事件管理类
     *
     * @param tag        标识符
     * @param disposable 上流事件
     */
//    protected void addSubscription(String tag, Disposable disposable) {
//        if (compositeDisposable == null) {
//            compositeDisposable = new CompositeDisposable();
//        }
//        if (disposableMap == null) {
//            disposableMap = new ArrayMap<>();
//        }
//        disposableMap.put(tag, disposable);
//        compositeDisposable.add(disposable);
//    }


    /**
     * RxJava取消注册，避免内存泄露
     * 取消以后就只能重新新建一个了
     */
//    protected void onUnsubscribe() {
//        if (compositeDisposable != null) {
//            // Using clear will clear all, but can accept new disposable
////            compositeDisposable.clear();
//            // Using dispose will clear all and set isDisposed = true, so it will not accept any new disposable
//            compositeDisposable.dispose();
//            compositeDisposable = null;
//        }
//        if (disposableMap != null && disposableMap.size() > 0) {
//            disposableMap.clear();
//        }
//    }

    /**
     * 根据标识符移除Disposable
     *
     * @param tags 标识符
     */
//    protected void removeDisposableByTag(String... tags) {
//        if (disposableMap != null && disposableMap.size() > 0) {
//            for (String tag : tags) {
//                if (disposableMap.containsKey(tag)) {
//                    compositeDisposable.remove(disposableMap.get(tag));
//                    disposableMap.remove(tag);
//                }
//            }
//        }
//    }


    /**
     * 将参数值传递到下个页面
     *
     * @param name
     * @param value
     */
    public static void putParmToNextPage(String name, String value) {
        paramMap.put(name, value);
    }

    /**
     * 从上个页面取得传递参数的值
     *
     * @param name
     * @return
     */
    public String getValueFromPrePage(String name) {
        return valueMap.get(name);
    }

    /**
     * 将上个页面传递过来的参数值全部放到valueMap 中
     */
    public void initValueFromPrePage() {
        if (getIntent() == null) {
            return;
        }
        if (getIntent().getExtras() == null) {
            return;
        }
        Bundle extBundle = getIntent().getExtras();
        if (extBundle != null) {
            Iterator<String> it = extBundle.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (key == null) {
                    continue;
                }
                String value = extBundle.getString(key);
                valueMap.put(key, value);
            }
        }
    }

    /**
     * 跳转至下一个页面
     *
     * @param clazz
     */
    public static void toNextActivity(Context context, Class<? extends Activity> clazz, Activity preActivity) {
        Intent intent = new Intent(context, clazz);
        Bundle bundle = new Bundle();
        Iterator it = paramMap.keySet().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj != null) {
                String key = String.valueOf(obj);
                String value = paramMap.get(key);
                bundle.putString(key, value);
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        //5.0过渡动画适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(preActivity).toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    public void finish() {
        super.finish();
//        ActivityCollector.finishActivity(this);
    }

}
