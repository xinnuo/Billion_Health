package com.ruanmeng.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment

import com.ruanmeng.billion_health.R
import kotlinx.android.synthetic.main.fragment_register_first.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFirstFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_register_first, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null && arguments.getBoolean("isGone", false)) ll_login.visibility = View.GONE

        et_name.addTextChangedListener(this)
        bt_next.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
        bt_next.isClickable = false

        del_clear.setOnClickListener { et_name.setText("") }
    }

    fun getMobile() : String = et_name.text.toString()

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_name.text.toString())) {
            bt_next.setBackgroundResource(R.drawable.rec_bg_green)
            bt_next.isClickable = true
        } else {
            bt_next.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
            bt_next.isClickable = false
        }
    }
}
