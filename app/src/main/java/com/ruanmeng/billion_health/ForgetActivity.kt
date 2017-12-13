package com.ruanmeng.billion_health

import android.os.Bundle
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.fragment.RegisterFirstFragment
import com.ruanmeng.fragment.RegisterSecondFragment
import com.ruanmeng.fragment.RegisterThirdFragment
import com.ruanmeng.model.LoginMessageEvent
import com.ruanmeng.model.MainMessageEvent
import com.ruanmeng.share.Const
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class ForgetActivity : BaseActivity() {

    lateinit var first : RegisterFirstFragment
    lateinit var second : RegisterSecondFragment
    lateinit var third : RegisterThirdFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)
        init_title("忘记密码")

        first = RegisterFirstFragment()
        second = RegisterSecondFragment()
        third = RegisterThirdFragment()

        val extra = Bundle()
        extra.putBoolean("isGone", true)
        first.arguments = extra

        supportFragmentManager
                .beginTransaction()
                .add(R.id.fl_forget_container, first)
                .commit()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_next -> {
                if (!CommonUtil.isMobileNumber(first.getMobile())) {
                    showToask("手机号码格式错误，请重新输入")
                } else {
                    supportFragmentManager
                            .beginTransaction()
                            /*.setCustomAnimations(
                                R.anim.push_left_in,
                                R.anim.push_left_out,
                                R.anim.push_right_in,
                                R.anim.push_right_out)*/
                            .add(R.id.fl_forget_container, second)
                            .addToBackStack(null)
                            .commit()
                }
            }
            R.id.bt_yzm -> {
                second.getMobileRequest(HttpIP.identify_getbyforget, first.getMobile())
            }
            R.id.bt_next2 -> {
                if (second.getMobileCode() != second.getMobileYzm()) {
                    showToask("验证码错误，请重新输入")
                } else {
                    supportFragmentManager
                            .beginTransaction()
                            /*.setCustomAnimations(
                                R.anim.push_left_in,
                                R.anim.push_left_out,
                                R.anim.push_right_in,
                                R.anim.push_right_out)*/
                            .add(R.id.fl_forget_container, third)
                            .addToBackStack(null)
                            .commit()
                }
            }
            R.id.bt_next3 -> {
                if (third.getPassword().length < 6) {
                    showToask("密码长度不少于6位")
                } else {
                    OkGo.post<String>(HttpIP.pwd_forget_sub)
                            .tag(this@ForgetActivity)
                            .isMultipart(true)
                            .params("mobile", first.getMobile())
                            .params("smscode", second.getMobileCode())
                            .params("newpwd", third.getPassword())
                            .execute(object : StringDialogCallback(this@ForgetActivity) {
                                /*{
                                    "msg": "密码修改成功",
                                    "msgcode": 100
                                }*/
                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    showToask(msg)

                                    startLogin()
                                }

                            })
                }
            }
        }
    }

    fun startLogin() {
        OkGo.post<String>(HttpIP.login_sub)
                .tag(this@ForgetActivity)
                .isMultipart(true)
                .params("accountName", first.getMobile())
                .params("password", third.getPassword())
                .params("loginType", "mobile")
                .execute(object : StringDialogCallback(this@ForgetActivity) {
                    /*{
                        "msg": "登录成功",
                        "msgcode": 100,
                        "object": {
                            "cusTel": "",
                            "isPass": 0,
                            "mobile": "17603870563",
                            "msgsNum": 28,
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

                        ActivityStack.getScreenManager().popActivities(ForgetActivity::class.java, LoginActivity::class.java)
                    }

                })
    }
}
