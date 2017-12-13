package com.ruanmeng.billion_health

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.GlideApp
import com.ruanmeng.model.TeachData
import com.ruanmeng.model.TeachModel
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.KeyboardHelper
import kotlinx.android.synthetic.main.activity_teach_detail.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class TeachDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var isPraised = "0"
    private var isCollect = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teach_detail)
        init_title("课程主页")

        teach_detail_refresh.isRefreshing = true
        getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        teach_detail_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData() }
        }

        teach_detail_list.apply {
            layoutManager = LinearLayoutManager(baseContext)
            setOnTouchListener { _, _ -> return@setOnTouchListener teach_detail_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.header_teach_detail) { data, injector ->
                    injector.text(R.id.header_teach_read, data.readCount + "人已阅读")
                            .text(R.id.header_teach_zan, data.praiseCount)

                            .with<WebView>(R.id.header_teach_web, { view ->
                                view.apply {
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

                                /*val str = "<meta " +
                                        "name=\"viewport\" " +
                                        ".view_h1{ width:95%; margin:0 auto; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.0em; }\n" +
                                        "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                        "<style>" +
                                        ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                        ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                        "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                        "*{ max-width:100% !important; }\n" +
                                        "</style>"

                                view.loadDataWithBaseURL(
                                        HttpIP.BaseImg,
                                        "$str<div class=\"con\">${data.title}</div><div class=\"con\">${data.content}</div>",
                                        "text/html",
                                        "utf-8",
                                        "")*/

                                val str = "<!doctype html><html>\n" +
                                        "<meta charset=\"utf-8\">" +
                                        "<style type=\"text/css\">" +
                                        "body{ padding:0; margin:0; }\n" +
                                        ".view_h1{ width:90%; margin:0 auto; margin-top:20px; text-align:center; display:block; overflow:hidden;  font-size:1.1em; color:#000; padding:0.5em 0; line-height:1.5em; }\n" +
                                        ".view_time{ width:95%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999; }\n" +
                                        ".con{ width:95%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                                        ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                                        "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                                        "*{ max-width:100% !important; }\n" +
                                        "</style>\n" +
                                        "<body style=\"padding:0; margin:0; \">" +
                                        "<div class=\"view_h1\">" +
                                        data.title +
                                        "</div>" +
                                        "<div class=\"con\">" +
                                        data.content +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                view.loadDataWithBaseURL(HttpIP.BaseImg, str, "text/html", "utf-8", "")
                            })

                            .with<CheckBox>(R.id.header_teach_zan, { view ->
                                view.isChecked = isPraised == "1"
                                teach_detail_zan.setImageResource(if (isPraised == "1") R.mipmap.kc_zan_pre else R.mipmap.kc_zan)

                                view.setOnTouchListener { _, event ->
                                    if (event.action == MotionEvent.ACTION_UP && !view.isChecked) {
                                        OkGo.post<String>(HttpIP.praise_sub)
                                                .tag(this@TeachDetailActivity)
                                                .isMultipart(true)
                                                .headers("token", getString("token"))
                                                .params("courseId", intent.getStringExtra("courseId"))
                                                .execute(object : StringDialogCallback(baseContext, false) {
                                                    /*{
                                                        "msg": "点赞成功",
                                                        "msgcode": 100
                                                    }*/
                                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                        showToask(msg)

                                                        isPraised = "1"
                                                        teach_detail_zan.setImageResource(R.mipmap.kc_zan_pre)

                                                        val praiseCount = data.praiseCount.toInt()
                                                        data.praise = "1"
                                                        data.praiseCount = (praiseCount + 1).toString()
                                                        mAdapter.updateData(list).notifyDataSetChanged()
                                                    }

                                                })
                                    }
                                    return@setOnTouchListener true
                                }
                            })

                            .with<CheckBox>(R.id.header_teach_collect, { view ->
                                view.isChecked = isCollect == "1"

                                view.setOnTouchListener { _, event ->
                                    if (event.action == MotionEvent.ACTION_UP) {
                                        if (!view.isChecked) {
                                            OkGo.post<String>(HttpIP.collect_sub)
                                                    .tag(this@TeachDetailActivity)
                                                    .isMultipart(true)
                                                    .headers("token", getString("token"))
                                                    .params("toCollectId", intent.getStringExtra("courseId"))
                                                    .params("collectType", "collectCourse")
                                                    .execute(object : StringDialogCallback(baseContext) {

                                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                            showToask(msg)
                                                            view.isChecked = true
                                                        }

                                                    })
                                        } else {
                                            OkGo.post<String>(HttpIP.cancel_collect_sub)
                                                    .tag(this@TeachDetailActivity)
                                                    .isMultipart(true)
                                                    .headers("token", getString("token"))
                                                    .params("toCollectId", intent.getStringExtra("courseId"))
                                                    .params("collectType", "collectCourse")
                                                    .execute(object : StringDialogCallback(baseContext) {

                                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                            showToask(msg)
                                                            view.isChecked = false
                                                        }

                                                    })
                                        }
                                    }
                                    return@setOnTouchListener true
                                }
                            })
                }
                .register<TeachData>(R.layout.item_comment_list) { data, injector ->
                    injector.text(R.id.item_comment_name, data.nickName)
                            .text(R.id.item_comment_time, data.createDate)
                            .text(R.id.item_comment_content, data.content)
                            .invisible(R.id.item_comment_ping)

                            .visibility(R.id.item_comment_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_comment_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_comment_img, { view ->
                                GlideApp.with(baseContext)
                                        .load(HttpIP.BaseImg + data.userHead)
                                        .placeholder(R.mipmap.my_tx_mr_1) // 等待时的图片
                                        .error(R.mipmap.my_tx_mr_1)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            })
                }
                .attachTo(teach_detail_list)
    }

    override fun getData() {
        OkGo.post<TeachModel>(HttpIP.course_details_data)
                .tag(this@TeachDetailActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("courseId", intent.getStringExtra("courseId"))
                .execute(object : JacksonDialogCallback<TeachModel>(baseContext, TeachModel::class.java) {

                    override fun onSuccess(response: Response<TeachModel>) {
                        list.apply {
                            clear()
                            add(response.body().courseDetails!!)
                            if (response.body().courseComment != null) addAll(response.body().courseComment!!)
                        }

                        isPraised = response.body().praise
                        isCollect = response.body().collect

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        teach_detail_refresh.isRefreshing = false
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.teach_detail_hint -> {
                showSheetDialog(intent.getStringExtra("courseId"))
            }
            R.id.teach_detail_zan -> {
                if (isPraised != "1") {
                    OkGo.post<String>(HttpIP.praise_sub)
                            .tag(this@TeachDetailActivity)
                            .isMultipart(true)
                            .headers("token", getString("token"))
                            .params("courseId", intent.getStringExtra("courseId"))
                            .execute(object : StringDialogCallback(baseContext, false) {
                                /*{
                                    "msg": "点赞成功",
                                    "msgcode": 100
                                }*/
                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    showToask(msg)

                                    isPraised = "1"
                                    teach_detail_zan.setImageResource(R.mipmap.kc_zan_pre)
                                    val praiseCount = (list[0] as CommonData).praiseCount.toInt()
                                    (list[0] as CommonData).praise = "1"
                                    (list[0] as CommonData).praiseCount = (praiseCount + 1).toString()
                                    mAdapter.updateData(list).notifyDataSetChanged()
                                }

                            })
                }
            }
        }
    }

    private fun showSheetDialog(courseId: String) {
        val dialog = BottomSheetDialog(baseContext)

        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_comment_input, null) as View
        val et_content = view.findViewById<EditText>(R.id.comment_hint) as EditText
        val tv_send = view.findViewById<TextView>(R.id.comment_send) as TextView

        tv_send.setOnClickListener {
            dialog.dismiss()

            OkGo.post<String>(HttpIP.course_comment_sub)
                    .tag(this@TeachDetailActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("content", et_content.text.toString())
                    .params("courseId", courseId)
                    .execute(object : StringDialogCallback(baseContext, false) {
                        /*{
                            "msg": "点赞成功",
                            "msgcode": 100
                        }*/
                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            showToask(msg)

                            if (msgCode == "100") {
                                teach_detail_refresh.isRefreshing = true
                                getData()
                            }
                        }

                    })
        }

        et_content.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }

        dialog.setContentView(view)
        dialog.setOnShowListener({ KeyboardHelper.showSoftInput(this@TeachDetailActivity) })
        dialog.show()
    }
}
