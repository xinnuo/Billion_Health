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
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        init_title("意见反馈")
    }

    override fun init_title() {
        super.init_title()
        bt_quit.setBackgroundResource(R.drawable.rec_ova_bg_d5d5d5)
        bt_quit.isClickable = false

        et_content.filters = arrayOf<InputFilter>(NameLengthFilter(300))
        et_content.addTextChangedListener(this@FeedbackActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.bt_quit ->
                OkGo.post<String>(HttpIP.consult_sub)
                    .tag(this@FeedbackActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("content", et_content.text.toString())
                    .execute(object : StringDialogCallback(baseContext) {
                        /*{
                            "msg": "用户问题反馈添加成功",
                            "msgcode": 100
                        }*/
                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            showToask(msg)

                            ActivityStack.getScreenManager().popActivities(this@FeedbackActivity::class.java)
                        }

                    })
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_content.text.toString())) {
            bt_quit.setBackgroundResource(R.drawable.rec_ova_bg_green)
            bt_quit.isClickable = true
        } else {
            bt_quit.setBackgroundResource(R.drawable.rec_ova_bg_d5d5d5)
            bt_quit.isClickable = false
        }
    }
}
