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

import com.bumptech.glide.Glide;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
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
public class LoopAdapter extends LoopPagerAdapter {

    private Context context;
    private List<String> imgs = new ArrayList<>();

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
        notifyDataSetChanged();
    }

    public LoopAdapter(Context context, RollPagerView viewPager) {
        super(viewPager);
        this.context = context;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.item_banner_img, null);
        ImageView iv_img = (ImageView) view.findViewById(R.id.iv_banner_img);

        GlideApp.with(context)
                .load(HttpIP.BaseImg + imgs.get(position))
                .placeholder(R.mipmap.not_4) //等待时的图片
                .error(R.mipmap.not_4)       //加载失败的图片
                .dontAnimate()
                .into(iv_img);
        return view;
    }

    @Override
    public int getRealCount() {
        return imgs.size();
    }

}
