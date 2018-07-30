package com.kuanquan.qudao.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.fly.baselibrary.utils.base.GsonUtils;
import com.example.fly.baselibrary.utils.useful.GlideUtil;
import com.example.fly.baselibrary.utils.useful.LogUtil;
import com.kuanquan.qudao.R;
import com.kuanquan.qudao.bean.HomeData;
import com.kuanquan.qudao.utils.CollectionsUtil;
import java.util.List;

/**
 * Created by fei.wang on 2018/6/11.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ONE = 0;
    private final int THREE = 2;
    private List<HomeData> lists;
    private ViewGroup parentF;

    public HomeAdapter(OnHomeListener mOnHomeListener) {
        this.mOnHomeListener = mOnHomeListener;
    }

    public void setData(List<HomeData> homeBeans) {
        this.lists = homeBeans;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentF = parent;
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case ONE:
                view = View.inflate(parent.getContext(), R.layout.adapter_live_open_layout, null);
                holder = new HomeAdapter_release.LiveHolder(view);
                return holder;
            case THREE:
                view = View.inflate(parent.getContext(), R.layout.adapter_discover_layout, null);
                holder = new HomeAdapter_release.DiscoverHolder(view);
                return holder;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeData homeBean = lists.get(position);
        if (holder instanceof LiveHolder) {  // live
            LiveHolder mLiveHolder = (LiveHolder) holder;
            GlideUtil.setImageCircle(parentF.getContext(), homeBean.image, mLiveHolder.live_open_head_image);
            mLiveHolder.live_open_title.setText(homeBean.title);
            mLiveHolder.live_open_content.setText(homeBean.content);
//            mLiveHolder.itemView.setContentDescription("直播");
        } else if (holder instanceof DiscoverHolder) {  // 发现
            DiscoverHolder mDiscoverHolder = (DiscoverHolder) holder;
            if (homeBean != null) {
                CollectionsUtil.setTextView(mDiscoverHolder.content, homeBean.content);
                CollectionsUtil.setTextView(mDiscoverHolder.title, homeBean.title);
                GlideUtil.setImage(parentF.getContext(), homeBean.image, mDiscoverHolder.image);
            }
            mDiscoverHolder.colse.setOnClickListener(new CloseOnClick(position, mDiscoverHolder.colse));
            if (position == 0) {
                mDiscoverHolder.text_discover_title_stick.setVisibility(View.VISIBLE);
            }else{
                mDiscoverHolder.text_discover_title_stick.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = lists.get(position).itemType;
        if (itemType == 0) {        //是直播布局
            return ONE;
        } else if (itemType == 2) { //是发现布局
            return THREE;
        } else {//其他位置返回正常的布局
            return THREE;
        }
    }

    /**
     * Live 的ViewHolder
     */
    public static class LiveHolder extends RecyclerView.ViewHolder {
        ImageView live_open_head_image;
        TextView live_open_title, live_open_content;

        public LiveHolder(View itemView) {
            super(itemView);
            live_open_head_image = itemView.findViewById(R.id.live_open_head_image);
            live_open_title = itemView.findViewById(R.id.live_open_title);
            live_open_content = itemView.findViewById(R.id.live_open_content);
        }
    }

    /**
     * discover的ViewHolder
     */
    public static class DiscoverHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        ImageView image, colse;
        RelativeLayout text_discover_title_stick;

        public DiscoverHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_discover_title);
            content = itemView.findViewById(R.id.text_discover_bottom_content);
            image = itemView.findViewById(R.id.text_discover_image);
            colse = itemView.findViewById(R.id.text_discover_image_close);
            text_discover_title_stick = itemView.findViewById(R.id.text_discover_title_stick);
        }
    }

    class CloseOnClick implements View.OnClickListener {
        int position;
        View close;

        public CloseOnClick(int position, View close) {
            this.close = close;
            this.position = position;
        }

        @Override
        public void onClick(View view) {

            LogUtil.e("点击的角标 = " + position);
        }
    }

    private OnHomeListener mOnHomeListener;

    public interface OnHomeListener {
        void onNotify();
    }
}
