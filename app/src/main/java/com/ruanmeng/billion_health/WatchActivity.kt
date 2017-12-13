package com.ruanmeng.billion_health

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.GlideApp
import com.ruanmeng.model.WatchModel
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.Tools
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_watch.*
import kotlinx.android.synthetic.main.layout_empty.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class WatchActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch)
        init_title("我的关注")

        watch_refresh.isRefreshing = true
        getData(mPosition)
    }

    public override fun onStart() {
        super.onStart()
        watch_tab.post { Tools.setIndicator(watch_tab, 40, 40) }
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无关注的动态！"

        watch_tab.apply {
            addTab(watch_tab.newTab().setText("我关注的"))
            addTab(watch_tab.newTab().setText("关注我的"))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) { }
                override fun onTabUnselected(tab: TabLayout.Tab) { }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position

                    watch_refresh.isRefreshing = true
                    OkGo.getInstance().cancelTag(this@WatchActivity)
                    getData(mPosition)
                }

            })
        }

        watch_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(mPosition) }
        }

        watch_list.apply {
            layoutManager = LinearLayoutManager(this@WatchActivity)

            /*addOnScrollListener(object : RecyclerView.OnScrollListener() {

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
            })*/

            setOnTouchListener { _, _ -> return@setOnTouchListener watch_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_watch_list) { data, injector ->
                    injector.text(R.id.item_watch_name, data.nickName)
                            .text(R.id.item_watch_age, (if (data.age == "") "0" else data.age) + "岁")
                            .text(R.id.item_watch_state, if (data.focusType == "1") "医生" else "好友")
                            .image(R.id.item_watch_gender, if (data.sex == "0") R.mipmap.my_gender_f else R.mipmap.my_gender_m)
                            .background(
                                    R.id.item_watch_state,
                                    if (data.focusType == "1") R.drawable.rec_ova_bg_orange else R.drawable.rec_ova_bg_green)

                            .with<RoundedImageView>(R.id.item_watch_img, { view ->
                                GlideApp.with(this@WatchActivity)
                                        .load(HttpIP.BaseImg + data.userHead)
                                        .placeholder(R.mipmap.my_tx_mr) // 等待时的图片
                                        .error(R.mipmap.my_tx_mr)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            })

                            .visibility(R.id.item_watch_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_watch_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_watch_ll, {
                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        data.userInfoId,
                                        data.nickName,
                                        Uri.parse(HttpIP.BaseImg + data.userHead)))

                                RongIM.getInstance().startPrivateChat(baseContext, data.userInfoId, data.nickName)
                            })

                            .clicked(R.id.item_watch_notice, {
                                OkGo.post<String>(HttpIP.cancel_focue_sub)
                                        .tag(this@WatchActivity)
                                        .isMultipart(true)
                                        .headers("token", getString("token"))
                                        .params("toUserId", data.userInfoId)
                                        .execute(object : StringDialogCallback(baseContext) {
                                            /*{
                                                "msg": "取消关注成功",
                                                "msgcode": 100
                                            }*/
                                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                showToask(msg)

                                                list.remove(data)
                                                mAdapter.updateData(list).notifyDataSetChanged()
                                                ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                                            }

                                        })
                            })
                }
                .attachTo(watch_list)
    }

    override fun getData(type: Int) {
        OkGo.post<WatchModel>(if (type == 0) HttpIP.me_focue_list else HttpIP.focue_me_list)
                .tag(this@WatchActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<WatchModel>(this@WatchActivity, WatchModel::class.java) {

                    override fun onSuccess(response: Response<WatchModel>) {
                        list.apply {
                            clear()
                            if (response.body().focue != null)
                                addAll(response.body().focue!!)
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        watch_refresh.isRefreshing = false

                        ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }
}
