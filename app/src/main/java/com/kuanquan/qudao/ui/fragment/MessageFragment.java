package com.kuanquan.qudao.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuanquan.qudao.R;

/**
 * 消息主页
 */
public class MessageFragment extends Fragment {


    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view_tsnackbar_layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

}
