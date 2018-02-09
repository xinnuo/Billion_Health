package com.ruanmeng.billion_health

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.http.addItems
import com.ruanmeng.model.*
import com.ruanmeng.share.HttpIP
import com.ruanmeng.view.FullyGridLayoutManager
import com.ruanmeng.view.FullyLinearLayoutManager
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_doctor_detail.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class DoctorDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_msg = ArrayList<Any>()

    private var doctorName = ""
    private var doctorHead = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_detail)
        setToolbarVisibility(false)
        init_title()

        EventBus.getDefault().register(this@DoctorDetailActivity)

        getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        doctor_detail_info.apply {
            settings.javaScriptEnabled = true
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        doctor_detail_feng.apply {
            layoutManager = FullyGridLayoutManager(this@DoctorDetailActivity, 4, GridLayoutManager.VERTICAL, false)
            adapter = SlimAdapter.create()
                    .register<DoctorData>(R.layout.item_img_grid) { data, injector ->
                        injector.with<ImageView>(R.id.item_img, { view ->
                            GlideApp.with(this@DoctorDetailActivity)
                                    .load(HttpIP.BaseImg + data.realPath)
                                    .placeholder(R.mipmap.not_1) // 等待时的图片
                                    .error(R.mipmap.not_1)       // 加载失败的图片
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(view)
                        })
                                .clicked(R.id.item_img, { v ->
                                    // 图片点击事件
                                    val imgs = ArrayList<Any>()
                                    list.mapTo(imgs) { HttpIP.BaseImg + (it as DoctorData).realPath }

                                    MNImageBrowser.showImageBrowser(baseContext, v, list.indexOf(data), imgs)
                                })
                    }
                    .attachTo(doctor_detail_feng)
        }

        doctor_detail_teach.apply {
            layoutManager = FullyLinearLayoutManager(this@DoctorDetailActivity)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_myteach_list) { data, injector ->
                        injector.text(R.id.item_myteach_title, data.title)
                                .text(R.id.item_myteach_time, data.createDate)
                                .invisible(R.id.item_myteach_read)
                                .invisible(R.id.item_myteach_zan)
                                .invisible(R.id.item_myteach_ping)

                                .visibility(
                                        R.id.item_myteach_divider1,
                                        if (list_msg.indexOf(data) == list_msg.size - 1) View.GONE else View.VISIBLE)

                                .clicked(R.id.item_myteach_ll, {
                                    val intent = Intent(baseContext, WebActivity::class.java)
                                    intent.putExtra("name", "课程通知")
                                    intent.putExtra("title", data.title)
                                    intent.putExtra("content", data.content)
                                    startActivity(intent)
                                })
                    }
                    .attachTo(doctor_detail_teach)
        }

        doctor_detail_watch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return@setOnTouchListener true
                } else {
                    if (!doctor_detail_watch.isChecked) {
                        OkGo.post<String>(HttpIP.focue_sub)
                                .tag(this@DoctorDetailActivity)
                                .isMultipart(true)
                                .headers("token", getString("token"))
                                .params("toUserId", intent.getStringExtra("doctorId"))
                                .params("focusType", "1")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        showToask(msg)
                                        doctor_detail_watch.isChecked = true
                                    }

                                })
                    } else {
                        OkGo.post<String>(HttpIP.cancel_focue_sub)
                                .tag(this@DoctorDetailActivity)
                                .isMultipart(true)
                                .headers("token", getString("token"))
                                .params("toUserId", intent.getStringExtra("doctorId"))
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        showToask(msg)
                                        doctor_detail_watch.isChecked = false
                                    }

                                })
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun getData() {
        OkGo.post<DoctorModel>(HttpIP.get_doctor_data)
                .tag(this@DoctorDetailActivity)
                .isMultipart(true)
                .params("doctorId", intent.getStringExtra("doctorId"))
                .params("userInfoId", getString("token"))
                .execute(object : JacksonDialogCallback<DoctorModel>(
                        this@DoctorDetailActivity,
                        DoctorModel::class.java,
                        true) {

                    override fun onSuccess(response: Response<DoctorModel>) {
                        val data = response.body().doctorData
                        doctorName = data!!.doctorName
                        doctorHead = data.doctorHead
                        doctor_detail_name.text = data.doctorName
                        doctor_detail_zi.text = data.credentialName
                        doctor_detail_shan.text = data.doctorAdept
                        doctor_detail_hospital.text = data.hospitalName

                        GlideApp.with(baseContext)
                                .load(HttpIP.BaseImg + data.doctorHead)
                                .placeholder(R.mipmap.my_tx_mr_1)
                                .error(R.mipmap.my_tx_mr_1)
                                .dontAnimate()
                                .into(doctor_detail_img)

                        val str = "<meta " +
                                "name=\"viewport\" " +
                                "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                "<style>" +
                                ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                "*{ max-width:100% !important; }\n" +
                                "</style>"
                        // val info = "以往的开发工作之中，少不了要跟各种异常作斗争，常见的异常种类包括空指针异常NullPointerException、数组越界异常IndexOutOfBoundsException、类型转换异常ClassCastException等等，其中最让人头痛的当数空指针异常，该异常频繁发生却又隐藏很深。调用一个空对象的方法，就会产生空指针异常，可是Java编码的时候编译器不会报错，开发者通常也意识不到问题，只有App运行之时发生闪退，查看崩溃日志才会恍然大悟“原来这里得加上对象非空的判断”。然而，饶是有经验的开发者，尚且摆脱不了如影随形的空指针，何况编程新手呢？问题的症结在于，Java编译器不会检查空值，只能由开发者在代码中增加“if (*** != null)”的判断，但是业务代码里面的方法调用浩如繁星，倘若在每个方法调用之前都加上非空判断，势必大量代码都充满了“if (*** != null)”，这样做的后果不仅降低了代码的可读性，而且给开发者带来不少的额外工作量"
                        val info = data.content
                        doctor_detail_info.loadDataWithBaseURL(HttpIP.BaseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")

                        if (response.body().focus == "1")
                            doctor_detail_watch.isChecked = true

                        if (response.body().doctorMsg != null) {
                            list_msg.apply {
                                clear()
                                addItems(response.body().doctorMsg)
                            }
                            (doctor_detail_teach.adapter as SlimAdapter).updateData(list_msg).notifyDataSetChanged()
                        }

                        if (response.body().doctorMien != null) {
                            list.apply {
                                clear()
                                addItems(response.body().doctorMien)
                            }
                            (doctor_detail_feng.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                        }
                    }
                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.doctor_detail_zixun -> {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return
                }

                OkGo.post<String>(HttpIP.doctor_consult)
                        .tag(this@DoctorDetailActivity)
                        .params("doctorId", intent.getStringExtra("doctorId"))
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        intent.getStringExtra("doctorId"),
                                        doctorName,
                                        Uri.parse(HttpIP.BaseImg + doctorHead)))

                                RongIM.getInstance().startPrivateChat(
                                        baseContext,
                                        intent.getStringExtra("doctorId"),
                                        doctorName)
                            }

                        })
            }
        }
    }

    @Subscribe
    fun onMessageEvent(event: LoginMessageEvent) {
        if (event.name == "登录成功") {
            getData()
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().unregister(this@DoctorDetailActivity)
        super.onBackPressed()
    }
}
