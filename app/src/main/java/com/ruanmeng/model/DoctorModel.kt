package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-08 10:26
 */
data class DoctorModel(
        var doctorData: DoctorData? = null,
        var myDoctors: List<DoctorData>? = ArrayList(),
        var doctorMien: List<DoctorData>? = ArrayList(),
        var doctorMsg: List<CommonData>? = ArrayList(),
        var focus: String = "",
        var msgcode: String = "",
        var success: String = ""
) : Serializable