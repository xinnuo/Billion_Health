package com.ruanmeng.billion_health

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
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
import com.ruanmeng.model.CommentData
import com.ruanmeng.model.CommentModel
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.GlideApp
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.DensityUtil
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.view.NineGridLayout
import kotlinx.android.synthetic.main.activity_state_detail.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class StateDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_detail)
        transparentStatusBar(false)
        init_title()

        state_detail_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        super.init_title()
        state_detail_refresh.apply {
            setProgressViewOffset(false, DensityUtil.dp2px(10f), DensityUtil.dp2px(80f))
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData() }
        }

        state_detail_list.apply {
            layoutManager = LinearLayoutManager(baseContext)
            setOnTouchListener { _, _ -> return@setOnTouchListener state_detail_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.header_state_detail) { data, injector ->
                    injector.text(R.id.item_state_name, data.nickName)
                            .text(R.id.item_state_age, (if (data.age == "") "0" else data.age) + "岁")
                            .image(R.id.item_state_gender, if (data.sex == "0") R.mipmap.my_gender_f else R.mipmap.my_gender_m)
                            .text(R.id.header_state_title, data.content)
                            .text(R.id.header_state_time, data.createDate)
                            .text(R.id.header_state_comment, "评论(" + (list.size - 1).toString() + ")")
                            .text(R.id.header_state_praise, data.praiseCount)

                            .visibility(R.id.header_state_nine, if (data.imgs == "") View.GONE else View.VISIBLE)
                            .visibility(R.id.header_state_videofl,
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

                            .with<NineGridLayout>(R.id.header_state_nine, { layout ->
                                if (data.imgs.isNotEmpty()) {
                                    layout.visibility = View.VISIBLE
                                    layout.loadUriList(data.imgs.split(","))
                                    layout.setOnClickImageListener { position, view, _, urlList ->
                                        // 图片点击事件
                                        MNImageBrowser.showImageBrowser(baseContext, view, position, urlList)
                                    }
                                } else layout.visibility = View.GONE
                            })

                            .with<ImageView>(R.id.header_state_videoimg, { view ->
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

                            .with<TextView>(R.id.header_state_praise, { view ->
                                comment_submit.setImageResource(if (data.praise == "1") R.mipmap.pyq_send else R.mipmap.kc_zan)

                                val drawable = resources.getDrawable(if (data.praise == "1") R.mipmap.dc_icon02_pre else R.mipmap.dc_icon02)
                                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                                view.setCompoundDrawables(drawable, null, null, null)

                                view.setTextColor(resources.getColor(if (data.praise == "1") R.color.colorAccent else R.color.light))
                            })

                            .clicked(R.id.header_state_videofl, { v ->
                                // 视频点击事件
                                VideoActivity.startVieoView(
                                        this@StateDetailActivity,
                                        v,
                                        data.width,
                                        data.height,
                                        HttpIP.BaseImg + data.dynamicVideoImgPath,
                                        HttpIP.BaseImg + data.dynamicVideoPath)
                            })

                            .clicked(R.id.item_state_img, {
                                val intent = Intent(baseContext, HomeActivity::class.java)
                                intent.putExtra("userInfoId", data.userInfoId)
                                startActivity(intent)
                            })

                            .clicked(R.id.header_state_praise, {
                                if (data.praise != "1") {
                                    OkGo.post<String>(HttpIP.dynamic_praise_sub)
                                            .tag(this@StateDetailActivity)
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
                }
                .register<CommentData>(R.layout.item_comment_list) { data, injector ->
                    injector.text(R.id.item_comment_time, data.createDate)
                    injector.text(R.id.item_comment_content, data.commentContent)

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

                            .with<TextView>(R.id.item_comment_name, { view ->
                                if (data.replyUserId == data.userInfoId) view.text = data.nikeName
                                else {
                                    val str_item = data.nikeName + " 回复 " + data.replyNikeName
                                    val spanText = SpannableString(str_item)
                                    spanText.setSpan(object : ClickableSpan() {

                                        override fun onClick(widget: View) {}

                                        override fun updateDrawState(ds: TextPaint) {
                                            super.updateDrawState(ds)
                                            ds.color = resources.getColor(R.color.colorAccent) // 设置文件颜色
                                            ds.isUnderlineText = false // 设置下划线
                                        }

                                    }, str_item.length - data.replyNikeName.length, str_item.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    view.text = spanText
                                }
                            })

                            .clicked(R.id.item_comment_img, {
                                val intent = Intent(baseContext, HomeActivity::class.java)
                                intent.putExtra("userInfoId", data.userInfoId)
                                startActivity(intent)
                            })

                            .clicked(R.id.item_comment_ping, { showSheetDialog(data.userInfoId) })
                }
                .attachTo(state_detail_list)
    }

    override fun getData() {
        OkGo.post<CommentModel>(HttpIP.get_dynamic)
                .tag(this@StateDetailActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("dynamicId", intent.getStringExtra("dynamicId"))
                .execute(object : JacksonDialogCallback<CommentModel>(baseContext, CommentModel::class.java) {

                    override fun onSuccess(response: Response<CommentModel>) {
                        list.apply {
                            clear()
                            add(response.body().dynamic!!)
                            if (response.body().comment != null) addAll(response.body().comment!!)
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        state_detail_refresh.isRefreshing = false
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.comment_hint -> {
                showSheetDialog(getString("token"))
            }
            R.id.comment_submit -> {
                if (list.size > 0 && (list[0] as CommonData).praise != "1") {
                    OkGo.post<String>(HttpIP.dynamic_praise_sub)
                            .tag(this@StateDetailActivity)
                            .isMultipart(true)
                            .headers("token", getString("token"))
                            .params("businessKey", intent.getStringExtra("dynamicId"))
                            .execute(object : StringDialogCallback(baseContext, false) {
                                /*{
                                    "msg": "点赞成功",
                                    "msgcode": 100
                                }*/
                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    showToask(msg)

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

    private fun showSheetDialog(userId: String) {
        val dialog = BottomSheetDialog(baseContext)

        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_comment_input, null) as View
        val et_content = view.findViewById<EditText>(R.id.comment_hint) as EditText
        val tv_send = view.findViewById<TextView>(R.id.comment_send) as TextView

        tv_send.setOnClickListener {
            dialog.dismiss()

            OkGo.post<String>(HttpIP.dynamiccomment_sub)
                    .tag(this@StateDetailActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("dynamicId", intent.getStringExtra("dynamicId"))
                    .params("commentContent", et_content.text.toString())
                    .params("replyUserId", userId)
                    .execute(object : StringDialogCallback(baseContext, false) {
                        /*{
                            "msg": "点赞成功",
                            "msgcode": 100
                        }*/
                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            showToask(msg)

                            if (msgCode == "100") {
                                state_detail_refresh.isRefreshing = true
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
        dialog.setOnShowListener({ KeyboardHelper.showSoftInput(this@StateDetailActivity) })
        dialog.show()
    }
}
