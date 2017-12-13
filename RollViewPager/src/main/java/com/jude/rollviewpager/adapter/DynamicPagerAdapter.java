package com.jude.rollviewpager.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


/**
 * 动态管理的Adapter。概念参照{@link android.support.v4.app.FragmentPagerAdapter}
 * 每次都会创建新view，销毁旧View。节省内存消耗性能
 *
 * <p>Subclasses only need to implement {@link #getView(ViewGroup,int)}
 * and {@link #getCount()} to have a working adapter.
 *
 * 动态的Adapter。当创建3号view时会销毁1号view(递推)，会时常调用getView。
 * 增加页面创建消耗，减小内存消耗。
 * 概念参照FragmentStatePagerAdapter。可以用于其他ViewPager。
 *
 */
public abstract class DynamicPagerAdapter extends PagerAdapter {

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View itemView = getView(container,position);
		container.addView(itemView);
		return itemView;
	}

	abstract View getView(ViewGroup container, int position);

}