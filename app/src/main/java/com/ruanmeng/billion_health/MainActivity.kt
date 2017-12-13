package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import cn.jpush.android.api.JPushInterface
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.RongCloudContext
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.fragment.MainFirstFragment
import com.ruanmeng.fragment.MainFourthFragment
import com.ruanmeng.fragment.MainSecondFragment
import com.ruanmeng.fragment.MainThirdFragment
import com.ruanmeng.http.OkGoUpdateHttpUtil
import com.ruanmeng.http.dialog
import com.ruanmeng.model.MainMessageEvent
import com.ruanmeng.share.Const
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.Tools
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app_kotlin.check
import com.vector.update_app_kotlin.updateApp
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.push.RongPushClient
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.*

class MainActivity : BaseActivity() {

    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbarVisibility(false)
        init_title()

        EventBus.getDefault().register(this@MainActivity)
        checkUpdate()

        if (getBoolean("isLogin")) {
            JPushInterface.resumePush(applicationContext)

            //设置别名（先注册）
            JPushInterface.setAlias(
                    applicationContext,
                    Const.JPUSH_SEQUENCE,
                    getString("token"))
        }

        //清除 Push 通知栏推送
        RongPushClient.clearAllPushNotifications(applicationContext)

        main_check1.performClick()
    }

    override fun onStart() {
        super.onStart()

        if (getBoolean("isLogin") && getString("rongtoken") != "") {
            if (!isConnected) connect(getString("rongtoken"))
        }
    }

    override fun init_title() {
        main_check1.setOnCheckedChangeListener(this)
        main_check2.setOnCheckedChangeListener(this)
        main_check3.setOnCheckedChangeListener(this)
        main_check4.setOnCheckedChangeListener(this)

        main_check2.setOnTouchListener { _, event ->
            if (!getBoolean("isLogin")) {
                if (event.action == MotionEvent.ACTION_UP) startActivity(LoginActivity::class.java)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
        main_check3.setOnTouchListener { _, event ->
            if (!getBoolean("isLogin")) {
                if (event.action == MotionEvent.ACTION_UP) startActivity(LoginActivity::class.java)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
        main_check4.setOnTouchListener { _, event ->
            if (!getBoolean("isLogin")) {
                if (event.action == MotionEvent.ACTION_UP) startActivity(LoginActivity::class.java)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        // instantiateItem从FragmentManager中查找Fragment，找不到就getItem新建一个，
        // setPrimaryItem设置隐藏和显示，最后finishUpdate提交事务。
        if (isChecked) {
            val fragment = mFragmentPagerAdapter
                    .instantiateItem(main_container, buttonView.id) as Fragment
            mFragmentPagerAdapter.setPrimaryItem(main_container, 0, fragment)
            mFragmentPagerAdapter.finishUpdate(main_container)
        }
    }

    private val mFragmentPagerAdapter = object : FragmentPagerAdapter(
            supportFragmentManager) {

        override fun getItem(position: Int): Fragment = when (position) {
            R.id.main_check1 -> MainFirstFragment()
            R.id.main_check2 -> MainSecondFragment()
            R.id.main_check3 -> MainThirdFragment()
            R.id.main_check4 -> MainFourthFragment()
            else -> MainFirstFragment()
        }

        override fun getCount(): Int = 4
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.first_city -> startActivity(CityActivity::class.java)
            R.id.first_search -> startActivity(SearchActivity::class.java)
            R.id.first_msg -> main_check3.performClick()
            R.id.first_child -> {
                intent = Intent(baseContext, DoctorFindActivity::class.java)
                intent.putExtra("name", "儿童")
                startActivity(intent)
            }
            R.id.first_young -> {
                intent = Intent(baseContext, DoctorFindActivity::class.java)
                intent.putExtra("name", "青少年")
                startActivity(intent)
            }
            R.id.first_man -> {
                intent = Intent(baseContext, DoctorFindActivity::class.java)
                intent.putExtra("name", "成人")
                startActivity(intent)
            }
            R.id.first_health -> {
                intent = Intent(baseContext, DoctorFindActivity::class.java)
                intent.putExtra("name", "健康")
                startActivity(intent)
            }
        }
    }

    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                showToask("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                onBackPressed()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        EventBus.getDefault().unregister(this@MainActivity)

        RongCloudContext.getInstance().clearNotificationMessage()
        RongIM.getInstance().disconnect()
        super.onBackPressed()
    }

    @Subscribe
    fun onMessageEvent(event: MainMessageEvent) {
        if (event.name == "登录成功") {
            if (!isConnected) connect(getString("rongtoken"))
        }
        if (event.name == "退出登录") {
            isConnected = false
            main_check1.performClick()
        }
        if (event.name == "异地登录") {
            isConnected = false
            window.decorView.postDelayed({
                val intent = Intent(baseContext, LoginActivity::class.java)
                intent.putExtra("offLine", true)
                startActivity(intent)
            }, 500)
        }
        if (event.name == "重新登录") {
            isConnected = false
            main_check1.performClick()
            window.decorView.postDelayed({
                val intent = Intent(baseContext, LoginActivity::class.java)
                intent.putExtra("offLine", true)
                intent.putExtra("isHidden", true)
                startActivity(intent)
            }, 500)
        }
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
            onBefore { /*showLoadingDialog()*/ }
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
            noNewApp { /*toast("当前已是最新版本！")*/ }
            onAfter { /*cancelLoadingDialog()*/ }
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

    private fun connect(token: String) {
        /**
         * IMKit SDK调用第二步,建立与服务器的连接
         *
         * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #init(Context)} 之后调用。</p>
         * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
         * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
         *
         * @param token    从服务端获取的用户身份令牌（Token）。
         * @param callback 连接回调。
         * @return RongIM  客户端核心类的实例。
         */
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            /*
             * 连接融云成功，返回当前 token 对应的用户 id
             */
            override fun onSuccess(userid: String) {
                isConnected = true
                OkLogger.i("融云连接成功， 用户ID：" + userid)
                OkLogger.i(RongIMClient.getInstance().currentConnectionStatus.message)

                RongCloudContext.getInstance().connectedListener()
            }

            /*
             * 连接融云失败 errorCode 错误码，可到官网 查看错误码对应的注释
             */
            override fun onError(errorCode: RongIMClient.ErrorCode) {
                OkLogger.e("融云连接失败，错误码：" + errorCode.message)
            }

            /*
             * Token 错误。可以从下面两点检查
             * 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
             * 2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
             */
            override fun onTokenIncorrect() {
                OkLogger.e("融云token错误！！！")
            }
        })
    }
}
