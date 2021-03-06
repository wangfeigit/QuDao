package com.kuanquan.qudao.ui.animator;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kuanquan.qudao.R;


/**
 * 帧动画
 */
public class FrameActivity extends AppCompatActivity {
    private ImageView viewById;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        viewById = findViewById(R.id.image);
        animationDrawable = new AnimationDrawable();

        // 为AnimationDrawable添加动画帧
        animationDrawable.addFrame(getResources().getDrawable(R.mipmap.answer_f),50);
        animationDrawable.addFrame(getResources().getDrawable(R.mipmap.bank_f),50);
        animationDrawable.addFrame(getResources().getDrawable(R.mipmap.bottom_detail_discuss_wz),60);
        animationDrawable.addFrame(getResources().getDrawable(R.mipmap.answer_f),90);
        animationDrawable.addFrame(getResources().getDrawable(R.mipmap.elective_f),120);
        animationDrawable.addFrame(getResources().getDrawable(R.mipmap.ic_launcher),150);

        animationDrawable.setOneShot(false);  // 设置为循环播放，不设置默认为播放一次

        // 给ImageView设置背景为帧动画  AnimationDrawable
        viewById.setBackground(animationDrawable);
    }

    public void onClick(View view){
        if (!animationDrawable.isRunning()) {
            animationDrawable.start();  // 开启动画
        }else{
            animationDrawable.stop();  // 停止动画
        }
    }
}
