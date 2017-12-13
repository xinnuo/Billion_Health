package com.ruanmeng.receiver;

import android.content.Context;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-21 11:11
 */

public class RongPushReceiver extends PushMessageReceiver {
    /**
     * push 通知到达事件
     *
     * @param context                 上下文
     * @param pushNotificationMessage 消息类型
     * @return 如果返回false, 会弹出融云SDK默认通知; 返回true, 融云SDK不会弹通知, 通知需要由您自定义
     */
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        return false;
    }

    /**
     * push 通知点击事件
     *
     * @param context                 上下文
     * @param pushNotificationMessage 消息类型
     * @return 如果返回false, 会走融云SDK默认处理逻辑, 即点击该通知会打开会话列表或会话界面; 返回true, 则由您自定义处理逻辑
     */
    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
        return false;
    }
}
