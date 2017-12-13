package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-08 10:26
 */
class HomeModel : Serializable {
    var imgs: List<CommonData>? = ArrayList()
    var homepageMsg: CommonData? = null
    var courseNum: String? = ""
    var focueMeNum: String? = ""
    var focus: String = "0"
    var meFocueNum: String? = ""
    var msgcode: String = ""
    var success: String = ""
}