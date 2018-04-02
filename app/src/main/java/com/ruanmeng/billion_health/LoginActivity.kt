package com.ruanmeng.billion_health

import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import cn.jpush.android.api.JPushInterface
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.RongCloudContext
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.LoginMessageEvent
import com.ruanmeng.model.MainMessageEvent
import com.ruanmeng.share.Const
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import io.rong.imkit.RongIM
import io.rong.push.RongPushClient
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        transparentStatusBar(false)

        init_title()
    }

    override fun init_title() {
        bt_login.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
        bt_login.isClickable = false

        et_name.addTextChangedListener(this)
        et_pwd.addTextChangedListener(this)
        cb_pwd.setOnCheckedChangeListener(this)

        if (!TextUtils.isEmpty(getString("mobile"))) {
            et_name.setText(getString("mobile"))
            et_name.setSelection(et_name.text.length)
        }

        if (intent.getBooleanExtra("offLine", false)) {
            if (!intent.getBooleanExtra("isHidden", false)) showToask("当前账户在其他设备登录")

            clearData()

            ActivityStack.getScreenManager().popAllActivityExcept(MainActivity::class.java, LoginActivity::class.java)
        }
    }

    override fun doClick(v: View) {
        when (v.id) {
            R.id.bt_login -> {
                if (!CommonUtil.isMobileNumber(et_name.text.toString())) {
                    showToask("手机号码格式错误，请重新输入")
                    return
                }
                if (et_pwd.text.length < 6) {
                    showToask("密码长度不少于6位")
                    return
                }

                OkGo.post<String>(HttpIP.login_sub)
                        .tag(this@LoginActivity)
                        .isMultipart(true)
                        .params("accountName", et_name.text.toString())
                        .params("password", et_pwd.text.toString())
                        .params("loginType", "mobile")
                        .execute(object : StringDialogCallback(this@LoginActivity) {
                            /*{
                                "msg": "登录成功",
                                "msgcode": 100,
                                "object": {
                                    "cusTel": "",
                                    "isPass": 0,
                                    "mobile": "17603870563",
                                    "msgsNum": 28,
                                    "rongtoken": "Q4DZc5aadc9rp8f7PWRFNc6IEHMWJRjuP/Un2Bh7R8OALyAioDNc8UX8RXn+TR8MVsC9MnbwkUndHZG64866M6nQ9l89IT/nN2JWgpjVAAH9/snaemskGR3HEfdi28dVDHGbu4/6NK8=",
                                    "token": "31743A18B53842298BC9DDF861651658"
                                }
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                val obj = JSONObject(response.body()).getJSONObject("object")

                                putBoolean("isLogin", true)
                                putString("cusTel", obj.getString("cusTel"))
                                putString("isPass", obj.getString("isPass"))
                                putString("mobile", obj.getString("mobile"))
                                putString("msgsNum", obj.getString("msgsNum"))
                                putString("rongtoken", obj.getString("rongtoken"))
                                putString("token", obj.getString("token"))

                                //设置别名（先初始化）
                                JPushInterface.setAlias(
                                        applicationContext,
                                        Const.JPUSH_SEQUENCE,
                                        getString("token"))

                                EventBus.getDefault().post(LoginMessageEvent(getString("token"), "登录成功"))
                                EventBus.getDefault().post(MainMessageEvent(getString("token"), "登录成功"))

                                ActivityStack.getScreenManager().popActivities(this@LoginActivity::class.java)
                            }

                        })
            }
            R.id.tv_sign -> { startActivity(DealActivity::class.java) }
            R.id.tv_forget -> { startActivity(ForgetActivity::class.java) }
            R.id.login_qq -> { }
            R.id.login_weixin -> { }
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
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_name.text.toString())
                && !TextUtils.isEmpty(et_pwd.text.toString())) {
            bt_login.setBackgroundResource(R.drawable.rec_bg_green)
            bt_login.isClickable = true
        } else {
            bt_login.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
            bt_login.isClickable = false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            et_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            et_pwd.setSelection(et_pwd.text.length)
        } else {
            et_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
            et_pwd.setSelection(et_pwd.text.length)
        }
    }
}
