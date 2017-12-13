package com.baidu.mapapi.utils;

import android.app.Activity;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-02-14 16:49
 * <p>
 * 百度定位工具类
 */
public class BDLocationHelper {

    private ArrayList<Integer> CODES = new ArrayList<>();

    private static BDLocationHelper myLocation;
    private Activity context;
    private LocationClient mLocClient; //定位SDK的核心类
    private int millisecond;
    private LocationCallback callback;

    private BDLocationHelper(Activity context) {
        this.context = context;
    }

    public static BDLocationHelper getInstance(Activity ctx) {
        if (myLocation == null)
            myLocation = new BDLocationHelper(ctx);
        return myLocation;
    }

    public void startLocation(int code, LocationCallback callback) {
        this.callback = callback;
        addCode(code);
        myLocation();
    }

    public void stopLocation() {
        if (mLocClient != null)
            mLocClient.stop();
        myLocation = null;
    }

    public void addCode(int code) {
        if (!CODES.contains(code)) CODES.add(code);
    }

    public void removeCode(int code) {
        if (CODES.contains(code)) CODES.remove(code);
    }

    public void clearCodes() {
        CODES.clear();
    }

    public BDLocationHelper setDuration(int millisecond) {
        this.millisecond = millisecond;
        return this;
    }

    /**
     * 开启定位功能
     */
    private void myLocation() {
        //定位初始化
        if (mLocClient != null) mLocClient.stop();
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(new MyLocationListener()); //注册定位监听器
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);                                      //打开gps
        option.setAddrType("all");                                    //返回的定位结果包含地址信息
        option.setCoorType("bd09ll");                                 //返回的定位结果是百度经纬度,默认值gcj02
        option.setPriority(LocationClientOption.GpsFirst);            //设置GPS优先
        if (millisecond > 0) option.setScanSpan(millisecond);         //设置发起定位请求的间隔时间
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位接口，需要实现两个方法
     */
    public class MyLocationListener implements BDLocationListener {
        /**
         * 接收异步返回的定位结果，参数是BDLocation类型参数
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            // mapview 销毁后不在处理新接收的位置
            if (null != location) {
                StringBuilder sb = new StringBuilder(256);
                sb.append("\nprovince : ");
                sb.append(location.getProvince());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\ncityCode : ");
                sb.append(location.getCityCode());
                sb.append("\nnetwork : ");
                sb.append(location.getNetworkLocationType());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    sb.append("\naddr : ");
                    String sAddr = location.getAddrStr();
                    sb.append(sAddr);
                }
                Log.i("BDLocation", sb.toString());

                callback.doWork(location, CODES.toArray());
            } else {
                callback.doWork(null, CODES.toArray());
            }
        }

        /**
         * 接收异步返回的POI查询结果，参数是BDLocation类型参数
         */

        public void onReceivePoi(BDLocation poiLocation) {
        }

    }

    public interface LocationCallback {
        void doWork(BDLocation location, Object... codes);
    }

}
