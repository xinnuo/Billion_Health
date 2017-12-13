package com.ruanmeng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruanmeng.billion_health.R;
import com.ruanmeng.model.CityData;

import java.util.List;

public class HotCityGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<CityData> mCities;

    public HotCityGridAdapter(Context context, List<CityData> items) {
        this.mContext = context;
        this.mCities = items;
    }

    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public String getItem(int position) {
        return mCities == null ? null : mCities.get(position).getAreaName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        HotCityViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_city_gridview, parent, false);
            holder = new HotCityViewHolder();
            holder.name = (TextView) view.findViewById(R.id.tv_hot_city_name);
            view.setTag(holder);
        } else {
            holder = (HotCityViewHolder) view.getTag();
        }
        holder.name.setText(mCities.get(position).getAreaName());
        return view;
    }

    private static class HotCityViewHolder {
        TextView name;
    }
}
