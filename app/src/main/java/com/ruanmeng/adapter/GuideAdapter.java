/**
 * created by 小卷毛, 2016/12/10
 * Copyright (c) 2016, 416143467@qq.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.ruanmeng.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.ruanmeng.billion_health.R;
import com.ruanmeng.model.GlideApp;
import com.ruanmeng.share.HttpIP;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2016-12-10 11:13
 */
public class GuideAdapter extends StaticPagerAdapter {

    private Context context;
    private List<Integer> imgs = new ArrayList<>();

    public void setImgs(List<Integer> imgs) {
        this.imgs = imgs;
        notifyDataSetChanged();
    }

    public GuideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.item_banner_img, null);
        ImageView iv_img = view.findViewById(R.id.iv_banner_img);

        GlideApp.with(context)
                .load(imgs.get(position))
                .dontAnimate()
                .into(iv_img);
        return view;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }
}
