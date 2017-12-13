package com.ruanmeng.billion_health

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.share.HttpIP
import kotlinx.android.synthetic.main.activity_nickname.*
import com.ruanmeng.utils.NameLengthFilter
import android.text.InputFilter
import com.ruanmeng.utils.ActivityStack
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo

class NicknameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)
        init_title("用户名")
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_d5d5d5)
        bt_ok.isClickable = false

        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(24))
        et_name.addTextChangedListener(this@NicknameActivity)

        if (!TextUtils.isEmpty(getString("nickName"))) {
            et_name.setText(getString("nickName"))
            et_name.setSelection(et_name.text.length)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.bt_ok -> {
                if (et_name.text.toString() == getString("nickName")) {
                    showToask("未做任何修改")
                    return
                }

                OkGo.post<String>(HttpIP.nickName_change_sub)
                        .tag(this@NicknameActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("nickName", et_name.text.toString())
                        .execute(object : StringDialogCallback(this@NicknameActivity) {
                            /*{
                                "msg": "更新成功",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                showToask(msg)
                                putString("nickName", et_name.text.toString())

                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        getString("token"),
                                        getString("nickName"),
                                        Uri.parse(HttpIP.BaseImg + getString("userhead"))))

                                ActivityStack.getScreenManager().popActivity()
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_name.text.toString())) {
            bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_green)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_d5d5d5)
            bt_ok.isClickable = false
        }
    }
}
