package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.DoctorData
import com.ruanmeng.model.GlideApp
import com.ruanmeng.model.HospitalModel
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.MultiGapDecoration
import kotlinx.android.synthetic.main.activity_doctor.*
import kotlinx.android.synthetic.main.layout_empty.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class DoctorMoreActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)
        init_title("全部医生")

        doctor_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无医生信息！"

        doctor_refresh.apply {
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(1) }
        }

        doctor_list.apply {
            gridLayoutManager = GridLayoutManager(baseContext, 3)
            layoutManager = gridLayoutManager

            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val total = gridLayoutManager.itemCount
                    val lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition()
                    //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                    // dy > 0 表示向下滑动
                    if (lastVisibleItem >= total - 4 && dy > 0) {
                        if (!isLoadingMore) {
                            isLoadingMore = true
                            getData(pageNum)
                        }
                    }
                }
            })

            setOnTouchListener { _, _ -> return@setOnTouchListener doctor_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
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
                .attachTo(doctor_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<HospitalModel>(HttpIP.hospital_recommendDoctor_data)
                .tag(this@DoctorMoreActivity)
                .isMultipart(true)
                .params("hospitalId", intent.getStringExtra("hospitalId"))
                .params("rows", 20)
                .params("page", 1)
                .execute(object : JacksonDialogCallback<HospitalModel>(
                        this@DoctorMoreActivity,
                        HospitalModel::class.java) {

                    override fun onSuccess(response: Response<HospitalModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            if (response.body().hospitalRecommendDoctors != null) addAll(response.body().hospitalRecommendDoctors!!)
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
