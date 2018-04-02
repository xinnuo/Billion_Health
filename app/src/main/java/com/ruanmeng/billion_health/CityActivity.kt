package com.ruanmeng.billion_health

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.view.View
import com.baidu.mapapi.utils.BDLocationHelper
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.HotCityGridAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.http.addItems
import com.ruanmeng.model.CityData
import com.ruanmeng.model.CityModel
import com.ruanmeng.model.MainCityEvent
import com.ruanmeng.share.HttpIP
import com.ruanmeng.sort.PinyinComparator
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DensityUtil
import com.ruanmeng.view.CustomGridView
import kotlinx.android.synthetic.main.activity_city.*
import kotlinx.android.synthetic.main.layout_city_search.*
import kotlinx.android.synthetic.main.layout_no_search_result.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import qdx.stickyheaderdecoration.NormalDecoration
import java.util.*
import kotlin.collections.ArrayList

class CityActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_hot = ArrayList<CityData>()
    private val list_index = ArrayList<String>()
    private var list_result = ArrayList<CityData>()

    private var resultAdapter: SlimAdapter? = null
    private var lng: Double = 0.0
    private var lat: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        setToolbarVisibility(false)
        init_title()

        getData()
    }

    override fun init_title() {
        super.init_title()
        layout_search.addTextChangedListener(this@CityActivity)
        search_clear.setOnClickListener(this@CityActivity)
        search_cancel.setOnClickListener(this@CityActivity)

        city_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager
            val decoration = object : NormalDecoration() {
                override fun getHeaderName(pos: Int): String = when (pos) {
                    0 -> "定位"
                    1 -> "热门"
                    else -> (list[pos] as CityData).firstLetter
                }
            }
            @Suppress("DEPRECATION")
            decoration.setHeaderContentColor(resources.getColor(R.color.divider))
            decoration.setHeaderHeight(DensityUtil.dp2px(30f))
            decoration.setTextSize(DensityUtil.sp2px(14f))
            @Suppress("DEPRECATION")
            decoration.setTextColor(resources.getColor(R.color.gray))
            addItemDecoration(decoration)
        }

        city_search_list.layoutManager = LinearLayoutManager(baseContext)

        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.item_city_header) { data, injector ->
                    injector.text(R.id.located_city, data)
                            .clicked(R.id.layout_locate, {
                                if (data == "正在定位...") startLocation()
                                else getCityCode()
                            })
                }
                .register<Int>(R.layout.item_hot_grid) { _, injector ->
                    injector.with<CustomGridView>(R.id.hot_grid, { view ->
                        view.adapter = HotCityGridAdapter(baseContext, list_hot)
                        view.setOnItemClickListener { _, _, position, _ ->
                            lng = list_hot[position].lng.toDouble()
                            lat = list_hot[position].lat.toDouble()
                            getCityCode()
                        }
                    })
                }
                .register<CityData>(R.layout.item_city_list) { data, injector ->
                    injector.text(R.id.item_city_name, data.areaName)
                            .visibility(
                                    R.id.item_city_divider,
                                    if (list.indexOf(data) != list.size - 1
                                            && data.firstLetter != (list[list.indexOf(data) + 1] as CityData).firstLetter)
                                        View.GONE
                                    else View.VISIBLE)
                            .clicked(R.id.item_city_name, {
                                lng = data.lng.toDouble()
                                lat = data.lat.toDouble()
                                getCityCode()
                            })
                }
                .attachTo(city_list)

        resultAdapter = SlimAdapter.create()
                .register<CityData>(R.layout.item_city_list) { data, injector ->
                    injector.text(R.id.item_city_name, data.areaName)
                            .clicked(R.id.item_city_name, {
                                lng = data.lng.toDouble()
                                lat = data.lat.toDouble()
                                getCityCode()
                            })
                }
                .attachTo(city_search_list)
    }

    private fun startLocation() {
        BDLocationHelper.getInstance(this@CityActivity).removeCode(1)
        BDLocationHelper.getInstance(this@CityActivity).startLocation(2) { location, codes ->
            if (location != null && 2 in codes) {
                lng = location.longitude
                lat = location.latitude

                list[0] = location.city.replace("市", "")
                mAdapter.updateData(list).notifyDataSetChanged()
            } else {
                list[0] = "定位失败"
                mAdapter.updateData(list).notifyDataSetChanged()
            }
        }

        /*AMapLocationHelper.getInstance(this@CityActivity).removeCode(1)
        AMapLocationHelper.getInstance(this@CityActivity).startLocation(2) { location, isSuccessed, codes ->
            if (isSuccessed && 2 in codes) {
                lng = location.longitude
                lat = location.latitude

                list[0] = location.city.replace("市", "")
                mAdapter.updateData(list).notifyDataSetChanged()
            } else {
                list[0] = "定位失败"
                mAdapter.updateData(list).notifyDataSetChanged()
            }
        }*/
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search_clear -> {
                layout_search.setText("")
                list_result.clear()
            }
            R.id.search_cancel -> onBackPressed()
        }
    }

    override fun getData() {
        OkGo.post<CityModel>(HttpIP.city_data)
                .tag(this@CityActivity)
                .execute(object : JacksonDialogCallback<CityModel>(baseContext, CityModel::class.java, true) {

                    override fun onSuccess(response: Response<CityModel>) {
                        seperateLists(response.body().citys)
                        list_hot.addItems(response.body().hots)

                        for (item in list) {
                            when (item) {
                                is String -> list_index.add("定位")
                                is Int -> list_index.add("热门")
                                is CityData -> {
                                    if (!list_index.contains(item.firstLetter))
                                        list_index.add(item.firstLetter)
                                }
                            }
                        }
                        index_layout.setIndexBarHeightRatio(0.9f)
                        index_layout.indexBar.setIndexsList(list_index)
                        index_layout.indexBar.setIndexChangeListener { name ->

                            if (list_index.indexOf(name) == 0 || list_index.indexOf(name) == 1) {
                                linearLayoutManager.scrollToPositionWithOffset(list_index.indexOf(name), 0)
                                return@setIndexChangeListener
                            }

                            for (i in 2 until list.size) {
                                if (name == (list[i] as CityData).firstLetter) {
                                    linearLayoutManager.scrollToPositionWithOffset(i, 0)
                                    return@setIndexChangeListener
                                }
                            }
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                        startLocation()
                    }

                })
    }

    private fun getCityCode() {
        OkGo.post<String>(HttpIP.address_code)
                .tag(this@CityActivity)
                .isMultipart(true)
                .params("lng", lng.toString())
                .params("lat", lat.toString())
                .execute(object : StringDialogCallback(baseContext, false) {
                    /*{
                        "city": "865510",
                        "cityName": "郑州",
                        "district": "86551016",
                        "districtName": "中牟县",
                        "msgcode": 100,
                        "province": "8655",
                        "provinceName": "河南",
                        "success": true
                    }*/
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body())
                        putString("provinceCode", obj.getString("province"))
                        putString("cityCode", obj.getString("city"))
                        putString("districtCode", obj.getString("district"))

                        putString("province", obj.getString("provinceName"))
                        putString("city", obj.getString("cityName"))
                        putString("district", obj.getString("districtName"))

                        putString("lng", lng.toString())
                        putString("lat", lat.toString())

                        EventBus.getDefault().post(MainCityEvent(getString("cityCode"), getString("city")))

                        ActivityStack.getScreenManager().popActivities(this@CityActivity::class.java)
                    }

                })
    }

    private fun seperateLists(mlist: List<CityData>?) {
        if (mlist != null && mlist.isNotEmpty()) Collections.sort(mlist, PinyinComparator())
        list.apply {
            add("正在定位...")
            add(1)
            if (mlist != null) addAll(mlist)
        }
    }

    private fun searchCity(keyword: String): ArrayList<CityData> =
            (2 until list.size)
                    .filter { keyword in (list[it] as CityData).areaName }
                    .mapTo(ArrayList()) { list[it] as CityData }

    override fun afterTextChanged(s: Editable) {
        val keyword = s.toString()
        if (TextUtils.isEmpty(keyword)) {
            search_clear.visibility = View.GONE
            empty_view.visibility = View.GONE
            city_search_list.visibility = View.GONE
        } else {
            search_clear.visibility = View.VISIBLE
            city_search_list.visibility = View.VISIBLE
            list_result = searchCity(keyword)
            if (list_result.isEmpty()) {
                empty_view.visibility = View.VISIBLE
            } else {
                empty_view.visibility = View.GONE
                resultAdapter!!.updateData(list_result).notifyDataSetChanged()
            }
        }
    }
}
