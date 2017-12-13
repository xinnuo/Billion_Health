package com.ruanmeng.billion_health

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.HospitalModel
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_book.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class BookActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    private var gender = ""
    private var diseaseId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        init_title("预约-" + (intent.getStringExtra("name") ?: ""))
    }

    override fun init_title() {
        super.init_title()
        book_submit.apply {
            setBackgroundResource(R.drawable.rec_bg_d5d5d5)
            isClickable = false
        }

        book_name.addTextChangedListener(this@BookActivity)
        book_tel.addTextChangedListener(this@BookActivity)
        tv_type.addTextChangedListener(this@BookActivity)
        tv_time.addTextChangedListener(this@BookActivity)
        book_memo.addTextChangedListener(this@BookActivity)
        rg_check.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_check1 -> gender = "0"
                R.id.rb_check2 -> gender = "1"
            }
        }

        if (getString("sex") == "0") rb_check1.isChecked = true
        else rb_check2.isChecked = true
        book_tel.setText(getString("mobile", ""))
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.book_type -> {
                if (list.size > 0) showSheetDialog()
                else {
                    OkGo.post<HospitalModel>(HttpIP.hospital_consultancyScope_data)
                            .tag(this@BookActivity)
                            .isMultipart(true)
                            .params("hospitalId", intent.getStringExtra("hospitalId"))
                            .params("level", 2)
                            .execute(object : JacksonDialogCallback<HospitalModel>(
                                    baseContext,
                                    HospitalModel::class.java,
                                    true) {

                                override fun onSuccess(response: Response<HospitalModel>) {
                                    if (response.body() != null && response.body().hospitalConsultancyScope != null) {
                                        list.clear()
                                        list.addAll(response.body().hospitalConsultancyScope!!)

                                        if (list.size > 0) showSheetDialog()
                                    }
                                }
                            })
                }
            }
            R.id.book_time -> {
                val year_now = Calendar.getInstance().get(Calendar.YEAR)

                DialogHelper.showDateDialog(this@BookActivity,
                        year_now,
                        year_now + 1,
                        5,
                        "选择预约时间",
                        true,
                        true, { _, _, _, _, _, date ->
                    tv_time.text = date
                })
            }
            R.id.book_submit -> {
                OkGo.post<String>(HttpIP.reserve_sub)
                        .tag(this@BookActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("reserveName", book_name.text.toString())
                        .params("mobile", book_tel.text.toString())
                        .params("sex", gender)
                        .params("diseaseId", diseaseId)
                        .params("reserveTime", tv_time.text.toString())
                        .params("remark", book_memo.text.toString())
                        .params("hospitalId", intent.getStringExtra("hospitalId"))
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "恭喜您预约成功!",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                showToask(msg)

                                ActivityStack.getScreenManager().popActivity()
                            }

                        })
            }
        }
    }

    private fun showSheetDialog() {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_book_type, null) as View
        val tv_ok = view.findViewById(R.id.dialog_book_ok) as TextView
        val tv_cancel = view.findViewById(R.id.dialog_book_cancle) as TextView
        val recyclerView = view.findViewById(R.id.dialog_book_list) as RecyclerView
        recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_city_list, { data, injector ->
                        injector.text(R.id.item_city_name, data.diseaseName)
                                .with<TextView>(R.id.item_city_name, { view ->
                                    view.gravity = Gravity.CENTER
                                    @Suppress("DEPRECATION")
                                    view.setTextColor(resources.getColor(if (data.isChecked) R.color.colorAccent else R.color.black))
                                })
                                .clicked(R.id.item_city_name, {
                                    for (item in list) item.isChecked = item == data
                                    (recyclerView.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                                })
                    })
                    .attachTo(recyclerView)
        }

        (recyclerView.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()

        val dialog = BottomSheetDialog(baseContext)
        dialog.setContentView(view)
        dialog.show()

        tv_ok.setOnClickListener {
            dialog.dismiss()
            for (item in list) {
                if (item.isChecked) {
                    tv_type.text = item.diseaseName
                    diseaseId = item.diseaseId
                }
            }
        }
        tv_cancel.setOnClickListener { dialog.dismiss() }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (book_name.text.toString() != ""
                && book_tel.text.toString() != ""
                && tv_type.text.toString() != ""
                && tv_time.text.toString() != ""
                && book_memo.text.toString() != "") {
            book_submit.setBackgroundResource(R.drawable.rec_bg_green)
            book_submit.isClickable = true
        } else {
            book_submit.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
            book_submit.isClickable = false
        }
    }
}
