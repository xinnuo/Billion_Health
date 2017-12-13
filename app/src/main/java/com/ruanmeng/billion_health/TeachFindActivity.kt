package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jude.rollviewpager.RollPagerView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.SliderData
import com.ruanmeng.model.TeachModel
import com.ruanmeng.share.HttpIP
import kotlinx.android.synthetic.main.activity_teach_find.*
import kotlinx.android.synthetic.main.layout_title_search_hint.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class TeachFindActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_slider = ArrayList<SliderData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teach_find)
        setToolbarVisibility(false)
        init_title()

        teach_find_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        search_hint_txt.text = "请输入课程名称"

        teach_find_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(1) }
        }

        teach_find_list.apply {
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

            setOnTouchListener { _, _ -> return@setOnTouchListener teach_find_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.header_teach_find) { _, injector ->
                    injector.with<RollPagerView>(R.id.first_banner, { view ->
                        val mLoopAdapter = LoopAdapter(this@TeachFindActivity, view)
                        view.apply {
                            setAdapter(mLoopAdapter)
                            setOnItemClickListener { position -> }
                        }

                        val imgs = ArrayList<String>()
                        list_slider.mapTo(imgs) { it.sliderImg }

                        mLoopAdapter.setImgs(imgs)
                    })
                }
                .register<CommonData>(R.layout.item_myteach_list) { data, injector ->
                    injector.text(R.id.item_myteach_title, data.title)
                            .text(R.id.item_myteach_time, data.createDate)
                            .text(R.id.item_myteach_read, data.readCount)
                            .text(R.id.item_myteach_zan, data.praiseCount)
                            .text(R.id.item_myteach_ping, data.commentCount)

                            .visibility(R.id.item_myteach_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_myteach_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_myteach_ll, {
                                if (!getBoolean("isLogin")) {
                                    startActivity(LoginActivity::class.java)
                                    return@clicked
                                }
                                val intent = Intent(baseContext, TeachDetailActivity::class.java)
                                intent.putExtra("courseId", data.courseId)
                                startActivity(intent)
                            })
                }
                .attachTo(teach_find_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<TeachModel>(HttpIP.search_course_data)
                .tag(this@TeachFindActivity)
                .isMultipart(true)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<TeachModel>(baseContext, TeachModel::class.java) {

                    override fun onSuccess(response: Response<TeachModel>) {
                        list_slider.apply {
                            clear()
                            if (response.body().sliderCourses != null) list_slider.addAll(response.body().sliderCourses!!)
                        }

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                add("轮播图")
                                pageNum = pindex
                            }
                            if (response.body().courses != null) addAll(response.body().courses!!)
                            if (size > 1) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        teach_find_refresh.isRefreshing = false
                        isLoadingMore = false
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_hint -> {
                startActivity(SearchActivity::class.java)
            }
        }
    }
}
