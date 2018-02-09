package com.ruanmeng.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.billion_health.*
import com.ruanmeng.model.CircleModel
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.GlideApp
import com.ruanmeng.model.MainSecondEvent
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.Tools
import com.ruanmeng.view.NineGridLayout
import kotlinx.android.synthetic.main.fragment_main_second.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_title_second.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class MainSecondFragment : BaseFragment() {

    private val list = ArrayList<Any>()

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_second, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        EventBus.getDefault().register(this@MainSecondFragment)

        second_refresh.isRefreshing = true
        mPosition = 1
        getData(mPosition)
    }

    override fun onStart() {
        super.onStart()
        second_tab.post { Tools.setIndicator(second_tab, 30, 30) }
    }

    override fun init_title() {
        empty_hint.text = "暂无最新动态！"

        second_tab.apply {
            addTab(second_tab.newTab().setText("广场"))
            addTab(second_tab.newTab().setText("好友"))
            addTab(second_tab.newTab().setText("我的"))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position + 1

                    second_refresh.isRefreshing = true
                    OkGo.getInstance().cancelTag(this@MainSecondFragment)
                    getData(mPosition)
                }

            })
        }

        second_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(mPosition) }
        }

        second_list.apply {
            layoutManager = LinearLayoutManager(this@MainSecondFragment.activity)
            setOnTouchListener { _, _ -> return@setOnTouchListener second_refresh.isRefreshing }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        }

        iv_nav_right.setOnClickListener {
            DialogHelper.showTypeDialog(activity, arrayOf("日志", "说说", "相册")) { type ->
                val intent = Intent(activity, IssueActivity::class.java)
                intent.putExtra("dynamicType", type)
                startActivity(intent)
            }
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
                            .visibility(R.id.item_state_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_state_nine, if (data.imgs == "") View.GONE else View.VISIBLE)
                            .visibility(R.id.item_state_videofl,
                                    if (data.dynamicVideoPath == "" || data.dynamicVideoImgPath == "") View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_state_img, { view ->
                                GlideApp.with(this@MainSecondFragment)
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
                                        val imgs = ArrayList<Any>()
                                        imgs.addAll(urlList)
                                        MNImageBrowser.showImageBrowser(activity, view, position, imgs)
                                    }
                                }
                            })

                            .with<ImageView>(R.id.item_state_videoimg, { view ->
                                if (data.dynamicVideoImgPath != "")
                                    Glide.with(this@MainSecondFragment)
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
                                            .tag(this@MainSecondFragment)
                                            .isMultipart(true)
                                            .headers("token", getString("token"))
                                            .params("businessKey", data.dynamicId)
                                            .execute(object : StringDialogCallback(activity, false) {
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
                                        activity,
                                        v,
                                        data.width,
                                        data.height,
                                        HttpIP.BaseImg + data.dynamicVideoImgPath,
                                        HttpIP.BaseImg + data.dynamicVideoPath)
                            })
                            .clicked(R.id.item_state_img, {
                                val intent = Intent(activity, HomeActivity::class.java)
                                intent.putExtra("userInfoId", data.userInfoId)
                                startActivity(intent)
                            })
                            .clicked(R.id.item_state_ll, {
                                val intent = Intent(activity, StateDetailActivity::class.java)
                                intent.putExtra("dynamicId", data.dynamicId)
                                startActivity(intent)
                            })
                }
                .attachTo(second_list)
    }

    override fun getData(position: Int) {
        OkGo.post<CircleModel>(HttpIP.friendCircle_data)
                .tag(this@MainSecondFragment)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("dynamicRole", position)
                .execute(object : JacksonDialogCallback<CircleModel>(this@MainSecondFragment.activity, CircleModel::class.java) {

                    override fun onSuccess(response: Response<CircleModel>) {
                        list.apply {
                            clear()
                            if (response.body().friendCircle != null)
                                addAll(response.body().friendCircle!!)
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        second_refresh.isRefreshing = false

                        ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    @Subscribe
    fun onMessageEvent(event: MainSecondEvent) {
        if (event.name == "朋友圈") {
            second_refresh.isRefreshing = true
            getData(mPosition)
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this@MainSecondFragment)
        super.onDestroy()
    }
}
