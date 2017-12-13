package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.GlideApp
import com.ruanmeng.model.HospitalData
import com.ruanmeng.model.HospitalModel
import com.ruanmeng.share.HttpIP
import kotlinx.android.synthetic.main.activity_hospital.*
import kotlinx.android.synthetic.main.layout_empty.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class HospitalActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)
        init_title("我的医院")

        hospital_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无医院信息！"

        hospital_refresh.apply {
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(1) }
        }

        hospital_list.apply {
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

            setOnTouchListener { _, _ -> return@setOnTouchListener hospital_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<HospitalData>(R.layout.item_hospital_list) { data, injector ->
                    injector.text(R.id.item_hospital_name,
                            if (data.hospitallevelName == "" && data.hospitaltypeName == "") data.hospitalName
                            else data.hospitalName + "(" + data.hospitallevelName + data.hospitaltypeName + ")")
                            .text(R.id.item_hospital_zixun, data.consultCount + "人咨询")
                            .text(R.id.item_hospital_location, data.hospitalAddress)

                            .visibility(R.id.item_hospital_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_hospital_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<ImageView>(R.id.item_hospital_img, { view ->
                                GlideApp.with(baseContext)
                                        .load(HttpIP.BaseImg + data.hospitalHead)
                                        .placeholder(R.mipmap.not_2) // 等待时的图片
                                        .error(R.mipmap.not_2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            })

                            .clicked(R.id.item_hospital_ll, {
                                val intent = Intent(baseContext, HospitalDetailActivity::class.java)
                                intent.putExtra("hospitalId", data.hospitalId)
                                intent.putExtra("title", data.hospitalName)
                                startActivity(intent)
                            })

                            .clicked(R.id.item_hospital_collect, {
                                OkGo.post<String>(HttpIP.cancel_collect_sub)
                                        .tag(this@HospitalActivity)
                                        .isMultipart(true)
                                        .headers("token", getString("token"))
                                        .params("toCollectId", data.hospitalId)
                                        .params("collectType", "collectHospital")
                                        .execute(object : StringDialogCallback(baseContext) {

                                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                showToask(msg)

                                                list.remove(data)
                                                mAdapter.updateData(list).notifyDataSetChanged()
                                                ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                                            }

                                        })
                            })
                }
                .attachTo(hospital_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<HospitalModel>(HttpIP.my_hospital_data)
                .tag(this@HospitalActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<HospitalModel>(baseContext, HospitalModel::class.java) {

                    override fun onSuccess(response: Response<HospitalModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            if (response.body().myHospitals != null) addAll(response.body().myHospitals!!)
                            if (size > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        hospital_refresh.isRefreshing = false
                        isLoadingMore = false

                        ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }
}
