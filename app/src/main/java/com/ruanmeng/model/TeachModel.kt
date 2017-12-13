package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-08 10:26
 */
data class TeachModel(
        var sliderCourses: List<SliderData>? = ArrayList(),
        var coures: List<CommonData>? = ArrayList(),
        var courses: List<CommonData>? = ArrayList(),
        var courseComment: List<TeachData>? = ArrayList(),
        var courseDetails: CommonData? = null,
        var praise: String = "0",
        var collect: String = "0",
        var msgcode: String = "",
        var success: String = ""
) : Serializable