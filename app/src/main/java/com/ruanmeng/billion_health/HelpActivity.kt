package com.ruanmeng.billion_health

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.HttpIP
import kotlinx.android.synthetic.main.activity_help.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class HelpActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        init_title("帮助中心")

        getData()
    }

    override fun init_title() {
        super.init_title()
        help_list.apply {
            layoutManager = LinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_help_list) { data, injector ->
                        injector.text(R.id.item_help_title, data.dataItemDetailsVal)
                                .text(R.id.item_help_content, data.content)

                                .clicked(R.id.item_help_ll, {
                                    val intent = Intent(baseContext, WebActivity::class.java)
                                    intent.putExtra("name", "帮助详情")
                                    intent.putExtra("htmlKey", data.htmlKey)
                                    startActivity(intent)
                                })
                    }
                    .attachTo(help_list)
        }
    }

    override fun getData() {
        OkGo.post<CommonModel>(HttpIP.help_list)
                .tag(this@HelpActivity)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        if (response.body().help != null) list.addAll(response.body().help!!)
                        (help_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                    }

                })
    }
}
