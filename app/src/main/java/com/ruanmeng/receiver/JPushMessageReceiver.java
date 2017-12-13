package com.ruanmeng.receiver;

import android.content.Context;

import com.lzy.okgo.utils.OkLogger;

import cn.jpush.android.api.JPushMessage;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-25 14:36
 */
public class JPushMessageReceiver extends cn.jpush.android.service.JPushMessageReceiver {

    /**
     * alias相关的操作会在此方法中回调结果
     *
     * @param context      上下文
     * @param jPushMessage alias相关操作返回的消息结果体
     */
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        OkLogger.i("JPush_Message", jPushMessage.toString());
    }

    /**
     * tag增删查改的操作会在此方法中回调结果
     *
     * @param context      上下文
     * @param jPushMessage tag相关操作返回的消息结果体
     */
    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        OkLogger.i("JPush_Message", jPushMessage.toString());
    }

    /**
     * 查询某个tag与当前用户的绑定状态的操作会在此方法中回调结果
     *
     * @param context      上下文
     * @param jPushMessage check tag与当前用户绑定状态的操作返回的消息结果体
     */
    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        OkLogger.i("JPush_Message", jPushMessage.toString());
    }
}
