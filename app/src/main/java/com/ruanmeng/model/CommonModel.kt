package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-08 10:26
 */
data class CommonModel(
        var myCourses: List<CommonData> ?= ArrayList(),
        var doctorData: List<DoctorData> ?= ArrayList(),
        var rows: List<CommonData> ?= ArrayList(),
        var diseaseData: List<CommonData> ?= ArrayList(),
        var help: List<CommonData> ?= ArrayList(),
        var users: List<CommonData> ?= ArrayList(),
        var credentials: List<CommonData>? = ArrayList(),
        var levels: List<CommonData>? = ArrayList(),
        var types: List<CommonData>? = ArrayList(),
        var psychologys: List<CommonData>? = ArrayList(),
        var msgcode: String = "",
        var msg: String = "",
        var success: String = ""
): Serializable