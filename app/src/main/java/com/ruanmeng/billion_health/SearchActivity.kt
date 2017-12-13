package com.ruanmeng.billion_health

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.SearchData
import com.ruanmeng.model.SearchModel
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.Tools
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_no_search_result.*
import kotlinx.android.synthetic.main.layout_title_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.json.JSONArray

class SearchActivity : BaseActivity() {

    private var list_old = ArrayList<String>()
    private var list = ArrayList<SearchData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        super.init_title()
        updateHistory()

        search_history.setOnTagClickListener { _, position, _ ->

            search_hint_txt.setText(list_old[position])
            search_hint_txt.setSelection(search_hint_txt.text.length)
            pageNum = 1
            getData(list_old[position], pageNum, true)

            return@setOnTagClickListener true
        }

        search_list.apply {
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
                            getData(search_hint_txt.text.toString().trim(), pageNum, false)
                        }
                    }
                }
            })

            adapter = SlimAdapter.create()
                    .register<SearchData>(R.layout.item_city_list) { data, injector ->
                        injector.text(R.id.item_city_name, data.name)
                                .clicked(R.id.item_city_name, {
                                    when (data.type) {
                                        "1" -> {
                                            val intent = Intent(baseContext, HospitalDetailActivity::class.java)
                                            intent.putExtra("hospitalId", data.id)
                                            intent.putExtra("title", data.name)
                                            startActivity(intent)
                                        }
                                        "2" -> {
                                            val intent = Intent(baseContext, DoctorDetailActivity::class.java)
                                            intent.putExtra("doctorId", data.id)
                                            startActivity(intent)
                                        }
                                        "3" -> {
                                            if (!getBoolean("isLogin")) {
                                                startActivity(LoginActivity::class.java)
                                                return@clicked
                                            }
                                            val intent = Intent(baseContext, TeachDetailActivity::class.java)
                                            intent.putExtra("courseId", data.id)
                                            startActivity(intent)
                                        }
                                    }
                                })
                    }
                    .attachTo(search_list)
        }

        search_hint_txt.addTextChangedListener(this@SearchActivity)
        search_hint_txt.setOnEditorActionListener { v, actionId, event ->
            /*判断是否是“SEARCH”键*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                /*隐藏软键盘*/
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)

                if (search_hint_txt.text.toString().trim() != "") {
                    val array = if (getString("history") == "") JSONArray() else JSONArray(getString("history"))
                    if (!array.toString().contains(search_hint_txt.text.toString().trim()))
                        array.put(search_hint_txt.text.toString().trim())
                    putString("history", array.toString())

                    list.clear()
                    (search_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()

                    pageNum = 1
                    getData(search_hint_txt.text.toString().trim(), pageNum, true)
                }
            }

            return@setOnEditorActionListener false
        }
    }

    private fun updateHistory() {
        if (getString("history") != "")
            list_old = Tools.jsonArrayToList(getString("history"))

        search_history.adapter = object : TagAdapter<String>(list_old) {

            override fun getView(parent: FlowLayout, position: Int, t: String): View {
                val tv_content = LayoutInflater.from(baseContext).inflate(R.layout.item_search_history, search_history, false) as TextView
                tv_content.text = t
                return tv_content
            }

        }
    }

    private fun getData(key: String, pindex: Int, isRefresh: Boolean) {
        OkGo.post<SearchModel>(HttpIP.indexsearch_sub)
                .tag(this@SearchActivity)
                .isMultipart(true)
                .params("keyWord", key)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<SearchModel>(baseContext, SearchModel::class.java, isRefresh) {

                    override fun onSuccess(response: Response<SearchModel>) {
                        search_ll.visibility = View.GONE
                        empty_view.visibility = View.GONE

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            if (response.body().info != null) addAll(response.body().info!!)
                            if (size > 0) pageNum++
                        }

                        (search_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        isLoadingMore = false

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_del -> {
                list_old.clear()
                putString("history", "")
                updateHistory()
            }
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString().trim() == "") {
            updateHistory()
            search_ll.visibility = View.VISIBLE
            empty_view.visibility = View.GONE
        }
    }
}
