package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.MainMessageEvent
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_password.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class PasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        init_title("密码")
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_d5d5d5)
        bt_ok.isClickable = false

        et_old.addTextChangedListener(this@PasswordActivity)
        et_new.addTextChangedListener(this@PasswordActivity)
        et_confirm.addTextChangedListener(this@PasswordActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.bt_ok -> {
                if (et_old.text.length < 6) {
                    showToask("原密码长度不少于6位")
                    return
                }
                if (et_new.text.length < 6) {
                    showToask("新密码长度不少于6位")
                    return
                }
                if (et_confirm.text.length < 6) {
                    showToask("确认密码长度不少于6位")
                    return
                }
                if (et_new.text.toString() != et_confirm.text.toString()) {
                    showToask("密码不一致，请重新输入")
                    return
                }

                OkGo.post<String>(HttpIP.password_change_sub)
                        .tag(this@PasswordActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("oldPwd", et_old.text.toString())
                        .params("newPwd", et_new.text.toString())
                        .params("confirmPwd", et_confirm.text.toString())
                        .execute(object : StringDialogCallback(this@PasswordActivity) {
                            /*{
                                "msg": "密码修改成功!",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                showToask(msg)

                                EventBus.getDefault().post(MainMessageEvent("", "重新登录"))
                                putBoolean("isLogin", false)

                                val intent = Intent(baseContext, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)

                                ActivityStack.getScreenManager().popActivity()
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_old.text.toString())
                && !TextUtils.isEmpty(et_new.text.toString())
                && !TextUtils.isEmpty(et_confirm.text.toString())) {
            bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_green)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_d5d5d5)
            bt_ok.isClickable = false
        }
    }
}
