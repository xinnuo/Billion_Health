package com.ruanmeng.billion_health

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.navi.BaiduMapNavigation
import com.baidu.mapapi.navi.NaviParaOption
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.http.addItems
import com.ruanmeng.model.*
import com.ruanmeng.share.HttpIP
import com.ruanmeng.view.FullyGridLayoutManager
import kotlinx.android.synthetic.main.activity_hospital_detail.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class HospitalDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_range = ArrayList<Any>()
    private var item: HospitalData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_detail)
        init_title(intent.getStringExtra("title"))

        EventBus.getDefault().register(this@HospitalDetailActivity)

        getData()
        getZixunData()
        getDoctorData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        hospital_detail_info.apply {
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        hospital_detail_doctor.apply {
            layoutManager = FullyGridLayoutManager(this@HospitalDetailActivity, 3, GridLayoutManager.VERTICAL, false)
            adapter = SlimAdapter.create()
                    .register<DoctorData>(R.layout.item_doctor_find_grid) { data, injector ->
                        injector.text(R.id.item_doctor_name, data.doctorName)
                                .text(R.id.item_doctor_job, data.credentialName)
                                .with<ImageView>(R.id.item_doctor_img, { view ->
                                    GlideApp.with(baseContext)
                                            .load(HttpIP.BaseImg + data.doctorHead)
                                            .placeholder(R.mipmap.not_3) // 等待时的图片
                                            .error(R.mipmap.not_3)       // 加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                })
                                .clicked(R.id.item_doctor_img, {
                                    val intent = Intent(baseContext, DoctorDetailActivity::class.java)
                                    intent.putExtra("doctorId", data.doctorId)
                                    startActivity(intent)
                                })
                    }
                    .attachTo(hospital_detail_doctor)
        }

        hospital_detail_range.apply {
            layoutManager = FullyGridLayoutManager(this@HospitalDetailActivity, 4, GridLayoutManager.VERTICAL, false)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_hospital_range_grid) { data, injector ->
                        injector.text(R.id.item_hospital_name, data.diseaseName)

                                .with<ImageView>(R.id.item_hospital_img, { view ->
                                    when (data.diseaseName) {
                                        "儿童心理咨询" -> view.setImageResource(R.mipmap.home_icon01)
                                        "青少年心理咨询" -> view.setImageResource(R.mipmap.home_icon02)
                                        "成人心理咨询" -> view.setImageResource(R.mipmap.home_icon03)
                                        "健康营养咨询" -> view.setImageResource(R.mipmap.home_icon04)
                                    }
                                })
                    }
                    .attachTo(hospital_detail_range)
        }

        hospital_collect.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return@setOnTouchListener true
                } else {
                    if (!hospital_collect.isChecked) {
                        OkGo.post<String>(HttpIP.collect_sub)
                                .tag(this@HospitalDetailActivity)
                                .isMultipart(true)
                                .headers("token", getString("token"))
                                .params("toCollectId", intent.getStringExtra("hospitalId"))
                                .params("collectType", "collectHospital")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        showToask(msg)
                                        hospital_collect.isChecked = true
                                    }

                                })
                    } else {
                        OkGo.post<String>(HttpIP.cancel_collect_sub)
                                .tag(this@HospitalDetailActivity)
                                .isMultipart(true)
                                .headers("token", getString("token"))
                                .params("toCollectId", intent.getStringExtra("hospitalId"))
                                .params("collectType", "collectHospital")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        showToask(msg)
                                        hospital_collect.isChecked = false
                                    }

                                })
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun getData() {
        OkGo.post<HospitalModel>(HttpIP.hospital_details)
                .tag(this@HospitalDetailActivity)
                .isMultipart(true)
                .params("hospitalId", intent.getStringExtra("hospitalId"))
                .params("userInfoId", getString("token"))
                .execute(object : JacksonDialogCallback<HospitalModel>(
                        this@HospitalDetailActivity,
                        HospitalModel::class.java,
                        true) {

                    override fun onSuccess(response: Response<HospitalModel>) {
                        item = response.body().hospitalDetails
                        if (item != null) {
                            val mLoopAdapter = LoopAdapter(baseContext, hospital_detail_banner)
                            hospital_detail_banner.apply {
                                setAdapter(mLoopAdapter)
                                setOnItemClickListener { position ->
                                    showToask("图片详情 position：$position")
                                }
                            }
                            if (item!!.hospitalImgs != "") mLoopAdapter.setImgs(item!!.hospitalImgs.split(","))

                            hospital_detail_name.text = item!!.hospitalName
                            hospital_detail_addr.text = "地址：" + item!!.hospitalAddress

                            val str = "<meta " +
                                    "name=\"viewport\" " +
                                    "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                    "<style>" +
                                    ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                    "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                    "*{ max-width:100% !important; }\n" +
                                    "</style>"
                            val info = item!!.content
                            hospital_detail_info.loadDataWithBaseURL(HttpIP.BaseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")
                        }

                        if (response.body().collect == "1")
                            hospital_collect.isChecked = true
                    }
                })
    }

    private fun getZixunData() {
        OkGo.post<HospitalModel>(HttpIP.hospital_consultancyScope_data)
                .tag(this@HospitalDetailActivity)
                .isMultipart(true)
                .params("hospitalId", intent.getStringExtra("hospitalId"))
                .params("level", 1)
                .execute(object : JacksonDialogCallback<HospitalModel>(
                        this@HospitalDetailActivity,
                        HospitalModel::class.java) {

                    override fun onSuccess(response: Response<HospitalModel>) {
                        list_range.addItems(response.body().hospitalConsultancyScope)
                        (hospital_detail_range.adapter as SlimAdapter).updateData(list_range).notifyDataSetChanged()
                    }
                })
    }

    private fun getDoctorData() {
        OkGo.post<HospitalModel>(HttpIP.hospital_recommendDoctor_data)
                .tag(this@HospitalDetailActivity)
                .isMultipart(true)
                .params("hospitalId", intent.getStringExtra("hospitalId"))
                .params("rows", 4)
                .params("page", 1)
                .execute(object : JacksonDialogCallback<HospitalModel>(
                        this@HospitalDetailActivity,
                        HospitalModel::class.java) {

                    override fun onSuccess(response: Response<HospitalModel>) {
                        if (response.body().hospitalRecommendDoctors != null) {
                            list.addAll(response.body().hospitalRecommendDoctors!!)
                            (hospital_detail_doctor.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                        }
                    }
                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.hospital_detail_daohang -> {
                if (item != null && item!!.lat != "") {
                    val pt1 = LatLng(getString("lat").toDouble(), getString("lng").toDouble())
                    val pt2 = LatLng(item!!.lat.toDouble(), item!!.lng.toDouble())

                    // 构建 导航参数
                    val para = NaviParaOption()
                            .startPoint(pt1)
                            .endPoint(pt2)
                            .startName(getString("city"))
                            .endName(item!!.hospitalName)

                    BaiduMapNavigation.openBaiduMapNavi(para, this@HospitalDetailActivity)
                }
            }
            R.id.hospital_detail_tel -> {
                if (item != null && item!!.mobile != "") {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item!!.mobile))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else showToask("暂无联系方式！")
            }
            R.id.hospital_detail_more -> {
                intent.setClass(baseContext, DoctorMoreActivity::class.java)
                startActivity(intent)
            }
            R.id.hospital_yuyue -> {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return
                }

                intent.setClass(baseContext, BookActivity::class.java)
                intent.putExtra("name", item!!.hospitalName)
                startActivity(intent)
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
        EventBus.getDefault().unregister(this@HospitalDetailActivity)
        super.onBackPressed()
    }
}
