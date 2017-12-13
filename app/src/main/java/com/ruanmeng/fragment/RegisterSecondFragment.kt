package com.ruanmeng.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.billion_health.BuildConfig
import com.ruanmeng.billion_health.R
import kotlinx.android.synthetic.main.fragment_register_second.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class RegisterSecondFragment : BaseFragment() {

    private var time_count : Int = 90
    private lateinit var thread: Runnable
    private var isRunning : Boolean = true // 用于停止Runnable线程
    private var YZM: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_register_second, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_code.addTextChangedListener(this)
        bt_next2.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
        bt_next2.isClickable = false
    }

    fun getMobileRequest(url: String, tel: String) {
        thread = Runnable {
            if (isRunning) {
                bt_yzm.text = "$time_count 秒后重发"
                if (time_count > 0) {
                    bt_yzm.postDelayed(thread, 1000)
                    time_count--
                } else {
                    bt_yzm.text = "获取验证码"
                    bt_yzm.setBackgroundResource(R.drawable.rec_bg_trans_stroke_green)
                    bt_yzm.isClickable = true
                    bt_yzm.setTextColor(resources.getColor(R.color.green))
                    time_count = 90
                }
            }
        }

        OkGo.post<String>(url)
                .tag(this@RegisterSecondFragment)
                .isMultipart(true)
                .params("mobile", tel)
                .execute(object : StringDialogCallback(this@RegisterSecondFragment.activity){

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        /*{
                            "msg": "验证码发送成功",
                            "msgcode": 100,
                            "object": "770855"
                        }*/
                        YZM = JSONObject(response.body()).getString("object")
                        if (BuildConfig.LOG_DEBUG) et_code.setText(YZM)

                        bt_yzm.setBackgroundResource(R.drawable.rec_bg_trans_stroke_divider)
                        bt_yzm.isClickable = false
                        bt_yzm.setTextColor(resources.getColor(R.color.light))
                        isRunning = true
                        time_count = 90
                        bt_yzm.post(thread)
                    }

                })
    }

    fun getMobileYzm() : String = YZM

    fun getMobileCode() : String = et_code.text.toString()

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_code.text.toString())) {
            bt_next2.setBackgroundResource(R.drawable.rec_bg_green)
            bt_next2.isClickable = true
        } else {
            bt_next2.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
            bt_next2.isClickable = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        OkGo.getInstance().cancelTag(this@RegisterSecondFragment)
    }
}
