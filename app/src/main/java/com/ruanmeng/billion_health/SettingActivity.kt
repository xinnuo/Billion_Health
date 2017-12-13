package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.luck.picture.lib.tools.PictureFileUtils
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.RongCloudContext
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.http.*
import com.ruanmeng.model.MainMessageEvent
import com.ruanmeng.share.Const
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.GlideCacheUtil
import com.ruanmeng.utils.Tools
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app_kotlin.check
import com.vector.update_app_kotlin.updateApp
import io.rong.imkit.RongIM
import io.rong.push.RongPushClient
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.*

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_cache.setRightString(GlideCacheUtil.getInstance().getCacheSize(this@SettingActivity))
        setting_version.setRightString("v" + Tools.getVersion(this@SettingActivity))

        setting_about.setOnClickListener(this@SettingActivity)
        setting_help.setOnClickListener(this@SettingActivity)
        setting_cache.setOnClickListener(this@SettingActivity)
        setting_version.setOnClickListener(this@SettingActivity)
        setting_feedback.setOnClickListener(this@SettingActivity)
        bt_quit.setOnClickListener(this@SettingActivity)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v.id) {
            R.id.setting_about -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("name", "关于我们")
                startActivity(intent)
            }
            R.id.setting_help -> startActivity(HelpActivity::class.java)
            R.id.setting_cache -> {
                AlertDialog.Builder(this)
                        .setTitle("清空缓存")
                        .setMessage("确定要清空缓存吗？")
                        .setPositiveButton("清空") { dialog, _ ->
                            dialog.dismiss()

                            GlideCacheUtil.getInstance().clearImageAllCache(baseContext)
                            PictureFileUtils.deleteCacheDirFile(baseContext)
                            setting_cache.setRightString("0.0B")
                        }
                        .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
            }
            R.id.setting_version -> { checkUpdate() }
            R.id.setting_feedback -> { startActivity(FeedbackActivity::class.java) }
            R.id.bt_quit -> {
                AlertDialog.Builder(this)
                        .setTitle("退出登录")
                        .setMessage("确定要退出当前账号吗？")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("退出") { dialog, _ ->
                            dialog.dismiss()

                            OkGo.post<String>(HttpIP.logout_sub)
                                    .tag(this@SettingActivity)
                                    .headers("token", getString("token"))
                                    .execute(object : StringDialogCallback(this@SettingActivity) {
                                        /*{
                                            "msg": "退出成功",
                                            "msgcode": 100
                                        }*/
                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                            clearData()
                                        }

                                        /*{
                                            "msg": "登录超时,请重新登录!",
                                            "msgcode": 102
                                        }*/
                                        override fun onSuccessResponseErrorCode(response: Response<String>, msg: String, msgCode: String) {
                                            if (msgCode == "102" && msg == "登录超时,请重新登录!") clearData()
                                        }

                                    })
                        }
                        .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
            }
        }
    }

    private fun clearData() {
        putBoolean("isLogin", false)
        putString("cusTel", "")
        putString("isPass", "")
        putString("msgsNum", "")
        putString("token", "")
        putString("rongtoken", "")

        putString("age", "")
        putString("nickName", "")
        putString("sex", "")
        putString("sign", "")
        putString("userInfoId", "")
        putString("userhead", "")

        putString("provinceCode", "")
        putString("cityCode", "")
        putString("districtCode", "")
        putString("province", "")
        putString("city", "")
        putString("district", "")
        putString("lng", "")
        putString("lat", "")

        JPushInterface.stopPush(applicationContext)

        //清除通知栏消息
        RongCloudContext.getInstance().clearNotificationMessage()
        RongPushClient.clearAllPushNotifications(applicationContext)
        RongIM.getInstance().logout()

        EventBus.getDefault().post(MainMessageEvent("", "退出登录"))

        onBackPressed()
    }

    /**
     * 版本更新
     */
    private fun checkUpdate() {
        //下载路径
        val path = Environment.getExternalStorageDirectory().absolutePath + Const.SAVE_FILE
        //自定义参数
        val params = HashMap<String, String>()
        params.put("baseType", "1")

        updateApp(HttpIP.get_version, OkGoUpdateHttpUtil()) {
            //设置请求方式，默认get
            isPost = true
            //添加自定义参数
            setParams(params)
            //设置apk下砸路径
            targetPath = path
        }.check {
            onBefore { showLoadingDialog() }
            parseJson {
                val obj = JSONObject(it).getJSONObject("object")
                val version_new = Integer.parseInt(obj.optString("versionNo").replace(".", ""))
                val version_old = Integer.parseInt(Tools.getVersion(baseContext).replace(".", ""))

                UpdateAppBean()
                        //（必须）是否更新Yes,No
                        .setUpdate(if (version_new > version_old) "Yes" else "No")
                        //（必须）新版本号，
                        .setNewVersion(obj.optString("versionNo"))
                        //（必须）下载地址
                        .setApkFileUrl(obj.optString("url"))
                        // .setApkFileUrl("https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/app-debug.apk")
                        //（必须）更新内容
                        .setUpdateLog(obj.optString("content"))
                        //是否强制更新，可以不设置
                        .setConstraint(false)
            }
            hasNewApp { updateApp, updateAppManager -> showDownloadDialog(updateApp, updateAppManager) }
            noNewApp { toast("当前已是最新版本！") }
            onAfter { cancelLoadingDialog() }
        }
    }

    /**
     * 自定义对话框
     */
    private fun showDownloadDialog(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
        dialog("版本更新", "是否升级到${updateApp.newVersion}版本？\n\n${updateApp.updateLog}") {
            positiveButton("升级") {
                updateAppManager.download()
                dismiss()
            }
            negativeButton("暂不升级") { dismiss() }
            show()
        }
    }
}
