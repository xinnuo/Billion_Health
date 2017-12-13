package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.http.addItems
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.DoctorData
import com.ruanmeng.model.GlideApp
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.MultiGapDecoration
import com.ruanmeng.utils.PopupWindowUtils
import kotlinx.android.synthetic.main.activity_doctor_find.*
import kotlinx.android.synthetic.main.layout_empty.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class DoctorFindActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_qu = ArrayList<CommonData>()

    private var pos_qu = -1
    private var pos_xu = -1
    private var diseaseId = ""
    private var credentials = ""
    private var sex = ""
    private var ihospital = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_find)
        setToolbarVisibility(false)
        init_title()

        doctor_find_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无医生信息！"

        doctor_find_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnRefreshListener { getData(1) }
        }

        doctor_find_list.apply {
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
                    if (lastVisibleItem >= total - 1 && dy > 0) {
                        if (!isLoadingMore) {
                            isLoadingMore = true
                            getData(pageNum)
                        }
                    }
                }
            })

            setOnTouchListener { _, _ -> return@setOnTouchListener doctor_find_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<DoctorData>(R.layout.item_doctor_find_grid) { data, injector ->
                    injector.text(R.id.item_doctor_name, data.doctorName)
                            .text(R.id.item_doctor_job, data.credentialName)
                            .with<ImageView>(R.id.item_doctor_img, { view ->
                                GlideApp.with(baseContext)
                                        .load(HttpIP.BaseImg + data.userHead)
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
                .attachTo(doctor_find_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(HttpIP.about_doctor_data)
                .tag(this@DoctorFindActivity)
                .isMultipart(true)
                .params("diseaseId", diseaseId)
                .params("areaId", if (pos_qu > -1) list_qu[pos_qu].areaId else "")
                .params("PTorder", if (pos_xu > 0) pos_xu.toString() else "")
                .params("credentials", credentials)
                .params("sex", sex)
                .params("ihospital", ihospital)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            if (response.body().doctorData != null) addAll(response.body().doctorData!!)
                            if (size > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        doctor_find_refresh.isRefreshing = false
                        isLoadingMore = false

                        ll_hint.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    fun updateHouseList() {
        doctor_find_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_hint -> {
                startActivity(SearchActivity::class.java)
            }
            R.id.doctor_type_ll -> {
                OkGo.post<CommonModel>(HttpIP.index_firstdisease_data)
                        .tag(this@DoctorFindActivity)
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                @Suppress("DEPRECATION")
                                doctor_type_tv.setTextColor(resources.getColor(R.color.colorAccent))
                                doctor_type_iv.setImageResource(R.mipmap.dc_tab_pre)

                                if (response.body().diseaseData!!.isNotEmpty()) {
                                    PopupWindowUtils.showSickPopWindow(
                                            this@DoctorFindActivity,
                                            doctor_find_divider,
                                            intent.getStringExtra("name"),
                                            diseaseId,
                                            response.body().diseaseData,
                                            object : PopupWindowUtils.PopupWindowSickCallBack {

                                                override fun getSecondList(diseaseId: String, handler: Handler) {
                                                    OkGo.post<CommonModel>(HttpIP.second_disease_data)
                                                            .tag(this@DoctorFindActivity)
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
                                                    doctor_type_tv.text = name
                                                    intent.putExtra("name", name_first)

                                                    updateHouseList()
                                                }

                                                override fun onDismiss() {
                                                    @Suppress("DEPRECATION")
                                                    doctor_type_tv.setTextColor(resources.getColor(R.color.black))
                                                    doctor_type_iv.setImageResource(R.mipmap.dc_tab)
                                                }
                                            })
                                }
                            }

                        })
            }
            R.id.doctor_qu_ll -> {
                OkGo.post<CommonModel>(HttpIP.area_data)
                        .tag(this@DoctorFindActivity)
                        .isMultipart(true)
                        .params("areaId", getString("cityCode"))
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                @Suppress("DEPRECATION")
                                doctor_qu_tv.setTextColor(resources.getColor(R.color.colorAccent))
                                doctor_qu_iv.setImageResource(R.mipmap.dc_tab_pre)

                                list_qu.clear()
                                list_qu.addItems(response.body().rows)
                                if (list_qu.isNotEmpty()) {
                                    PopupWindowUtils.showDatePopWindow(
                                            this@DoctorFindActivity,
                                            doctor_find_divider,
                                            pos_qu,
                                            list_qu,
                                            object : PopupWindowUtils.PopupWindowCallBack {
                                                override fun onDismiss() {
                                                    @Suppress("DEPRECATION")
                                                    doctor_qu_tv.setTextColor(resources.getColor(R.color.black))
                                                    doctor_qu_iv.setImageResource(R.mipmap.dc_tab)
                                                }

                                                override fun doWork(position: Int, name: String?) {
                                                    pos_qu = position
                                                    doctor_qu_tv.text = name

                                                    updateHouseList()
                                                }
                                            })
                                }
                            }

                        })
            }
            R.id.doctor_xuan_ll -> {
                OkGo.post<CommonModel>(HttpIP.get_doctorcredential_data)
                        .tag(this@DoctorFindActivity)
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                if (response.body() != null && response.body().credentials != null) {
                                    @Suppress("DEPRECATION")
                                    doctor_xuan_tv.setTextColor(resources.getColor(R.color.colorAccent))
                                    doctor_xuan_iv.setImageResource(R.mipmap.dc_tab_pre)

                                    PopupWindowUtils.showDoctorFilterPopWindow(
                                            this@DoctorFindActivity,
                                            doctor_find_divider,
                                            response.body().credentials,
                                            object : PopupWindowUtils.PopupWindowFilterCallBack {
                                                override fun onDismiss() {
                                                    @Suppress("DEPRECATION")
                                                    doctor_xuan_tv.setTextColor(resources.getColor(R.color.black))
                                                    doctor_xuan_iv.setImageResource(R.mipmap.dc_tab)
                                                }

                                                override fun doWork(type: String, zi: String, gender: String) {
                                                    credentials = zi
                                                    sex = gender
                                                    ihospital = type

                                                    updateHouseList()
                                                }
                                            })
                                }
                            }

                        })
            }
            R.id.doctor_rank_ll -> {
                @Suppress("DEPRECATION")
                doctor_rank_tv.setTextColor(resources.getColor(R.color.colorAccent))
                doctor_rank_iv.setImageResource(R.mipmap.dc_tab_pre)

                PopupWindowUtils.showOrderPopWindow(
                        this@DoctorFindActivity,
                        doctor_find_divider,
                        pos_xu,
                        object : PopupWindowUtils.PopupWindowCallBack {
                            override fun onDismiss() {
                                @Suppress("DEPRECATION")
                                doctor_rank_tv.setTextColor(resources.getColor(R.color.black))
                                doctor_rank_iv.setImageResource(R.mipmap.dc_tab)
                            }

                            override fun doWork(position: Int, name: String?) {
                                pos_xu = position
                                doctor_rank_tv.text = name

                                updateHouseList()
                            }
                        })
            }
        }
    }
}
