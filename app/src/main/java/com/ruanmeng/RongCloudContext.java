package com.ruanmeng;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.ruanmeng.billion_health.MainActivity;
import com.ruanmeng.billion_health.R;
import com.ruanmeng.model.MainMessageEvent;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.utils.PreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-21 10:09
 * <p>
 * 描述：融云相关监听 事件集合类
 */
public class RongCloudContext implements RongIM.ConversationListBehaviorListener, //设置会话列表界面操作的监听器
        RongIM.ConversationBehaviorListener,                                      //设置会话界面操作的监听器
        RongIMClient.OnReceiveMessageListener,                                    //设置接收消息的监听器
        RongIM.OnSendMessageListener,                                             //设置发送消息的监听
        RongIMClient.ConnectionStatusListener,                                    //设置连接状态变化的监听器
        RongIM.LocationProvider,       //位置信息的提供者，实现后获取用户位置信息
        RongIM.UserInfoProvider,       //设置用户信息的提供者，供RongIM调用获取用户名称和头像信息
        RongIM.GroupInfoProvider,      //群组信息的提供者
        RongIM.GroupUserInfoProvider   //群组中用户信息的提供者
{

    private static final String TAG = RongCloudContext.class.getSimpleName();
    private static RongCloudContext mRongCloudInstance;
    private Context mContext;

    private NotificationManager manager;

    private RongCloudContext(Context mContext) {
        this.mContext = mContext;
        initListener();
    }

    /**
     * 初始化 RongCloud.
     */
    public static void init(Context context) {
        if (mRongCloudInstance == null) {
            synchronized (RongCloudContext.class) {
                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudContext(context);
                }
            }
        }
    }

    /**
     * 获取RongCloud 实例。
     */
    public static RongCloudContext getInstance() {
        return mRongCloudInstance;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * init 后就能设置的监听
     */
    private void initListener() {
        RongIM.setConversationListBehaviorListener(this);  //设置会话列表界面操作的监听器
        RongIM.setConversationBehaviorListener(this);      //设置会话界面操作的监听器
        RongIM.setOnReceiveMessageListener(this);          //设置接收消息的监听器
        RongIM.getInstance().setSendMessageListener(this); //设置发送消息的监听
        RongIM.setConnectionStatusListener(this);          //设置连接状态变化的监听器

        setCustomExtensionModule(); //自定义输入区域扩展栏

        Conversation.ConversationType[] types = new Conversation.ConversationType[]{
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
                Conversation.ConversationType.DISCUSSION
        };
        RongIM.getInstance().setReadReceiptConversationTypeList(types); //设置发送消息回执的会话类型
    }

    /**
     * connect 后能设置的监听
     */
    public void connectedListener() {
        RongIM.getInstance().enableNewComingMessageIcon(true); //显示新消息提醒
        RongIM.getInstance().enableUnreadMessageIcon(true);    //显示未读消息数目

        RongIM.getInstance().setMessageAttachedUserInfo(true); //设置消息体内是否携带用户信息

        RongIM.setLocationProvider(this);            //设置地理位置提供者
        RongIM.setUserInfoProvider(this, true);      //设置用户信息的提供者
        RongIM.setGroupInfoProvider(this, true);     //群组信息的提供者
        RongIM.setGroupUserInfoProvider(this, true); //群组中用户信息的提供者
    }

    /**
     * 自定义输入区域扩展栏
     * <p>
     * 可以通过更改 rc_fr_conversation.xml 里 app:RCStyle="SCE" ，更改默认输入显示形式
     */
    public void setCustomExtensionModule() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new CustomExtensionModule());
            }
        }
    }

    public void setDefaultExtensionModule() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule currentModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof CustomExtensionModule) {
                    currentModule = module;
                    break;
                }
            }
            if (currentModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(currentModule);
                RongExtensionManager.getInstance().registerExtensionModule(new DefaultExtensionModule());
            }
        }
    }

    /**
     * 退出应用时，清除通知栏消息
     */
    public void clearNotificationMessage() {
        if (manager != null) manager.cancelAll();
    }

    /**
     * 当点击会话头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param targetId         被点击的用户id。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String targetId) {
        return false;
    }

    /**
     * 当长按会话头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param targetId         被点击的用户id。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String targetId) {
        return false;
    }

    /**
     * 长按会话列表中的 item 时执行。
     *
     * @param context        上下文。
     * @param view           触发点击的 View。
     * @param uiConversation 长按时的会话条目。
     * @return 如果用户自己处理了长按会话后的逻辑处理，则返回 true， 否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    /**
     * 点击会话列表中的 item 时执行。
     *
     * @param context        上下文。
     * @param view           触发点击的 View。
     * @param uiConversation 会话条目。
     * @return 如果用户自己处理了点击会话后的逻辑处理，则返回 true， 否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }


    /**
     * 当点击用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param userInfo         被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    /**
     * 当长按用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param userInfo         被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    /**
     * 当点击消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被点击的消息的实体信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        return false;
    }

    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @param link    被点击的链接。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLinkClick(Context context, String link) {
        return false;
    }

    /**
     * 当长按消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被长按的消息的实体信息。
     * @return 如果用户自己处理了长按后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }


    /**
     * 收到消息的处理。
     *
     * @param message 收到的消息实体。
     * @param left    剩余未拉取消息数目。
     * @return 收到消息是否处理完成，true 表示自己处理铃声和后台通知，false 走融云默认处理方式。
     */
    @Override
    public boolean onReceived(Message message, int left) {
        String content = "";
        String tagetId = message.getTargetId();

        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage) { //文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            textMessage.getExtra();
            content = textMessage.getContent();

            Log.i(TAG, "onReceived-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) { //图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            content = "[图片]";

            Log.i(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) { //语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            content = "[语音]";

            Log.i(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof LocationMessage) { //位置消息
            LocationMessage locationMessage = (LocationMessage) messageContent;
            content = "[位置]";

            Log.i(TAG, "onReceived-locationMessage:" + locationMessage.getImgUri().toString());
        } else if (messageContent instanceof FileMessage) { //文件消息
            FileMessage fileMessage = (FileMessage) messageContent;
            content = "[文件]";

            Log.i(TAG, "onReceived-fileMessage:" + fileMessage.getFileUrl().toString());
        } else if (messageContent instanceof RichContentMessage) { //图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            content = "[图文]";

            Log.i(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
        } else if (messageContent instanceof InformationNotificationMessage) { //小灰条消息
            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
            content = "[小灰条]";

            Log.i(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
        } else {
            Log.i(TAG, "onReceived-其他消息，自己来判断处理");
        }

        String contentTitle = messageContent.getUserInfo() == null ? "对方消息" : messageContent.getUserInfo().getName();

        //在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service
        if (manager == null)
            manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //通过Notification.Builder来创建通知
        NotificationCompat.Builder notify = new NotificationCompat.Builder(mContext)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                //设置状态栏中的小图片，这个图片同样也是在下拉状态栏中所显示，需要更换更大的图片，可以使用setLargeIcon(Bitmap icon)
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置在status bar上显示的提示文字
                .setTicker("您有一条消息")
                //设置在下拉status bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentTitle(contentTitle)
                //TextView中显示的详细内容
                .setContentText(content)
                //是否可清除
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        Intent intent = new Intent();

        if (RongContext.getInstance() != null) {
            Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName)
                    .buildUpon()
                    .appendPath("conversation")
                    .appendPath(message.getConversationType().getName().toLowerCase())
                    .appendQueryParameter("targetId", messageContent.getUserInfo() == null ? tagetId : messageContent.getUserInfo().getUserId())
                    .appendQueryParameter("title", contentTitle)
                    .build();
            intent.setData(uri);
            intent.setAction("android.intent.action.VIEW");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 200, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notify.setContentIntent(pendingIntent);
        manager.notify(Math.abs(messageContent.getUserInfo() == null ?
                tagetId.hashCode() :
                messageContent.getUserInfo().getUserId().hashCode()), notify.build());

        return true;
    }


    /**
     * 消息发送前监听器处理接口（是否发送成功可以从 SentStatus 属性获取）。
     *
     * @param message 发送的消息实例。
     * @return 处理后的消息实例。
     */
    @Override
    public Message onSend(Message message) {
        return message;
    }

    /**
     * 消息在 UI 展示后执行/自己的消息发出后执行,无论成功或失败。
     *
     * @param message              消息实例。
     * @param sentMessageErrorCode 发送消息失败的状态码，消息发送成功 SentMessageErrorCode 为 null。
     * @return true 表示走自己的处理方式，false 走融云默认处理方式。
     */
    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        if (message.getSentStatus() == Message.SentStatus.FAILED) {
            switch (sentMessageErrorCode) {
                case NOT_IN_CHATROOM:       //不在聊天室
                    break;
                case NOT_IN_DISCUSSION:     //不在讨论组
                    break;
                case NOT_IN_GROUP:          //不在群组
                    break;
                case REJECTED_BY_BLACKLIST: //你在他的黑名单中
                    break;
            }
        }
        return false;
    }


    /**
     * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
     *
     * @param connectionStatus 网络状态。
     */
    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus) {
            case CONNECTED:
                Log.i(TAG, "onChanged: 连接成功");
                break;
            case DISCONNECTED:
                Log.i(TAG, "onChanged: 断开连接");
                break;
            case CONNECTING:
                Log.i(TAG, "onChanged: 连接中");
                break;
            case NETWORK_UNAVAILABLE:
                Log.i(TAG, "onChanged: 网络不可用");
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:
                Log.i(TAG, "onChanged: 用户账户在其他设备登录");

                EventBus.getDefault().post(new MainMessageEvent("", "异地登录"));

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                break;
        }
    }


    /**
     * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
     *
     * @param context          上下文
     * @param locationCallback 回调
     */
    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {
    }


    @Override
    public UserInfo getUserInfo(String userId) {
        return new UserInfo(
                PreferencesUtils.getString(mContext, "token"),
                PreferencesUtils.getString(mContext, "nickName"),
                Uri.parse(HttpIP.BaseImg + PreferencesUtils.getString(mContext, "userhead")));
    }


    @Override
    public Group getGroupInfo(String groupId) {
        return null;
    }


    @Override
    public GroupUserInfo getGroupUserInfo(String groupId, String userId) {
        return null;
    }
}
