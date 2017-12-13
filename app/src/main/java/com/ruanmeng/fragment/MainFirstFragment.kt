package com.ruanmeng.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.baidu.location.BDLocation
import com.baidu.mapapi.utils.BDLocationHelper
import com.jude.rollviewpager.RollPagerView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.billion_health.*
import com.ruanmeng.http.addItems
import com.ruanmeng.model.*
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.HtmlUtil
import com.ruanmeng.utils.MultiGapDecoration
import com.ruanmeng.view.SwitcherTextView
import kotlinx.android.synthetic.main.fragment_main_first.*
import kotlinx.android.synthetic.main.layout_title_first.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

/**
 * A simple [Fragment] subclass.
 */
@RuntimePermissions
class MainFirstFragment : BaseFragment() {

    private val list = ArrayList<Any>()
    private val list_notice = ArrayList<CommonData>()

    private var img_Ad = ""
    private var img_Title = ""

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_first, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        EventBus.getDefault().register(this@MainFirstFragment)

        first_refresh.isRefreshing = true
        getData()
    }

    override fun onStart() {
        super.onStart()

        if (getString("city", "") != "") first_city.text = getString("city")
        else MainFirstFragmentPermissionsDispatcher.needsPermissionWithCheck(this@MainFirstFragment)
    }

    override fun init_title() {
        first_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))

            setOnRefreshListener { getData() }
        }

        first_list.apply {
            gridLayoutManager = GridLayoutManager(activity, 3)
            layoutManager = gridLayoutManager

            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = when {
                    mAdapter.getItem(position) is HospitalData -> 1
                    mAdapter.getItem(position) is DoctorData -> 1
                    else -> 3
                }
            }

            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })

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

            setOnTouchListener { _, _ -> return@setOnTouchListener first_refresh.isRefreshing }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.header_first) { data, injector ->
                    injector.with<RollPagerView>(R.id.first_banner, { view ->
                        val mLoopAdapter = LoopAdapter(this@MainFirstFragment.activity, view)
                        view.apply {
                            setAdapter(mLoopAdapter)
                            setOnItemClickListener { position ->
                                //轮播图点击事件
                            }
                        }

                        val imgs = ArrayList<String>()
                        data.slider.mapTo(imgs) { it.sliderImg }

                        mLoopAdapter.setImgs(imgs)
                    })
                            .with<SwitcherTextView>(R.id.first_notice, { view ->
                                if (list_notice.size > 0) {
                                    view.setData(list_notice, { position ->
                                        val intent = Intent(activity, WebActivity::class.java)
                                        intent.putExtra("name", "公告详情")
                                        intent.putExtra("newsId", list_notice[position].newsId)
                                        startActivity(intent)
                                    }, view)
                                }
                            })
                }
                .register<String>(R.layout.item_first_divder) { data, injector ->
                    when (data) {
                        "入驻医院" -> {
                            injector.text(R.id.first_hint, "找医院")
                                    .gone(R.id.first_ad)

                                    .clicked(R.id.first_type_ll) { startActivity(HospitalFindActivity::class.java) }

                                    .with<TextView>(R.id.first_type, { view ->
                                        view.text = data
                                        @Suppress("DEPRECATION")
                                        val nav_left = resources.getDrawable(R.mipmap.home_lab01)
                                        nav_left.setBounds(0, 0, nav_left.minimumWidth, nav_left.minimumHeight)
                                        view.setCompoundDrawables(nav_left, null, null, null)
                                    })
                        }
                        "热门推荐" -> {
                            injector.text(R.id.first_hint, "更多")
                                    .gone(R.id.first_ad)

                                    .clicked(R.id.first_type_ll) { startActivity(DoctorFindActivity::class.java) }

                                    .with<TextView>(R.id.first_type, { view ->
                                        view.text = data
                                        @Suppress("DEPRECATION")
                                        val nav_left = resources.getDrawable(R.mipmap.home_lab02)
                                        nav_left.setBounds(0, 0, nav_left.minimumWidth, nav_left.minimumHeight)
                                        view.setCompoundDrawables(nav_left, null, null, null)
                                    })
                        }
                        "健康讲堂" -> {
                            injector.text(R.id.first_hint, "更多")
                                    .visible(R.id.first_ad)

                                    .clicked(R.id.first_type_ll) { startActivity(TeachFindActivity::class.java) }

                                    .clicked(R.id.firt_image) {
                                        val intent = Intent(activity, WebActivity::class.java)
                                        intent.putExtra("name", "招商中心")
                                        startActivity(intent)
                                    }

                                    .with<ImageView>(R.id.firt_image, { view ->
                                        if (img_Ad.isNotEmpty()) {
                                            GlideApp.with(this@MainFirstFragment)
                                                    .load(HttpIP.BaseImg + img_Ad)
                                                    .centerCrop()
                                                    .dontAnimate()
                                                    .into(view)
                                        }
                                    })

                                    .with<TextView>(R.id.first_type, { view ->
                                        view.text = data
                                        @Suppress("DEPRECATION")
                                        val nav_left = resources.getDrawable(R.mipmap.home_lab03)
                                        nav_left.setBounds(0, 0, nav_left.minimumWidth, nav_left.minimumHeight)
                                        view.setCompoundDrawables(nav_left, null, null, null)
                                    })
                        }
                    }
                }
                .register<HospitalData>(R.layout.item_hospital_grid) { data, injector ->
                    injector.text(R.id.item_hospital_name, data.hospitalName)
                            .text(R.id.item_hospital_info, data.hospitalSynopsis)
                            .with<ImageView>(R.id.item_hospital_img, { view ->
                                GlideApp.with(this@MainFirstFragment)
                                        .load(HttpIP.BaseImg + data.hospitalHead)
                                        .placeholder(R.mipmap.not_2) // 等待时的图片
                                        .error(R.mipmap.not_2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            })
                            .clicked(R.id.item_hospital_img, {
                                val intent = Intent(baseContext, HospitalDetailActivity::class.java)
                                intent.putExtra("hospitalId", data.hospitalId)
                                intent.putExtra("title", data.hospitalName)
                                startActivity(intent)
                            })
                }
                .register<DoctorData>(R.layout.item_doctor_grid) { data, injector ->
                    injector.text(R.id.item_doctor_name, data.doctorName)
                            .text(R.id.item_doctor_job, data.credentialName)
                            .visibility(R.id.item_doctor_watch, View.GONE)
                            .with<ImageView>(R.id.item_doctor_img, { view ->
                                GlideApp.with(this@MainFirstFragment)
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
                .register<HallData>(R.layout.item_teach_list) { data, injector ->
                    injector.text(R.id.item_teach_title, data.title)
                            .text(R.id.item_teach_read, data.readCount + "人已阅读")

                            .visibility(R.id.item_teach_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_teach_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_teach_divider3, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<TextView>(R.id.item_teach_content, { view ->
                                Thread {
                                    val str = HtmlUtil.delHTMLTag(data.content) //耗时操作
                                    activity.runOnUiThread { view.text = str }
                                }.start()
                            })

                            .with<ImageView>(R.id.item_teach_img, { view ->
                                GlideApp.with(this@MainFirstFragment)
                                        .load(HttpIP.BaseImg + data.imgHead)
                                        .placeholder(R.mipmap.not_2) // 等待时的图片
                                        .error(R.mipmap.not_2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            })

                            .clicked(R.id.item_teach_11, {
                                if (!getBoolean("isLogin")) {
                                    startActivity(LoginActivity::class.java)
                                    return@clicked
                                }
                                val intent = Intent(activity, TeachDetailActivity::class.java)
                                intent.putExtra("courseId", data.courseId)
                                startActivity(intent)
                            })
                }
                .attachTo(first_list)
    }

    override fun getData() {
        OkGo.post<FirstModel>(HttpIP.index_data)
                .tag(this@MainFirstFragment)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheKey(HttpIP.index_data)
                .execute(object : JacksonDialogCallback<FirstModel>(this@MainFirstFragment.activity, FirstModel::class.java) {

                    override fun onSuccess(response: Response<FirstModel>) {
                        list.clear()
                        list.add(CommonData().apply { if (response.body().slider != null) slider = response.body().slider!! })
                        list.add("入驻医院")
                        list.addItems(response.body().enterHospital)
                        list.add("热门推荐")
                        list.addItems(response.body().famousDoctorsData)
                        list.add("健康讲堂")
                        list.addItems(response.body().coures)

                        if (response.body().zszx != null) {
                            img_Ad = response.body().zszx!![0].sliderImg
                            img_Title = response.body().zszx!![0].title
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()

                        list_notice.apply {
                            clear()
                            addItems(response.body().news)
                        }
                    }

                    override fun onFinish() {
                        super.onFinish()
                        first_refresh.isRefreshing = false
                    }

                })
    }

    private fun getCityCode(location: BDLocation) {
        OkGo.post<String>(HttpIP.address_code)
                .tag(this@MainFirstFragment)
                .isMultipart(true)
                .params("lng", location.longitude.toString())
                .params("lat", location.latitude.toString())
                .execute(object : StringDialogCallback(activity, false) {
                    /*{
                        "city": "865510",
                        "district": "86551016",
                        "msgcode": 100,
                        "province": "8655",
                        "success": true
                    }*/
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body())
                        putString("provinceCode", obj.getString("province"))
                        putString("cityCode", obj.getString("city"))
                        putString("districtCode", obj.getString("district"))

                        putString("province", location.province.replace("省", ""))
                        putString("city", location.city.replace("市", ""))
                        putString("district", location.district)

                        putString("lng", location.longitude.toString())
                        putString("lat", location.latitude.toString())
                    }

                })
    }

    @Subscribe
    fun onMessageEvent(event: MainCityEvent) {
        first_city.text = event.name
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this@MainFirstFragment)
        super.onDestroy()
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun needsPermission() {
        BDLocationHelper.getInstance(activity).startLocation(1) { location, codes ->
            if (location != null && 1 in codes) {
                first_city.text = location.city.replace("市", "")
                getCityCode(location)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MainFirstFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun permissionDenied() {
        showToask("请求权限被拒绝")
    }
}
