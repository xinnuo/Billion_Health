package com.ruanmeng.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.billion_health.R
import kotlinx.android.synthetic.main.fragment_register_third.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterThirdFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_register_third, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_pwd.addTextChangedListener(this)
        bt_next3.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
        bt_next3.isClickable = false
    }

    fun getPassword() = et_pwd.text.toString()

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_pwd.text.toString())) {
            bt_next3.setBackgroundResource(R.drawable.rec_bg_green)
            bt_next3.isClickable = true
        } else {
            bt_next3.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
            bt_next3.isClickable = false
        }
    }
}