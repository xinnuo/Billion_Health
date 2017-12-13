package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.*
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.PopupWindowUtils
import kotlinx.android.synthetic.main.activity_hospital_find.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_title_search_hint.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class HospitalFindActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_qu = ArrayList<CommonData>()

    private var pos_qu = -1
    private var pos_xu = -1
    private var diseaseId = ""
    private var levels = ""
    private var types = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_find)
        setToolbarVisibility(false)
        init_title()

        hospital_find_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无医院信息！"
        search_hint_txt.text = "请输入医院名称"

        hospital_find_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(1) }
        }

        hospital_find_list.apply {
            linearLayoutManager = LinearLayoutManager(this@HospitalFindActivity)
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

            setOnTouchListener { _, _ -> return@setOnTouchListener hospital_find_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<HospitalData>(R.layout.item_find_hospital_list) { data, injector ->
                    injector.text(R.id.item_hospital_name,
                            if (data.hospitallevelName == "" && data.hospitaltypeName == "") data.hospitalName
                            else data.hospitalName + "(" + data.hospitallevelName + data.hospitaltypeName + ")")
                            .text(R.id.item_hospital_zixun, data.consultCount + "人咨询")
                            .text(R.id.item_hospital_location, data.hospitalAddress)

                            .visibility(R.id.item_hospital_length, if (data.distance == "" || data.distance == "0") View.GONE else View.VISIBLE)
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
                    if (data.distance != "" && data.distance != "0") {
                        injector.text(
                                R.id.item_hospital_length,
                                if (data.distance.toDouble() < 1000) data.distance + "m"
                                else String.format("%.1f", data.distance.toDouble() / 1000) + "km")
                    }
                }
                .attachTo(hospital_find_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<HospitalModel>(HttpIP.find_hospitalInfo)
                .tag(this@HospitalFindActivity)
                .isMultipart(true)
                .params("lng", getString("lng"))
                .params("lat", getString("lat"))
                .params("diseaseId", diseaseId)
                .params("district", if (pos_qu > -1) list_qu[pos_qu].areaId else "")
                .params("filtrate", "")
                .params("orderTp", if (pos_xu > 0) pos_xu.toString() else "")
                .params("page", pindex)
                .params("levels", levels)
                .params("types", types)
                .execute(object : JacksonDialogCallback<HospitalModel>(baseContext, HospitalModel::class.java) {

                    override fun onSuccess(response: Response<HospitalModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            if (response.body().hospitalInfos != null) addAll(response.body().hospitalInfos!!)
                            if (size > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        hospital_find_refresh.isRefreshing = false
                        isLoadingMore = false

                        ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    fun updateHouseList() {
        hospital_find_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }

    @Suppress("DEPRECATION")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_hint -> {
                startActivity(SearchActivity::class.java)
            }
            R.id.hospital_qu_ll -> {
                OkGo.post<CommonModel>(HttpIP.area_data)
                        .tag(this@HospitalFindActivity)
                        .isMultipart(true)
                        .params("areaId", getString("cityCode"))
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                hospital_qu_tv.setTextColor(resources.getColor(R.color.colorAccent))
                                hospital_qu_iv.setImageResource(R.mipmap.dc_tab_pre)

                                list_qu.clear()
                                list_qu.addAll(response.body().rows!!)
                                if (list_qu.isNotEmpty()) {
                                    PopupWindowUtils.showDatePopWindow(
                                            this@HospitalFindActivity,
                                            hospital_find_divider,
                                            pos_qu,
                                            list_qu,
                                            object : PopupWindowUtils.PopupWindowCallBack {
                                                override fun onDismiss() {
                                                    hospital_qu_tv.setTextColor(resources.getColor(R.color.black))
                                                    hospital_qu_iv.setImageResource(R.mipmap.dc_tab)
                                                }

                                                override fun doWork(position: Int, name: String?) {
                                                    pos_qu = position
                                                    hospital_qu_tv.text = name

                                                    updateHouseList()
                                                }
                                            })
                                }
                            }

                        })
            }
            R.id.hospital_type_ll -> {
                OkGo.post<CommonModel>(HttpIP.index_firstdisease_data)
                        .tag(this@HospitalFindActivity)
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                hospital_type_tv.setTextColor(resources.getColor(R.color.colorAccent))
                                hospital_type_iv.setImageResource(R.mipmap.dc_tab_pre)

                                if (response.body().diseaseData!!.isNotEmpty()) {
                                    PopupWindowUtils.showSickPopWindow(
                                            this@HospitalFindActivity,
                                            hospital_find_divider,
                                            intent.getStringExtra("name"),
                                            diseaseId,
                                            response.body().diseaseData,
                                            object : PopupWindowUtils.PopupWindowSickCallBack {

                                                override fun getSecondList(diseaseId: String, handler: Handler) {
                                                    OkGo.post<CommonModel>(HttpIP.second_disease_data)
                                                            .tag(this@HospitalFindActivity)
                                                            .isMultipart(true)
                                                            .params("diseaseId", diseaseId)
                                                            .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                                                                override fun onSuccess(response: Response<CommonModel>) {
                                                                    if (response.body().diseaseData == null) showToask("暂无二级病症分类！")

                                                                    val msg = Message()
                                                                    msg.obj = response.body().diseaseData
                                                                    msg.what = 1
                                                                    handler.sendMessage(msg)
                                                                }

                                                            })
                                                }

                                                override fun doWork(id: String, name: String, name_first: String) {
                                                    diseaseId = id
                                                    hospital_type_tv.text = name
                                                    intent.putExtra("name", name_first)

                                                    updateHouseList()
                                                }

                                                override fun onDismiss() {
                                                    hospital_type_tv.setTextColor(resources.getColor(R.color.black))
                                                    hospital_type_iv.setImageResource(R.mipmap.dc_tab)
                                                }
                                            })
                                }
                            }

                        })
            }
            R.id.hospital_xuan_ll -> {
                OkGo.post<CommonModel>(HttpIP.find_hospitalotherInfo)
                        .tag(this@HospitalFindActivity)
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                if (response.body() != null) {
                                    hospital_xuan_tv.setTextColor(resources.getColor(R.color.colorAccent))
                                    hospital_xuan_iv.setImageResource(R.mipmap.dc_tab_pre)

                                    var list_level = ArrayList<CommonData>()
                                    var list_type = ArrayList<CommonData>()

                                    if (response.body().levels != null) list_level = response.body().levels as ArrayList<CommonData>
                                    if (response.body().types != null) list_type = response.body().types as ArrayList<CommonData>

                                    PopupWindowUtils.showHospitalFilterPopWindow(
                                            this@HospitalFindActivity,
                                            hospital_find_divider,
                                            list_level,
                                            list_type,
                                            object : PopupWindowUtils.PopupWindowFilterCallBack {

                                                override fun onDismiss() {
                                                    hospital_xuan_tv.setTextColor(resources.getColor(R.color.black))
                                                    hospital_xuan_iv.setImageResource(R.mipmap.dc_tab)
                                                }

                                                override fun doWork(level: String, type: String, gender: String) {
                                                    levels = level
                                                    types = type

                                                    updateHouseList()
                                                }
                                            })
                                }
                            }

                        })
            }
            R.id.hospital_rank_ll -> {
                hospital_rank_tv.setTextColor(resources.getColor(R.color.colorAccent))
                hospital_rank_iv.setImageResource(R.mipmap.dc_tab_pre)

                PopupWindowUtils.showOrderPopWindow(
                        this@HospitalFindActivity,
                        hospital_find_divider,
                        pos_xu,
                        object : PopupWindowUtils.PopupWindowCallBack {
                            override fun onDismiss() {
                                hospital_rank_tv.setTextColor(resources.getColor(R.color.black))
                                hospital_rank_iv.setImageResource(R.mipmap.dc_tab)
                            }

                            override fun doWork(position: Int, name: String?) {
                                pos_xu = position
                                hospital_rank_tv.text = name

                                updateHouseList()
                            }
                        })
            }
        }
    }
}
