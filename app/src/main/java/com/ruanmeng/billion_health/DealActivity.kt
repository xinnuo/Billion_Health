package com.ruanmeng.billion_health

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.share.HttpIP
import kotlinx.android.synthetic.main.activity_deal.*
import org.json.JSONObject

class DealActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)
        init_title("注册协议")

        getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        deal_web.apply {
            //支持javascript
            settings.javaScriptEnabled = true
            // 设置可以支持缩放
            settings.setSupportZoom(true)
            // 自适应屏幕
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        bt_agree.setOnClickListener {
            startActivity(RegisterActivity::class.java)
            onBackPressed()
        }
    }

    override fun getData() {
        OkGo.post<String>(HttpIP.gvrp)
                .tag(this@DealActivity)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val str = "<!doctype html><html>\n" +
                                "<meta charset=\"utf-8\">" +
                                "<style type=\"text/css\">" +
                                "body{ padding:0; margin:0; }\n" +
                                ".con{ width:95%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                                ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                                "*{ max-width:100% !important; }\n" +
                                "</style>\n" +
                                "<body style=\"padding:0; margin:0; \">" +
                                "<div class=\"con\">" +
                                JSONObject(response.body()).getString("zcxy") +
                                "</div>" +
                                "</body>" +
                                "</html>"

                        deal_web.loadDataWithBaseURL(HttpIP.BaseImg, str, "text/html", "utf-8", "")
                    }

                })
    }
}
