package com.ruanmeng.billion_health

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import android.widget.TextView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.FriendData
import com.ruanmeng.model.FriendsModel
import com.ruanmeng.model.GlideApp
import com.ruanmeng.share.HttpIP
import com.ruanmeng.sort.CharacterParser
import com.ruanmeng.sort.PinyinFriendComparator
import com.ruanmeng.utils.DensityUtil
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_base_search.*
import kotlinx.android.synthetic.main.activity_friends.*
import kotlinx.android.synthetic.main.layout_no_search_result.*
import net.idik.lib.slimadapter.SlimAdapter
import qdx.stickyheaderdecoration.NormalDecoration
import java.util.*

class FriendsActivity : BaseActivity() {

    private val list = ArrayList<FriendData>()
    private val list_index = ArrayList<String>()

    private var list_result = ArrayList<FriendData>()
    private var resultAdapter: SlimAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSuperContentView(R.layout.activity_base_search)
        setContentView(R.layout.activity_friends)
        init_title("好友列表")

        getData()
    }

    override fun init_title() {
        super.init_title()
        @Suppress("DEPRECATION")
        empty_view.setBackgroundColor(resources.getColor(R.color.background))
        layout_search.addTextChangedListener(this@FriendsActivity)
        layout_search_clear.setOnClickListener {
            layout_search.setText("")
            list_result.clear()
        }

        friends_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager
            val decoration = object : NormalDecoration() {
                override fun getHeaderName(pos: Int): String = list[pos].letter
            }
            @Suppress("DEPRECATION")
            decoration.setHeaderContentColor(resources.getColor(R.color.background))
            decoration.setHeaderHeight(DensityUtil.dp2px(30f))
            decoration.setTextSize(DensityUtil.sp2px(14f))
            @Suppress("DEPRECATION")
            decoration.setTextColor(resources.getColor(R.color.gray))
            addItemDecoration(decoration)
        }

        mAdapter = SlimAdapter.create()
                .register<FriendData>(R.layout.item_friends_list) { data, injector ->
                    injector.text(R.id.item_friends_name, data.name)

                            .visibility(
                                    R.id.item_friends_divider1,
                                    if (list.indexOf(data) != list.size - 1
                                            && data.letter != list[list.indexOf(data) + 1].letter)
                                        View.GONE
                                    else View.VISIBLE)

                            .with<TextView>(R.id.item_friends_status) { view ->
                                if (data.type == "1") {
                                    view.text = "医生"
                                    view.setBackgroundResource(R.drawable.rec_ova_bg_orange)
                                } else {
                                    view.text = "好友"
                                    view.setBackgroundResource(R.drawable.rec_ova_bg_green)
                                }
                            }

                            .with<RoundedImageView>(R.id.item_friends_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(HttpIP.BaseImg + data.head)
                                        .placeholder(R.mipmap.my_tx_mr_1) // 等待时的图片
                                        .error(R.mipmap.my_tx_mr_1)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_friends_ll, {
                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        data.userId,
                                        data.name,
                                        Uri.parse(HttpIP.BaseImg + data.head)))

                                RongIM.getInstance().startPrivateChat(baseContext, data.userId, data.name)
                            })
                }
                .attachTo(friends_list)

        friends_search_list.layoutManager = LinearLayoutManager(baseContext)

        resultAdapter = SlimAdapter.create()
                .register<FriendData>(R.layout.item_friends_list) { data, injector ->
                    injector.text(R.id.item_friends_name, data.name)

                            .visibility(R.id.item_friends_divider1, if (list_result.indexOf(data) == list_result.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_friends_divider2, if (list_result.indexOf(data) != list_result.size - 1) View.GONE else View.VISIBLE)

                            .with<TextView>(R.id.item_friends_status) { view ->
                                if (data.type == "1") {
                                    view.text = "医生"
                                    view.setBackgroundResource(R.drawable.rec_ova_bg_orange)
                                } else {
                                    view.text = "好友"
                                    view.setBackgroundResource(R.drawable.rec_ova_bg_green)
                                }
                            }

                            .with<RoundedImageView>(R.id.item_friends_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(HttpIP.BaseImg + data.head)
                                        .placeholder(R.mipmap.my_tx_mr_1) // 等待时的图片
                                        .error(R.mipmap.my_tx_mr_1)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_friends_ll, {
                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        data.userId,
                                        data.name,
                                        Uri.parse(HttpIP.BaseImg + data.head)))

                                RongIM.getInstance().startPrivateChat(baseContext, data.userId, data.name)
                            })
                }
                .attachTo(friends_search_list)

        val letters = arrayOf(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
        list_index.addAll(letters)
        friends_index.setIndexBarHeightRatio(0.9f)
        friends_index.indexBar.setIndexsList(list_index)
        friends_index.indexBar.setIndexChangeListener { name ->
            for (item in list) {
                if (name == item.letter) {
                    linearLayoutManager.scrollToPositionWithOffset(list.indexOf(item), 0)
                    return@setIndexChangeListener
                }
            }
        }
    }

    override fun getData() {
        OkGo.post<FriendsModel>(HttpIP.frienddata_list)
                .tag(this@FriendsActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<FriendsModel>(baseContext, FriendsModel::class.java, true) {

                    override fun onSuccess(response: Response<FriendsModel>) {
                        seperateLists(response.body().friends)

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                })
    }

    private fun seperateLists(mlist: List<FriendData>?) {
        if (mlist != null && mlist.isNotEmpty()) {
            mlist.forEach { item ->
                val letter = CharacterParser.getInstance().getSelling(item.name)
                val sortStr = letter.substring(0, 1).toUpperCase()
                if (sortStr.matches("[A-Z]".toRegex())) {
                    item.letter = sortStr
                } else {
                    item.letter = "#"
                }
            }
            Collections.sort(mlist, PinyinFriendComparator())
        }
        if (mlist != null) list.addAll(mlist)
    }

    private fun searchResult(keyword: String): ArrayList<FriendData> =
            list.filterTo(ArrayList()) { keyword in it.name }

    override fun afterTextChanged(s: Editable) {
        val keyword = s.toString()
        if (keyword.isEmpty()) {
            layout_search_clear.visibility = View.GONE
            empty_view.visibility = View.GONE
            friends_search_list.visibility = View.GONE
        } else {
            layout_search_clear.visibility = View.VISIBLE
            friends_search_list.visibility = View.VISIBLE

            list_result = searchResult(keyword)

            if (list_result.isEmpty()) {
                empty_view.visibility = View.VISIBLE
            } else {
                empty_view.visibility = View.GONE
                resultAdapter!!.updateData(list_result).notifyDataSetChanged()
            }
        }
    }
}
