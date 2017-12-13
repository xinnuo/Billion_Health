package com.ruanmeng.billion_health

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MotionEvent
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.GlideApp
import com.ruanmeng.model.HomeModel
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.MultiGapDecoration
import com.ruanmeng.view.FullyGridLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import net.idik.lib.slimadapter.SlimAdapter
import kotlin.collections.ArrayList

class HomeActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        transparentStatusBar(false)
        init_title()

        getData()
    }

    override fun init_title() {
        super.init_title()
        if (getString("token") == intent.getStringExtra("userInfoId")) home_check.visibility = View.INVISIBLE

        home_list.apply {
            layoutManager = FullyGridLayoutManager(this@HomeActivity, 4, GridLayoutManager.VERTICAL, false)
            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_roundimg_grid) { data, injector ->
                        injector.with<RoundedImageView>(R.id.item_img, { view ->
                            GlideApp.with(baseContext)
                                    .load(HttpIP.BaseImg + data.img)
                                    .placeholder(R.mipmap.not_1) // 等待时的图片
                                    .error(R.mipmap.not_1)       // 加载失败的图片
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(view)
                        })

                                .clicked(R.id.item_img, { v ->
                                    // 图片点击事件
                                    val imgs = ArrayList<String>()
                                    list.mapTo(imgs) { HttpIP.BaseImg + (it as CommonData).img }

                                    MNImageBrowser.showImageBrowser(baseContext, v, list.indexOf(data), imgs)
                                })
                    }
                    .attachTo(home_list)
        }

        home_check.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!home_check.isChecked) {
                    OkGo.post<String>(HttpIP.focue_sub)
                            .tag(this@HomeActivity)
                            .isMultipart(true)
                            .headers("token", getString("token"))
                            .params("toUserId", intent.getStringExtra("userInfoId"))
                            .params("focusType", "2")
                            .execute(object : StringDialogCallback(baseContext) {

                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    showToask(msg)
                                    home_check.isChecked = true
                                }

                            })
                } else {
                    OkGo.post<String>(HttpIP.cancel_focue_sub)
                            .tag(this@HomeActivity)
                            .isMultipart(true)
                            .headers("token", getString("token"))
                            .params("toUserId", intent.getStringExtra("userInfoId"))
                            .execute(object : StringDialogCallback(baseContext) {

                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    showToask(msg)
                                    home_check.isChecked = false
                                }

                            })
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun getData() {
        OkGo.post<HomeModel>(HttpIP.user_homepage_msg)
                .tag(this@HomeActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("userInfoId", intent.getStringExtra("userInfoId"))
                .execute(object : JacksonDialogCallback<HomeModel>(baseContext, HomeModel::class.java, true) {

                    override fun onSuccess(response: Response<HomeModel>) {
                        if (response.body().homepageMsg != null) {
                            val data = response.body().homepageMsg

                            home_teach.text = response.body().courseNum
                            home_watching.text = response.body().focueMeNum + "人"
                            home_watched.text = response.body().meFocueNum + "人"

                            if (response.body().focus == "1") home_check.isChecked = true

                            userName = data!!.nickName
                            home_name.text = data.nickName
                            home_age.text = (if (data.age == "") "0" else data.age) + "岁"
                            home_city.text = data.city
                            home_qu.text = data.district
                            home_sign.text = data.sign

                            home_gender.setImageResource(if (data.sex == "0") R.mipmap.my_gender_f else R.mipmap.my_gender_m)
                            home_city.visibility = if (data.city == "") View.INVISIBLE else View.VISIBLE
                            home_qu.visibility = if (data.district == "") View.INVISIBLE else View.VISIBLE

                            GlideApp.with(baseContext)
                                    .load(HttpIP.BaseImg + data.userhead)
                                    .placeholder(R.mipmap.my_tx_mr_1) // 等待时的图片
                                    .error(R.mipmap.my_tx_mr_1)       // 加载失败的图片
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(home_img)
                        }

                        if (response.body().imgs != null) list.addAll(response.body().imgs!!)
                        (home_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.home_state -> {
                if (getString("token") == intent.getStringExtra("userInfoId")) {
                    intent.setClass(baseContext, StateActivity::class.java)
                    intent.putExtra("title", "我的动态")
                    startActivity(intent)
                } else {
                    intent.setClass(baseContext, StateLateActivity::class.java)
                    intent.putExtra("title", userName + "的动态")
                    intent.putExtra("isOther", true)
                    startActivity(intent)
                }
            }
        }
    }
}
