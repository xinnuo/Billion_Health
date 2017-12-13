package com.ruanmeng.billion_health

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.share.HttpIP
import kotlinx.android.synthetic.main.activity_web.*
import org.json.JSONObject

class WebActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        //关于我们，帮助详情，招商中心，公告详情，课程通知
        init_title(intent.getStringExtra("name"))

        when (intent.getStringExtra("name")) {
            "关于我们" -> {
                OkGo.post<String>(HttpIP.about_us)
                        .tag(this@WebActivity)
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
                                        JSONObject(response.body()).getString("about") +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                wv_web.loadDataWithBaseURL(HttpIP.BaseImg, str, "text/html", "utf-8", "")
                            }

                        })
            }
            "帮助详情" -> {
                OkGo.post<String>(HttpIP.help_center)
                        .tag(this@WebActivity)
                        .params("htmlKey", intent.getStringExtra("htmlKey"))
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
                                        JSONObject(response.body()).getString("help") +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                wv_web.loadDataWithBaseURL(HttpIP.BaseImg, str, "text/html", "utf-8", "")
                            }

                        })
            }
            "招商中心" -> {
                OkGo.post<String>(HttpIP.zszx)
                        .tag(this@WebActivity)
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
                                        JSONObject(response.body()).getString("zszx") +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                wv_web.loadDataWithBaseURL(HttpIP.BaseImg, str, "text/html", "utf-8", "")
                            }

                        })
            }
            "公告详情" -> {
                OkGo.post<String>(HttpIP.getnews_data)
                        .tag(this@WebActivity)
                        .params("newsId", intent.getStringExtra("newsId"))
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val str = "<!doctype html><html>\n" +
                                        "<meta charset=\"utf-8\">" +
                                        "<style type=\"text/css\">" +
                                        "body{ padding:0; margin:0; }\n" +
                                        ".view_h1{ width:95%; margin:0 auto; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.0em; }\n" +
                                        ".view_time{ width:95%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999; }\n" +
                                        ".con{ width:95%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                                        ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                                        "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                                        "*{ max-width:100% !important; }\n" +
                                        "</style>\n" +
                                        "<body style=\"padding:0; margin:0; \">" +
                                        "<div class=\"view_h1\">" +
                                        JSONObject(response.body()).getJSONObject("news").getString("title") +
                                        "</div>" +
                                        "<div class=\"view_time\" style=\"border-bottom:1px solid #e7e7e7; padding-bottom:5px;\">" +
                                        JSONObject(response.body()).getJSONObject("news").getString("createDate") +
                                        "</div>" +
                                        "<div class=\"con\">" +
                                        JSONObject(response.body()).getJSONObject("news").getString("content") +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                wv_web.loadDataWithBaseURL(HttpIP.BaseImg, str, "text/html", "utf-8", "")
                            }

                        })
            }
            "课程通知" -> {
                val str = "<!doctype html><html>\n" +
                        "<meta charset=\"utf-8\">" +
                        "<style type=\"text/css\">" +
                        "body{ padding:0; margin:0; }\n" +
                        ".view_h1{ width:95%; margin:0 auto; margin-top:20px; text-align:center; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.5em; }\n" +
                        ".view_time{ width:95%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999; }\n" +
                        ".con{ width:95%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                        ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                        "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                        "*{ max-width:100% !important; }\n" +
                        "</style>\n" +
                        "<body style=\"padding:0; margin:0; \">" +
                        "<div class=\"view_h1\">" +
                        intent.getStringExtra("title") +
                        "</div>" +
                        "<div class=\"con\">" +
                        intent.getStringExtra("content") +
                        "</div>" +
                        "</body>" +
                        "</html>"

                wv_web.loadDataWithBaseURL(HttpIP.BaseImg, str, "text/html", "utf-8", "")
            }
            "心理测试" -> {
                tvTitle.text = intent.getStringExtra("psychologyTitle")

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
                        intent.getStringExtra("content") +
                        "</div>" +
                        "</body>" +
                        "</html>"

                wv_web.loadDataWithBaseURL(HttpIP.BaseImg, str, "text/html", "utf-8", "")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        wv_web.apply {
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
    }
}
