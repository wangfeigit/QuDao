package com.kuanquan.qudao.bean;

//import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/11.
 *
 */
//public class HomeBean implements MultiItemEntity,Serializable {
public class HomeBean implements Serializable {
    public static final int ONE = 0;
    public static final int TWO = 1;
    public static final int THREE = 2;
    public static final int FOUR = 3;
    public static final int FIVE = 4;

    public String title;
    public String content;
    public String image;
    public String isDiscover;  // 1 表示显示发现的标题栏   2 不显示
    public String head;  // 头部
    public int position;  // 角标

    public int itemType;   // 0 banner  1 (5个item布局)  2 项目item  3 直播布局 (水平滚动) 4 高顿头条 5 (发现布局)

    public List<HomeBeanChild> lists = new ArrayList<>();   // 直播列表（横向）
    public List<HomeBeanChild> lives = new ArrayList<>();   // 直播列表（横向）

    public List<HomeBeanChild> tabItems = new ArrayList<>();   // 5个item

    public List<HomeBeanChild> banners = new ArrayList<>();   // banner

    public List<HomeBeanChild> projects = new ArrayList<>();   // 项目

//    public HomeBean(int itemType) {
//        this.itemType = itemType;
//    }

//    @Override
//    public int getItemType() {
//        return itemType;
//    }
}
