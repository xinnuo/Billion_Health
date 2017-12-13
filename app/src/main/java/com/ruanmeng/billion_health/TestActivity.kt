package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.GlideApp
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.HtmlUtil
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.layout_empty.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class TestActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        init_title("心理测试")

        test_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无心理测试信息！"

        test_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(1) }
        }

        test_list.apply {
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
            })

            setOnTouchListener { _, _ -> return@setOnTouchListener test_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_teach_list) { data, injector ->
                    injector.text(R.id.item_teach_title, data.psychologyTitle)
                            .text(R.id.item_teach_read, data.readCount + "人已阅读")

                            .visibility(R.id.item_teach_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_teach_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<TextView>(R.id.item_teach_content, { view ->
                                Thread {
                                    val str = HtmlUtil.delHTMLTag(data.content) //耗时操作
                                    runOnUiThread { view.text = str }
                                }.start()
                            })

                            .with<ImageView>(R.id.item_teach_img, { view ->
                                GlideApp.with(baseContext)
                                        .load(HttpIP.BaseImg + data.psychologyHead)
                                        .placeholder(R.mipmap.not_2) // 等待时的图片
                                        .error(R.mipmap.not_2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            })

                            .clicked(R.id.item_teach_11, {
                                val intent = Intent(baseContext, WebActivity::class.java)
                                intent.putExtra("name", "心理测试")
                                intent.putExtra("psychologyTitle", data.psychologyTitle)
                                intent.putExtra("content", data.content)
                                startActivity(intent)
                            })
                }
                .attachTo(test_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(HttpIP.psychology_data)
                .tag(this@TestActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            if (response.body().psychologys != null) addAll(response.body().psychologys!!)
                            if (size > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        test_refresh.isRefreshing = false
                        isLoadingMore = false

                        ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }
}
