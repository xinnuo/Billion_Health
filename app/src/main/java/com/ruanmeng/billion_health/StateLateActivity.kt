package com.ruanmeng.billion_health

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CircleModel
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.GlideApp
import com.ruanmeng.share.HttpIP
import com.ruanmeng.view.NineGridLayout
import kotlinx.android.synthetic.main.activity_doctor.*
import kotlinx.android.synthetic.main.layout_empty.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class StateLateActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)
        //最近动态
        init_title(intent.getStringExtra("title"))

        doctor_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无最新动态！"

        doctor_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(1) }
        }

        doctor_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val total = linearLayoutManager.itemCount
                    val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                    // dy > 0 表示向下滑动
                    if (lastVisibleItem >= total - 1 && dy > 0) {
                        if (!isLoadingMore) {
                            isLoadingMore = true
                            getData(pageNum)
                        }
                    }
                }

                /*
                 * Glide图片加载优化，
                 * 只在拖动和静止时加载，自动滑动时不加载
                 */
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_DRAGGING -> GlideApp.with(context).resumeRequests()
                        RecyclerView.SCROLL_STATE_SETTLING -> GlideApp.with(context).pauseRequests()
                        RecyclerView.SCROLL_STATE_IDLE -> GlideApp.with(context).resumeRequests()
                    }
                }
            })

            setOnTouchListener { _, _ -> return@setOnTouchListener doctor_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_state_list) { data, injector ->
                    injector.text(R.id.item_state_name, data.nickName)
                            .text(R.id.item_state_age, (if (data.age == "") "0" else data.age) + "岁")
                            .text(R.id.item_state_title, data.content)
                            .text(R.id.item_state_time, data.createDate)
                            .text(R.id.item_state_read, data.browseNum)
                            .text(R.id.item_state_praise, data.praiseCount)
                            .text(R.id.item_state_comment, data.commentCount)
                            .image(R.id.item_state_gender, if (data.sex == "0") R.mipmap.my_gender_f else R.mipmap.my_gender_m)

                            .visibility(R.id.item_state_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_state_divider3, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_state_nine, if (data.imgs == "") View.GONE else View.VISIBLE)
                            .visibility(R.id.item_state_videofl,
                                    if (data.dynamicVideoPath == "" || data.dynamicVideoImgPath == "") View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_state_img, { view ->
                                GlideApp.with(baseContext)
                                        .load(HttpIP.BaseImg + data.userHead)
                                        .placeholder(R.mipmap.my_tx_mr_1) // 等待时的图片
                                        .error(R.mipmap.my_tx_mr_1)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            })

                            .with<NineGridLayout>(R.id.item_state_nine, { layout ->
                                if (data.imgs != "") {
                                    layout.loadUriList(data.imgs.split(","))
                                    layout.setOnClickImageListener { position, view, _, urlList ->
                                        // 图片点击事件
                                        MNImageBrowser.showImageBrowser(baseContext, view, position, urlList)
                                    }
                                }
                            })

                            .with<ImageView>(R.id.item_state_videoimg, { view ->
                                if (data.dynamicVideoImgPath != "")
                                    Glide.with(baseContext)
                                            .asBitmap()
                                            .load(HttpIP.BaseImg + data.dynamicVideoImgPath)
                                            .into(object : SimpleTarget<Bitmap>() {
                                                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>) {

                                                    data.width = bitmap.width.toString()
                                                    data.height = bitmap.height.toString()

                                                    view.setImageBitmap(bitmap)
                                                }
                                            })
                            })

                            .with<TextView>(R.id.item_state_praise, { view ->
                                @Suppress("DEPRECATION")
                                val drawable = resources.getDrawable(if (data.praise == "1") R.mipmap.dc_icon02_pre else R.mipmap.dc_icon02)
                                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                                view.setCompoundDrawables(drawable, null, null, null)

                                @Suppress("DEPRECATION")
                                view.setTextColor(resources.getColor(if (data.praise == "1") R.color.colorAccent else R.color.light))
                            })

                            .clicked(R.id.item_state_praise, {
                                if (data.praise != "1") {
                                    OkGo.post<String>(HttpIP.dynamic_praise_sub)
                                            .tag(this@StateLateActivity)
                                            .isMultipart(true)
                                            .headers("token", getString("token"))
                                            .params("businessKey", data.dynamicId)
                                            .execute(object : StringDialogCallback(baseContext, false) {
                                                /*{
                                                    "msg": "点赞成功",
                                                    "msgcode": 100
                                                }*/
                                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                    showToask(msg)

                                                    val praiseCount = data.praiseCount.toInt()
                                                    data.praise = "1"
                                                    data.praiseCount = (praiseCount + 1).toString()
                                                    mAdapter.updateData(list).notifyDataSetChanged()
                                                }

                                            })
                                }
                            })

                            .clicked(R.id.item_state_videofl, { v ->
                                // 视频点击事件
                                VideoActivity.startVieoView(
                                        this@StateLateActivity,
                                        v,
                                        data.width,
                                        data.height,
                                        HttpIP.BaseImg + data.dynamicVideoImgPath,
                                        HttpIP.BaseImg + data.dynamicVideoPath)
                            })
                            .clicked(R.id.item_state_ll, {
                                val intent = Intent(baseContext, StateDetailActivity::class.java)
                                intent.putExtra("dynamicId", data.dynamicId)
                                startActivity(intent)
                            })
                }
                .attachTo(doctor_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CircleModel>(HttpIP.orther_dynamic_data)
                .tag(this@StateLateActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("userInfoId", intent.getStringExtra("userInfoId") ?: "")
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CircleModel>(baseContext, CircleModel::class.java) {

                    override fun onSuccess(response: Response<CircleModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            if (response.body().dynamics != null) addAll(response.body().dynamics!!)
                            if (size > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        doctor_refresh.isRefreshing = false
                        isLoadingMore = false

                        ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }
}