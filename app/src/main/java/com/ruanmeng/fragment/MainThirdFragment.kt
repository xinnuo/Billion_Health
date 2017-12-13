package com.ruanmeng.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.billion_health.FriendsActivity
import com.ruanmeng.billion_health.R
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.HttpIP
import io.rong.imkit.RongIM
import io.rong.imkit.fragment.ConversationListFragment
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.layout_title_third.*

/**
 * A simple [Fragment] subclass.
 */
class MainThirdFragment : BaseFragment() {

    private var list: List<Conversation>? = null
    private var list_user = ArrayList<CommonData>()

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_third, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_nav_right.setOnClickListener { startActivity(FriendsActivity::class.java) }

        enterFragment()

        list = RongIM.getInstance().conversationList
        // if (list != null && list!!.isNotEmpty()) getData()
    }

    override fun getData() {
        val sb = StringBuilder()
        list!!.forEach { item -> sb.append(item.targetId).append(",") }

        OkGo.post<CommonModel>(HttpIP.userlist_data)
                .tag(this@MainThirdFragment)
                .isMultipart(true)
                .params("userIds", sb.toString())
                .execute(object : JacksonDialogCallback<CommonModel>(activity, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_user.apply {
                            clear()
                            if (response.body().users != null) addAll(response.body().users!!)
                        }

                        list_user.forEach { item ->
                            RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                    item.userInfoId,
                                    item.nickName,
                                    Uri.parse(HttpIP.BaseImg + item.userHead)))
                        }
                    }
                })
    }

    /**
     * 加载 会话列表 ConversationListFragment
     */
    private fun enterFragment() {
        val fragment = childFragmentManager.findFragmentById(R.id.conversationlist) as ConversationListFragment
        val uri = Uri.parse("rong://" + activity.applicationInfo.packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")    //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")       //设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false") //设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")     //设置系统会话非聚合显示
                .build()
        fragment.uri = uri
    }
}
