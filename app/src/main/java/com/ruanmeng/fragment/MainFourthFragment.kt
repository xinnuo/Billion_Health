package com.ruanmeng.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.billion_health.*
import com.ruanmeng.model.GlideApp
import com.ruanmeng.share.HttpIP
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.fragment_main_fourth.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class MainFourthFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_fourth, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun onStart() {
        super.onStart()

        if (getBoolean("isLogin")) {
            fourth_city.text = getString("city")
            fourth_qu.text = getString("district")

            getData()
        }
    }

    override fun getData() {
        OkGo.post<String>(HttpIP.user_msg_data)
                .tag(this@MainFourthFragment)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(this@MainFourthFragment.activity, false) {
                    /*{
                        "msgcode": 100,
                        "success": true,
                        "userMsg": {
                            "age": 11,
                            "areaName city": "",
                            "areaName district": "",
                            "areaName province": "",
                            "city": "",
                            "district": "",
                            "isPass": 0,
                            "mobile": "",
                            "nickName": "18625879630",
                            "province": "",
                            "sex": 1,
                            "sign": "说点什么好呢～～～",
                            "userInfoId": "0A9401AF2E8D49FB9C631C47FC277163",
                            "userhead": "upload/userhead/0A9401AF2E8D49FB9C631C47FC277163/B62595966230494389050B1A54611D69.png"
                        }
                    }*/
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body()).getJSONObject("userMsg")

                        putString("age", obj.getString("age"))
                        putString("nickName", obj.getString("nickName"))
                        putString("sex", obj.getString("sex"))
                        putString("sign", obj.getString("sign"))
                        putString("userInfoId", obj.getString("userInfoId"))
                        putString("userhead", obj.getString("userhead"))
                        putString("isPass", obj.getString("isPass"))

                        fourth_name.text = getString("nickName") ?: getString("mobile")
                        fourth_age.text = (getString("age") ?: "0") + "岁"
                        fourth_sign.text = "个性签名：" + getString("sign")
                        fourth_gender.setImageResource(if (getString("sex") == "0") R.mipmap.my_gender_f else R.mipmap.my_gender_m)
                        if (fourth_img.getTag(R.id.fourth_img) == null) {
                            GlideApp.with(this@MainFourthFragment.activity)
                                    .load(HttpIP.BaseImg + getString("userhead"))
                                    .placeholder(R.mipmap.my_tx_mr)
                                    .error(R.mipmap.my_tx_mr)
                                    .dontAnimate()
                                    .into(fourth_img)

                            fourth_img.setTag(R.id.fourth_img, getString("userhead"))
                        } else {
                            if (fourth_img.getTag(R.id.fourth_img) != getString("userhead")) {
                                GlideApp.with(this@MainFourthFragment.activity)
                                        .load(HttpIP.BaseImg + getString("userhead"))
                                        .placeholder(R.mipmap.my_tx_mr)
                                        .error(R.mipmap.my_tx_mr)
                                        .dontAnimate()
                                        .into(fourth_img)

                                fourth_img.setTag(R.id.fourth_img, getString("userhead"))
                            }
                        }

                        RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                getString("token"),
                                getString("nickName"),
                                Uri.parse(HttpIP.BaseImg + getString("userhead"))))
                    }

                })
    }

    override fun init_title() {
        fourth_info.setOnClickListener(this@MainFourthFragment)
        fourth_friend.setOnClickListener(this@MainFourthFragment)
        fourth_tai.setOnClickListener(this@MainFourthFragment)
        fourth_teach.setOnClickListener(this@MainFourthFragment)
        fourth_doctor.setOnClickListener(this@MainFourthFragment)
        fourth_hospital.setOnClickListener(this@MainFourthFragment)
        fourth_test.setOnClickListener(this@MainFourthFragment)
        fourth_real.setOnClickListener(this@MainFourthFragment)
        fourth_setting.setOnClickListener(this@MainFourthFragment)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fourth_info -> startActivity(InfoActivity::class.java)
            R.id.fourth_friend -> startActivity(WatchActivity::class.java)
            R.id.fourth_tai -> {
                val intent = Intent(activity, StateActivity::class.java)
                intent.putExtra("title", "我的动态")
                startActivity(intent)
            }
            R.id.fourth_teach -> startActivity(TeachActivity::class.java)
            R.id.fourth_doctor -> startActivity(DoctorActivity::class.java)
            R.id.fourth_hospital -> startActivity(HospitalActivity::class.java)
            R.id.fourth_test -> startActivity(TestActivity::class.java)
            R.id.fourth_real -> {
                when (getString("isPass")) {
                    "-1" -> {
                        showToask("实名认证审核失败，请重新上传！")
                        startActivity(RealActivity::class.java)
                    }
                    "0" -> showToask("实名认证信息正在审核中！")
                    "1" -> showToask("已通过实名认证！")
                    else -> startActivity(RealActivity::class.java)
                }
            }
            R.id.fourth_setting -> startActivity(SettingActivity::class.java)
        }
    }
}
