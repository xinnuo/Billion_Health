package com.ruanmeng.billion_health

import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.NameLengthFilter
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        init_title("个性签名")
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_d5d5d5)
        bt_ok.isClickable = false

        et_content.filters = arrayOf<InputFilter>(NameLengthFilter(30))
        et_content.addTextChangedListener(this@SignActivity)

        if (getString("sign") != "") {
            et_content.setText(getString("sign"))
            et_content.setSelection(et_content.text.length)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.bt_ok -> {
                if (et_content.text.toString() == getString("sign")) {
                    showToask("未做任何修改")
                    return
                }

                OkGo.post<String>(HttpIP.sign_change_sub)
                        .tag(this@SignActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("sign", et_content.text.toString())
                        .execute(object : StringDialogCallback(this@SignActivity) {
                            /*{
                                "msg": "修改成功",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                showToask(msg)
                                putString("sign", et_content.text.toString())

                                ActivityStack.getScreenManager().popActivity()
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_content.text.toString())) {
            bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_green)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_ova_bg_d5d5d5)
            bt_ok.isClickable = false
        }
    }
}
